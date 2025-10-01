package com.veloshare.domain;
import java.util.Date;

public class Trip {
    private String tripId;
    private String bikeId;
    private String userId;
    private Date startTime;
    private Date endTime;
    private Station startStation;
    private Station endStation;
    private double cost;
    private double distanceKM;
    private boolean isActive;
    private Bike bike;

    public Trip(String tripId, String bikeId, String userId, Station startStation, double cost, double distanceKM, Bike bike) {
        this.tripId = tripId;
        this.bikeId = bikeId;
        this.userId = userId;
        this.startStation = startStation;
        this.cost = cost;
        this.distanceKM = distanceKM;
        this.startTime = new Date();
        this.isActive = true;
        this.bike = bike;
    }

    public String getTripId() { 
        return tripId; 
    }
    public String getBikeId() { 
        return bikeId; 
    }
    public String getUserId() { 
        return userId; 
    }
    public Date getStartTime() { 
        return startTime; 
    }
    public Date getEndTime() { 
        return endTime; 
    }

    public double getCost() { 
        return cost; 
    }
    public double getDistance() { 
        return distanceKM; 
    }

    public boolean isActive() {
        return isActive;
    }

    public Bike getBike() {
        return bike;
    }

    public void endTrip(Station endStation) {
        this.endStation = endStation;
        this.endTime = new Date();
        this.isActive = false;
    }
}