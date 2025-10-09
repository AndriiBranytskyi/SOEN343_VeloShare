package com.veloshare.domain;

public class TripFactory {

    private static int tripCounter = 0;

    public Trip createTrip(String bikeId, String userId, Station startStation, double estimatedCost, double estimatedDistance, User user, Bike bike) {
        String tripId = "trip" + (++tripCounter);
        Trip trip = new Trip(tripId, bikeId, userId, startStation, estimatedCost, estimatedDistance, bike);
        bike.startTrip();
        user.addTrip(trip);
        return trip;
    }
}
