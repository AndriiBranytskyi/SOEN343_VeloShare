package com.veloshare.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class bmsService {

    private Map<String, Trip> activeTrips = new HashMap<>();
    private Map<String, Reservation> activeReservations = new HashMap<>();
    private Map<String, Station> stations = new HashMap<>();
    private TripFactory tripFactory = new TripFactory();

    public void loadConfig(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line.trim());
            }
            String content = jsonContent.toString();

            // find stations array bounds
            int stationsKey = content.indexOf("\"stations\"");
            if (stationsKey < 0) {
                throw new IllegalStateException("Invalid JSON: missing \"stations\"");
            }
            int arrStart = content.indexOf("[", stationsKey);
            //handling the json for closing bracket for stations without detecting the bikes brackets
            int depth = 0;
            int arrEnd = -1;
            for (int i = arrStart; i < content.length(); i++) {
                char c = content.charAt(i);
                if (c == '[') {
                    depth++;
                } else if (c == ']') {
                    depth--;
                }
                if (depth == 0) {
                    arrEnd = i;
                    break;
                }
            }

            if (arrStart < 0 || arrEnd < 0 || arrEnd <= arrStart) {
                throw new IllegalStateException("Invalid JSON format for stations[]");
            }

            String stationsArray = content.substring(arrStart + 1, arrEnd).trim();

            //this assumes there is no nested object except bikes[]
            String[] stationObjs = stationsArray.split("\\},\\s*\\{");

            this.stations.clear();

            for (String raw : stationObjs) {
                if (raw.startsWith("{")) {
                    raw = raw.substring(1);
                }
                if (raw.endsWith("}")) {
                    raw = raw.substring(0, raw.length() - 1);
                }

                // extract
                String name = findString(raw, "name");
                String latS = findNumber(raw, "latitude");
                String lonS = findNumber(raw, "longitude");
                String capS = findNumber(raw, "capacity");
                if (name == null || latS == null || lonS == null || capS == null) {
                    continue;
                }

                double latitude = Double.parseDouble(latS);
                double longitude = Double.parseDouble(lonS);
                int capacity = Integer.parseInt(capS);

                Station station = new Station(name, latitude, longitude, capacity);
                stations.put(name, station);

                // extract bikes array inside this station object
                int bikesIdx = raw.indexOf("\"bikes\"");
                if (bikesIdx >= 0) {
                    int bStart = raw.indexOf("[", bikesIdx);
                    int bEnd = raw.indexOf("]", bStart);
                    if (bStart > 0 && bEnd > bStart) {
                        String bikesArray = raw.substring(bStart + 1, bEnd).trim();
                        if (!bikesArray.isEmpty()) {
                            String[] bikeObjs = bikesArray.split("\\},\\s*\\{");
                            for (String braw : bikeObjs) {
                                if (braw.startsWith("{")) {
                                    braw = braw.substring(1);
                                }
                                if (braw.endsWith("}")) {
                                    braw = braw.substring(0, braw.length() - 1);
                                }
                                String id = findString(braw, "id");
                                String type = findString(braw, "type");
                                if (id != null && !id.isBlank()) {
                                    addBikeToStation(name, id, (type != null && !type.isBlank()) ? type : "standard");
                                }
                            }
                        }
                    }
                }
            }

            emit(new Event("CONFIG_LOADED", "Configuration loaded from file", "Stations: " + stations.size()));
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Failed to load config file: " + e.getMessage());
        }
    }

    private static String findString(String obj, String key) {
        String needle = "\"" + key + "\"";
        int k = obj.indexOf(needle);
        if (k < 0) {
            return null;
        }
        int colon = obj.indexOf(':', k + needle.length());
        if (colon < 0) {
            return null;
        }
        int q1 = obj.indexOf('"', colon + 1);
        if (q1 < 0) {
            return null;
        }
        int q2 = obj.indexOf('"', q1 + 1);
        if (q2 < 0) {
            return null;
        }
        return obj.substring(q1 + 1, q2);
    }

    private static String findNumber(String obj, String key) {
        String needle = "\"" + key + "\"";
        int k = obj.indexOf(needle);
        if (k < 0) {
            return null;
        }
        int colon = obj.indexOf(':', k + needle.length());
        if (colon < 0) {
            return null;
        }
        int end = colon + 1;
        while (end < obj.length() && Character.isWhitespace(obj.charAt(end))) {
            end++;
        }
        int start = end;
        while (end < obj.length()) {
            char c = obj.charAt(end);
            if ((c >= '0' && c <= '9') || c == '-' || c == '.') {
                end++;
                continue;
            }
            break;
        }
        if (start == end) {
            return null;
        }
        return obj.substring(start, end);
    }

    public void checkReservations() {
        for (Reservation reservation : new HashMap<>(activeReservations).values()) {
            reservation.checkExpiry();
            if (!reservation.isValid()) {
                emit(new Event("RESERVATION_EXPIRY", "Reservation expired", "Reservation ID: " + reservation.getReservationId()));
                cancelReservation(reservation.getReservationId());
            }
        }
    }

    public void emit(Event event) {
        event.log(); // Log to console; can extend to observers
    }

    public String startTrip(String userId, String bikeId, Station startStation, double estimatedCost, double estimatedDistance, User user) throws IllegalAccessException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        System.out.println("Debug: Checking role for user " + userId + ", role: " + user.getRole());
        if (user.getRole() != Role.RIDER) {
            throw new IllegalAccessException("User " + userId + " is not a Rider");
        }

        // Consume existing reservation for this bike instead of cancelling
        Reservation existingReservation = activeReservations.values().stream()
                .filter(r -> r.getBikeId().equals(bikeId) && r.isActive() && r.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        if (existingReservation != null) {
            existingReservation.setActive(false); // Mark as consumed
            emit(new Event("RESERVATION_CANCELLED", "Reservation consumed for trip start", "Reservation ID: " + existingReservation.getReservationId()));
        }

        if (startStation.isOutOfService()) {
            throw new IllegalStateException("Station " + startStation.getName() + " is out of service");
        }

        //debug cause I was testing 
        System.out.println("DEBUG before startTrip lookup");
        System.out.println("Station: " + startStation.getName());
        for (Dock d : startStation.getDocks()) {
            String line = "Dock " + d.getDockId() + " status=" + d.getStatus();
            if (d.isOccupied() && d.getBike() != null) {
                line += " | bike=" + d.getBike().getId() + " state=" + d.getBike().getState();
            } else {
                line += " | bike=null";
            }
            System.out.println("  " + line);
        }
        System.out.println("Looking for bikeId = " + bikeId);

        Dock fromDock = startStation.getDocks().stream()
                .filter(Dock::isOccupied)
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("[startTrip] Bike " + bikeId + " not found at start station"));

        Bike bike = fromDock.getBike();
        if (bike.isUnderMaintenance()) {
            throw new IllegalStateException("Bike " + bikeId + " is under maintenance");
        }
        if (bike.isOnTrip()) {
            throw new IllegalStateException("Bike " + bikeId + " is currently on a trip");
        }   

        fromDock.release();
        startStation.updateCounts();
        emit(new Event("DOCK_STATUS_CHANGE", "Dock released",
                "Station: " + startStation.getName() + ", Dock: " + fromDock.getDockId()));

        Trip trip = tripFactory.createTrip(bikeId, userId, startStation, estimatedCost, estimatedDistance, user, bike);
        activeTrips.put(trip.getTripId(), trip);
        System.out.println("Starting trip for bike " + bikeId + ", current state: " + (existingReservation != null ? "Reserved" : "Available"));
        emit(new Event("TRIP_START", "Trip started", "Trip ID: " + trip.getTripId()));

        return trip.getTripId();
    }

    public void endTrip(String tripId, Station endStation) {
        System.out.println(activeTrips.values());
        tripId = tripId == null ? "" : tripId.trim();  // normalize
        System.out.println("bmsService.endTrip -> looking up tripId='" + tripId + "'");
        System.out.println("activeTrips keys: " + activeTrips.keySet());

        Trip trip = activeTrips.get(tripId);
        if (trip == null) {
            throw new IllegalStateException("Trip " + tripId + " not found");
        }
        if (endStation.isOutOfService()) {
            throw new IllegalStateException("Station " + endStation.getName() + " is out of service");
        }

        Dock dock = endStation.getDocks().stream()
                .filter(d -> d.getStatus() == DockStatus.FREE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free docks at " + endStation.getName()));

        Bike bike = trip.getBike();
        if (bike == null) {
            System.out.println("WARN DEBUG: trip " + tripId + " has null bike");
            bike = new Bike(trip.getBikeId(), "standard");
            trip.setBike(bike); 
        }

        bike.endTrip(); //bike is switched back to available was confirmed with debug
        dock.occupy(bike);
        trip.endTrip(endStation);
        endStation.updateCounts();
        activeTrips.remove(tripId);

        System.out.println("Debug: Bike " + bike.getId() + " returned to dock at station " + endStation.getName());
        emit(new Event("TRIP_END", "Trip ended", "Trip ID: " + tripId));
        emit(new Event("DOCK_STATUS_CHANGE", "Dock occupied", "Station: " + endStation.getName() + ", Dock: " + dock.getDockId()));
    }

    public void reserveBike(String userId, String bikeId, int holdMinutes, Station station) {
        if (activeReservations.values().stream().anyMatch(r -> r.getUserId().equals(userId) && r.isValid())) {
            throw new IllegalStateException("User " + userId + " already has an active reservation");
        }

        Dock dock = station.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId) && d.getStatus() == DockStatus.OCCUPIED)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bike " + bikeId + " not found at station xxx"));

        Bike bike = dock.getBike();
        if (!bike.isAvailable()) {
            throw new IllegalStateException("Bike " + bikeId + " is not available");
        }
        if (bike.isUnderMaintenance()) {
            throw new IllegalStateException("Bike " + bikeId + " is under maintenance");
        }
        if (station.isOutOfService()) {
            throw new IllegalStateException("Station " + station.getName() + " is out of service");
        }

        Reservation reservation = new Reservation("res" + bikeId + userId, bikeId, userId, holdMinutes);
        bike.reserve(reservation.getExpiresAt()); 
         
        System.out.println(bike.getState()); //debug

        activeReservations.put(reservation.getReservationId(), reservation);
        System.out.println("Debug: Created reservation " + reservation.getReservationId() + " for user " + userId);
        emit(new Event("BIKE_STATUS_CHANGE", "Bike reserved", "Bike ID: " + bikeId));
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = activeReservations.get(reservationId);
        if (reservation == null || !reservation.isActive()) {
            throw new IllegalStateException("Reservation " + reservationId + " not found or already cancelled");
        }
        reservation.setActive(false);
        Station station = getStationForBike(reservation.getBikeId());
        Bike bike = findBikeById(reservation.getBikeId(), station);
        if (bike != null && "Reserved".equals(bike.getState())) {
            bike.cancelReservation();
        }
        activeReservations.remove(reservationId);
        emit(new Event("RESERVATION_CANCELLED", "Reservation cancelled", "Reservation ID: " + reservationId));
    }

    public void moveBike(String bikeId, Station fromStation, Station toStation, User user) throws IllegalAccessException {
        if (user.getRole() != Role.OPERATOR) {
            throw new IllegalAccessException("User " + user.getUserId() + " is not an Operator");
        }

        Dock fromDock = fromStation.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("[reserveBike] Bike " + bikeId + " not found at station"));

        Bike bike = fromDock.getBike();
        fromDock.release();
        fromStation.updateCounts();
        emit(new Event("DOCK_STATUS_CHANGE", "Dock released", "Station: " + fromStation.getName() + ", Dock: " + fromDock.getDockId()));

        Dock toDock = toStation.getDocks().stream()
                .filter(d -> d.getStatus() == DockStatus.FREE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free docks at to station"));

        toDock.occupy(bike);
        toStation.updateCounts();
        emit(new Event("DOCK_STATUS_CHANGE", "Dock occupied", "Station: " + toStation.getName() + ", Dock: " + toDock.getDockId()));
        emit(new Event("BIKE_STATUS_CHANGE", "Bike moved", "Bike ID: " + bikeId));
    }

    public StationReadModel getStationReadModel(String stationName) {
        Station station = stations.get(stationName);
        if (station == null) {
            throw new IllegalStateException("Station " + stationName + " not found");
        }
        return new StationReadModel(station.getName(), station.getLatitude(), station.getLongitude(), station.getCapacity(), station.getBikesAvailable(), station.getFreeDocks());
    }

    public void addStation(Station station) {
        stations.put(station.getName(), station);
        emit(new Event("STATION_ADDED", "Station added", "Station Name: " + station.getName()));
    }

    public void addBikeToStation(String stationName, String bikeId, String type) {
        Station station = requireStation(stationName);
        Dock free = station.getDocks().stream()
                .filter(d -> d.getStatus() == DockStatus.FREE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free docks at " + stationName));
        Bike bike = new Bike(bikeId, type);
        free.occupy(bike);
        station.updateCounts();
        emit(new Event("DOCK_STATUS_CHANGE", "Dock occupied (bootstrap bike)",
                "Station: " + stationName + ", Dock: " + free.getDockId()));
    }

    // Expose view of stations
    public java.util.Collection<Station> getStations() {
        return java.util.Collections.unmodifiableCollection(stations.values());
    }

    private Bike findBikeById(String bikeId, Station station) {
        return station.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId))
                .findFirst()
                .map(Dock::getBike)
                .orElse(null);
    }

    private Station getStationForBike(String bikeId) {
        for (Station station : stations.values()) {
            if (findBikeById(bikeId, station) != null) {
                return station;
            }
        }
        return null;
    }

    public Station requireStation(String name) {
        Station s = this.stations.get(name);
        if (s == null) {
            throw new IllegalStateException("Station " + name + " not found");
        }
        return s;
    }

    public void setStationOutOfService(Station s, boolean oos) {
        s.setOutOfService(oos);
        emit(new Event("STATION_STATUS_CHANGE", oos ? "Station out of service" : "Station active",
                "Station: " + s.getName()));
    }

    public void setBikeMaintenance(String bikeId, boolean maintenance) {
        Station st = getStationForBike(bikeId);
        if (st == null) {
            throw new IllegalStateException("Bike " + bikeId + " not found at any station");
        }
        Bike bike = findBikeById(bikeId, st);
        if (bike == null) {
            throw new IllegalStateException("Bike " + bikeId + " not found");
        }

        if (maintenance) {
            bike.setMaintenance();
        } else {
            bike.clearMaintenance(); // back to available
        }

        emit(new Event("BIKE_STATUS_CHANGE",
                maintenance ? "Bike in maintenance" : "Bike available",
                "Bike ID: " + bikeId));
    }
}
