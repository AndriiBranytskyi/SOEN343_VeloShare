package com.veloshare.api;

public record StartTripReq(String userId,String bikeId, String stationName,
        double estimatedCost, double estimatedDistance) {

}
