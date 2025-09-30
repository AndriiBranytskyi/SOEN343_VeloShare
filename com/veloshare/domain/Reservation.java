package com.veloshare.domain;
import java.util.Date;

public class Reservation {
    private String reservationId;
    private String bikeId;
    private String userId;
    private Date createdAt;
    private Date expiresAt;
    private boolean isActive;

    public Reservation(String reservationId, String bikeId, String userId, int holdMinutes) {
        this.reservationId = reservationId;
        this.bikeId = bikeId;
        this.userId = userId;
        this.createdAt = new Date();
        this.expiresAt = new Date(System.currentTimeMillis() + holdMinutes * 60000);
        this.isActive = true;
    }

    public void checkExpiry() {
        if (new Date().after(expiresAt)) {
            this.isActive = false;
        }
    }

    public String getReservationId() { 
        return reservationId;
    }
    public String getBikeId() { 
        return bikeId; 
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


    public String getUserId() { 
        return userId; 
    }

    public boolean isActive() { 
        return isActive; 
    }
    public void setActive(boolean active) { 
        this.isActive = active; 
    }
   
    public boolean isValid() {
        checkExpiry();
        return isActive;
    }


}