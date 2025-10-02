package com.veloshare.domain;

public class TripFactory {
    private static int tripCounter = 0;

    public Trip createTrip(String bikeId, String userId, Station startStation, double estimatedCost, double estimatedDistance, User user) {
        String tripId = "trip" + (++tripCounter);
        Bike bike = startStation.getDocks().stream()
                .filter(d -> d.getBike() != null && d.getBike().getId().equals(bikeId) && d.getStatus() == DockStatus.OCCUPIED)
                .findFirst()
                .map(Dock::getBike)
                .orElseThrow(() -> new IllegalStateException("Bike " + bikeId + " not found at station"));

        // Allow trip start from Reserved or Available state
        if (!"Reserved".equals(bike.getState()) && !"Available".equals(bike.getState())) {
            throw new IllegalStateException("Bike " + bikeId + " is not in a valid state for trip start");
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