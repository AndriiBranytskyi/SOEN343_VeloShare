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

    public Trip(String tripId, String bikeId, String userId, Station startStation, double cost, double distanceKM) {
        this.tripId = tripId;
        this.bikeId = bikeId;
        this.userId = userId;
        this.startTime = new Date();
        this.startStation = startStation;
        this.endTime = null;
        this.endStation = null;
        this.cost = 0.0;
        this.distanceKM = 0.0;

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

    public void setEndTime(Date endTime) { 
        this.endTime = endTime; 
    }
    public Station getStartStation() { 
        return startStation; 
    }
    public Station getEndStation() { 
        return endStation; 
    }


    public void setEndStation(Station endStation) { 
        this.endStation = endStation; 
    }

    public void endTrip(Station endStation) {
        this.endTime = new Date();
        this.endStation = endStation;
    }

    public boolean isActive() {
        return endTime == null;
    }

    // duration in min
    public long getDuration() {
        if (endTime != null) {
            return (endTime.getTime() - startTime.getTime()) / (1000 * 60); 
        }
        return -1;
    }

 
}