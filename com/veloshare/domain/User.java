package com.veloshare.domain;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private double balance;
    private Role role;
    private List<Trip> trips;

    public User(String userId, String name, Role role) {
        this.userId = userId;
        this.name = name;
        this.balance = 0.0;
        this.role = role;
        this.trips = new ArrayList<>();
    }

    public String getUserId() { 
        return userId; 
    }
    public String getName() { 
        return name; 
    }
    public double getBalance() { 
        return balance; 
    }
    // Return copy
    public List<Trip> getTrips() { 
        return new ArrayList<>(trips); 
    } 
    public void addTrip(Trip trip) { 
        trips.add(trip); 
    }
    public void deductBalance(double amount) { 
        balance -= amount; 
    }
    public Role getRole() { 
        return role; 
    }
}