package com.veloshare.domain;
import java.util.ArrayList;
import java.util.List;

public class Station {
    private String name;
    private double latitude;
    private double longitude;
    private int capacity;
    private List<Dock> docks;

    public Station(String name, double latitude, double longitude, int capacity) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.docks = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            docks.add(new Dock(i, i)); // position = dockId 
        }
    }

    public String getName() { 
        return name; 
    }
    public double getLatitude() { 
        return latitude; 
    }
    public double getLongitude() { 
        return longitude; 
    }
    public int getCapacity() { 
        return capacity; 
    }
    public List<Dock> getDocks() { 
        return new ArrayList<>(docks); 
    }
    public int getBikesAvailable() { 
        return (int) docks.stream().filter(Dock::isOccupied).count(); 
    }
    public int getFreeDocks() { 
        return (int) docks.stream().filter(d -> d.getStatus() == DockStatus.FREE).count(); 
    }

    public void updateCounts() {
        // empty for now
    }
    public boolean isFull() {
        return getFreeDocks() == 0;
    }

    public boolean isEmpty() {
        return getBikesAvailable() == 0;
    }

    public void setOutOfService(boolean oos) {
        // Update logic as needed
    }
}