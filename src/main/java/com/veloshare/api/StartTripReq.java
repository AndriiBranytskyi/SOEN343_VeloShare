package com.veloshare.api;

public record StartTripReq(String bikeId, String stationName,
        double estimatedCost, double estimatedDistance) {

}
