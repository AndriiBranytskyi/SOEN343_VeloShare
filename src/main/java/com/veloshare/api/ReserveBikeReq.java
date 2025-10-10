package com.veloshare.api;

public record ReserveBikeReq(String userId, String bikeId, String stationName, int minutes) {

}
