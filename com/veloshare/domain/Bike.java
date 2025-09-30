package com.veloshare.domain;
import java.util.Date;

public class Bike {
    private String id;
    private String type;
    private String state; // e.g., "available", "reserved", "on_trip"
    private Date reservationExpiry;

    public Bike(String id, String type) {
        this.id = id;
        this.type = type;
        this.state = "available";
        this.reservationExpiry = null;
    }

    public String getId() { return id; }
    public String getState() { return state; }
    public void setBikeStatus(String state) {
        // Add state transition validation
        if ("reserved".equals(this.state) && "on_trip".equals(state)) {
            if (reservationExpiry == null || new Date().after(reservationExpiry)) {
                this.state = state; // Allow transition if reservation expired or not set
            } else {
                throw new IllegalStateException("Invalid state transition for bike " + id +
                                               ": Reservation still active until " + reservationExpiry);
            }
        } else if ("available".equals(this.state) && "reserved".equals(state)) {
            this.state = state; // Allow reserving from available
        } else if ("on_trip".equals(this.state) && "available".equals(state)) {
            this.state = state; // Allow returning to available
        } else {
            throw new IllegalStateException("Invalid state transition for bike " + id +
                                           ": Cannot move from " + this.state + " to " + state);
        }
    }

    public void setReservationExpiry(Date expiry) {
        this.reservationExpiry = expiry;
    }

    public Date getReservationExpiry() {
        return reservationExpiry;
    }
}