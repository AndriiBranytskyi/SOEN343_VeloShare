package com.veloshare.api;

public record MoveBikeReq(String bikeId, String fromStation, String toStation,
        String operatorId, String operatorName) {

}
