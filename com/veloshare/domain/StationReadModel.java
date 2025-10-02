package com.veloshare.domain;

public class StationReadModel {
    private String name;
    private double latitude;
    private double longitude;
    private int capacity;
    private int bikesAvailable;
    private int freeDocks;

    public StationReadModel(String name, double latitude, double longitude, int capacity, int bikesAvailable, int freeDocks) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.bikesAvailable = bikesAvailable;
        this.freeDocks = freeDocks;
    }

    // Getters
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getCapacity() { return capacity; }
    public int getBikesAvailable() { return bikesAvailable; }
    public int getFreeDocks() { return freeDocks; }
}