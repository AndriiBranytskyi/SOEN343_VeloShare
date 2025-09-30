package com.veloshare.domain;


import com.veloshare.domain.Bike;
public class Dock {
    private String id;
    private String state; 
    private Bike bike;

    public Dock(String id) {
        this.id = id;
        this.state = "empty";
        this.bike = null;
    }

    public String getId() { 
        return id; 
    }
    public String getState() { 
        return state; 
    }
    public void setState(String state) { 
        this.state = state; 
    }
    public Bike getBike() { 
        return bike; 
    }
    public void setBike(Bike bike) { 
        this.bike = bike; 
    }

    public void occupy(Bike bike) {
        if (!"out_of_service".equals(state)) {
            this.state = "occupied";
            this.bike = bike;
        } else {
            throw new IllegalStateException("Dock " + id + " is out of service");
        }
    }

    public Bike release() {
        if ("occupied".equals(state)) {
            Bike temp = bike;
            this.state = "empty";
            this.bike = null;
            return temp;
        }
        return null;
    }

    public boolean isOutOfService() {
        return "out_of_service".equals(state);
    }

    
}