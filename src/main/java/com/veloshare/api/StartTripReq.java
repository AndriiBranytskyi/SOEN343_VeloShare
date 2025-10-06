package com.veloshare.api;

public record StartTripReq(String userId, String userName, String role, String bikeId, String stationName,
        double estimatedCost, double estimatedDistance) {

}
