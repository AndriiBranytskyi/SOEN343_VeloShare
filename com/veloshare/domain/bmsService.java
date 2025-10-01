package com.veloshare.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class bmsService {
    private Map<String, Trip> activeTrips = new HashMap<>();
    private TripFactory tripFactory = new TripFactory();

    public void startTrip(String userId, String bikeId, Station startStation, double estimatedCost, double estimatedDistance, User user) {
        Trip trip = tripFactory.createTrip("trip1", bikeId, userId, startStation, estimatedCost, estimatedDistance, user);
        activeTrips.put(trip.getTripId(), trip);
    }

    public void endTrip(String tripId, Station endStation) {
        Trip trip = activeTrips.get(tripId);
        if (trip == null || trip.isActive()) throw new IllegalStateException("Trip " + tripId + " not found or still active");

        Dock dock = endStation.getDocks().stream()
                .filter(d -> d.getStatus() == DockStatus.FREE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free docks at " + endStation.getName()));

        trip.endTrip(endStation);
        Bike bike = trip.getBike(); // Retrieve the Bike from the Trip
        if (bike != null) bike.endTrip();
        dock.occupy(bike);
        endStation.updateCounts();
        activeTrips.remove(tripId);
    }

    public void reserveBike(String userId, String bikeId, double holdMinutes, Station station) {
        Dock dock = station.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId) && d.getStatus() == DockStatus.OCCUPIED)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bike " + bikeId + " not found at station"));

        Bike bike = dock.getBike();
        if (!"available".equals(bike.getState())) {
            throw new IllegalStateException("Bike " + bikeId + " is not available");
        }

        Date expiry = new Date(System.currentTimeMillis() + (long) (holdMinutes * 60 * 1000));
        bike.reserve(expiry);
    }
}