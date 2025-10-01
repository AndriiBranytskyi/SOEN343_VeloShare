package com.veloshare.domain;

import java.util.Date;

public class Bike {
    private String id;
    private String type;
    private BikeState state;

    public Bike(String id, String type) {
        this.id = id;
        this.type = type;
        this.state = new AvailableState(this);
    }

    public String getId() { 
        return id; 
    }
    public String getState() { 
        return state.getClass().getSimpleName().replace("State", ""); }

    public String getType() {
        return type;
    }

    public void reserve(Date expiry) {
        state.reserve(expiry);
    }

    public void startTrip() {
        state.startTrip();
    }

    public void endTrip() {
        state.endTrip();
    }
   
    void setState(BikeState state) { 
        this.state = state;
    }
}

interface BikeState {
    void reserve(Date expiry);
    void startTrip();
    void endTrip();
}

class AvailableState implements BikeState {
    private Bike bike;

    public AvailableState(Bike bike) { 
        this.bike = bike; 
    }
    public void reserve(Date expiry) { 
        bike.setState(new ReservedState(bike, expiry)); 
    }
    public void startTrip() { 
        throw new IllegalStateException("Cannot start trip from available state"); 
    }
    public void endTrip() { 
        throw new IllegalStateException("Cannot end trip from available state"); 
    }
}

class ReservedState implements BikeState {
    private Bike bike;
    private Date expiry;

    public ReservedState(Bike bike, Date expiry) { 
        this.bike = bike; 
        this.expiry = expiry; 
    }

    public void reserve(Date expiry) { 
         throw new IllegalStateException("Already reserved"); 
        }

    public void startTrip() {
        if (new Date().after(expiry)) 
            throw new IllegalStateException("Reservation expired");
        bike.setState(new OnTripState(bike));
    }

    public void endTrip() { 
        throw new IllegalStateException("Cannot end trip from reserved state"); 
    }
}

class OnTripState implements BikeState {
    private Bike bike;

    public OnTripState(Bike bike) { 
        this.bike = bike; 
    }
    public void reserve(Date expiry) { 
        throw new IllegalStateException("Cannot reserve during trip"); 
    }
    public void startTrip() {  
        throw new IllegalStateException("Already on trip"); 
    }
    public void endTrip() { 
        bike.setState(new AvailableState(bike)); 
    }
}