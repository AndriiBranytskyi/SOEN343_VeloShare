package com.veloshare.domain;
import java.util.ArrayList;
import java.util.List;

public class Station {
    private String name;
    private String status; 
    private double latitude;
    private double longitude;
    private String address;
    private int capacity;
    private int bikesAvailable;
    private int freeDocks;
    private List<Dock> docks = new ArrayList<>();
    private int reservationHoldMinutes = 5;

    public Station(String name, double latitude, double longitude, int capacity) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.status = "empty";
        this.bikesAvailable = 0;
        this.freeDocks = capacity;
        for (int i = 0; i < capacity; i++) {
            docks.add(new Dock("dock" + i));
        }
    }

    public String getName() { 
        return name; 
    }
    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }
    public double getLatitude() { 
        return latitude; 
    }
    public double getLongitude() { 
        return longitude; 
    }
    public String getAddress() { 
        return address; 
    }
    public void setAddress(String address) { 
        this.address = address; 
    }
    public int getCapacity() { 
        return capacity; 
    }
    public int getBikesAvailable() { 
        return bikesAvailable; 
    }
    public int getFreeDocks() { 
        return freeDocks; 
    }
    public List<Dock> getDocks() { 
        return docks; 
    }
    public int getReservationHoldMinutes() { 
        return reservationHoldMinutes; 
    }
    public void setReservationHoldMinutes(int reservationHoldMinutes) { 
        this.reservationHoldMinutes = reservationHoldMinutes; 
    }

    public void updateCounts() {
        this.bikesAvailable = (int) docks.stream().filter(d -> "occupied".equals(d.getState())).count();
        this.freeDocks = capacity - bikesAvailable;
        this.status = freeDocks == 0 ? "full" : freeDocks == capacity ? "empty" : "occupied";
    }

    public Dock getFreeDock() {
        return docks.stream().filter(d -> "empty".equals(d.getState())).findFirst().orElse(null);
    }

    public boolean canAcceptReturn() {
        return !isOutOfService() && freeDocks > 0;
    }

    public boolean isOutOfService() {
        return "out_of_service".equals(status);
    }

    public void setOutOfService(boolean oos) {
        if (oos && !isOutOfService()) {
            this.status = "out_of_service";
            docks.forEach(d -> d.setState("out_of_service"));
        } else if (!oos && isOutOfService()) {
            this.status = freeDocks == 0 ? "full" : freeDocks == capacity ? "empty" : "occupied";
            docks.forEach(d -> {
                if (d.getBike() != null) d.setState("occupied");
                else d.setState("empty");
            });
        }
        updateCounts(); 
    }

}