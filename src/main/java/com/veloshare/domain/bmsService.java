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
            int stationsStart = content.indexOf("[") + 1;
            int stationsEnd = content.lastIndexOf("]");
            if (stationsStart < 0 || stationsEnd < 0) {
                throw new IllegalStateException("Invalid JSON format");
            }

            String stationsStr = content.substring(stationsStart, stationsEnd).trim();
            String[] stationStrs = stationsStr.split("\\},\\{");
            for (String stationStr : stationStrs) {
                stationStr = stationStr.replaceAll("[{}\"]", "").trim();
                String[] parts = stationStr.split(",");
                String name = null, latStr = null, lonStr = null;
                int capacity = 0;
                for (String part : parts) {
                    String[] keyValue = part.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
                        if ("name".equals(key)) {
                            name = value;
                        } else if ("latitude".equals(key)) {
                            latStr = value;
                        } else if ("longitude".equals(key)) {
                            lonStr = value;
                        } else if ("capacity".equals(key)) {
                            capacity = Integer.parseInt(value);
                        }
                    }
                }
                if (name != null && latStr != null && lonStr != null) {
                    double latitude = Double.parseDouble(latStr);
                    double longitude = Double.parseDouble(lonStr);
                    Station station = new Station(name, latitude, longitude, capacity);
                    stations.put(name, station);
                }
            }
            emit(new Event("CONFIG_LOADED", "Configuration loaded from file", "Stations: " + stations.size()));
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Failed to load config file: " + e.getMessage());
        }
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

    public void startTrip(String userId, String bikeId, Station startStation, double estimatedCost, double estimatedDistance, User user) throws IllegalAccessException {
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

        Trip trip = tripFactory.createTrip(bikeId, userId, startStation, estimatedCost, estimatedDistance, user);
        activeTrips.put(trip.getTripId(), trip);
        System.out.println("Starting trip for bike " + bikeId + ", current state: " + (existingReservation != null ? "Reserved" : "Available"));
        emit(new Event("TRIP_START", "Trip started", "Trip ID: " + trip.getTripId()));
    }

    public void endTrip(String tripId, Station endStation) {
        Trip trip = activeTrips.get(tripId);
        if (trip == null) {
            throw new IllegalStateException("Trip " + tripId + " not found");
        }

        Dock dock = endStation.getDocks().stream()
                .filter(d -> d.getStatus() == DockStatus.FREE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free docks at " + endStation.getName()));

        trip.endTrip(endStation);
        Bike bike = trip.getBike();
        if (bike != null) {
            bike.endTrip();
        }
        dock.occupy(bike);
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
                .orElseThrow(() -> new IllegalStateException("Bike " + bikeId + " not found at station"));

        Bike bike = dock.getBike();
        if (!"Available".equals(bike.getState())) {
            throw new IllegalStateException("Bike " + bikeId + " is not available");
        }

        Reservation reservation = new Reservation("res" + bikeId + userId, bikeId, userId, holdMinutes);
        bike.reserve(reservation.getExpiresAt());
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
        emit(new Event("RESERVATION_EXPIRY", "Reservation cancelled", "Reservation ID: " + reservationId));
    }

    public void moveBike(String bikeId, Station fromStation, Station toStation, User user) throws IllegalAccessException {
        if (user.getRole() != Role.OPERATOR) {
            throw new IllegalAccessException("User " + user.getUserId() + " is not an Operator");
        }

        Dock fromDock = fromStation.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bike " + bikeId + " not found at from station"));

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
}
