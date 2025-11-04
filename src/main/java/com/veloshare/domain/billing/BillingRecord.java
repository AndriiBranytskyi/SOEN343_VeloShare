package com.veloshare.domain.billing;

import java.util.Date;

public class BillingRecord {
    private final String tripId;
    private final String userId;
    private final String bikeID;
    private final String originStation;
    private final String destinationStation;
    private final Date startTime;
    private final Date endTime;
    private final double amount;


    public BillingRecord(String tripId, String userId, String bikeID, String originStation,
                         String destinationStation, Date startTime, Date endTime, double amount) {
        this.tripId = tripId;
        this.userId = userId;
        this.bikeID = bikeID;
        this.originStation = originStation;
        this.destinationStation = destinationStation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
    }

    // getters
    public String getTripId() {
        return tripId;
    }
    public String getUserId() {
        return userId;
    }
    public String getBikeId() {
        return bikeID;
    }
    public String getOriginStation() {
        return originStation;
    }
    public String getDestinationStation() {
        return destinationStation;
    }
    public Date getStartTime() {
        return startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public double getAmount() {
        return amount;
    }

    
}
