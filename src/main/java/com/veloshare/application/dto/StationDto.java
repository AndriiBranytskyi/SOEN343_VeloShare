package com.veloshare.application.dto;

import java.util.List;

public record StationDto(String name, double lat, double lon, int capacity, int bikesAvailable, int freeDocks, int standardBikes, int eBikes, String address, List<BikeDto> bikes) {

    public record BikeDto(String id, String type) {

    }

}
