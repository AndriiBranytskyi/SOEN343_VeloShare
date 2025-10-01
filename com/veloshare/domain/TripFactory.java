package com.veloshare.domain;

import java.util.Date;

public class TripFactory {
    public Trip createTrip(String tripId, String bikeId, String userId, Station startStation, double estimatedCost, double estimatedDistance, User user) {
        Bike bike = startStation.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId) && d.getStatus() == DockStatus.OCCUPIED)
                .findFirst()
                .map(Dock::getBike)
                .orElseThrow(() -> new IllegalStateException("Bike " + bikeId + " not found at station"));

        if (!"reserved".equals(bike.getState()) && !"available".equals(bike.getState())) {
            throw new IllegalStateException("Bike " + bikeId + " is not available or reserved");
        }

        Trip trip = new Trip(tripId, bikeId, userId, startStation, estimatedCost, estimatedDistance, bike);
        bike.startTrip();
        startStation.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId))
                .findFirst()
                .ifPresent(Dock::release);
        user.addTrip(trip);
        return trip;
    }

}