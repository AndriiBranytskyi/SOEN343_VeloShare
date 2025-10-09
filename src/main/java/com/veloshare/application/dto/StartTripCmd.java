package com.veloshare.application.dto;

public record StartTripCmd(String userId, String bikeId, String stationName, double estimatedCost, double estimatedDistance) {

}
