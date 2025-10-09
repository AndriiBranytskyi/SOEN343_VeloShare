package com.veloshare.application.dto;

public record ReserveBikeCmd(String userId, String bikeId, String StationName, int minutes) {

}
