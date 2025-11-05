package com.veloshare.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Station {

    private String name;
    private double latitude;
    private double longitude;
    private int capacity;
    private List<Dock> docks;
    private boolean outOfService;
    private String address;

    public Station(String name, double latitude, double longitude, int capacity, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.address = address;
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

    public String getAddress() {
        return address;
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
        this.outOfService = oos;
    }

    public boolean isOutOfService() {
        return outOfService;
    }

    /**
     * Dock the given bike into the first FREE dock in this station.
     * Returns the dockId assigned.
     * Throws IllegalStateException if no free dock exists.
    
    public synchronized int dockBike(Bike bike) {
        if (bike == null) throw new IllegalArgumentException("bike must not be null");

        // If bike is already docked here, return its dock id
        for (Dock d : docks) {
            Bike b = d.getBike();
            if (b != null && equalsBikeId(b, bike)) {
                return d.getDockId();
            }
        }

        // find first free dock
        for (Dock d : docks) {
            if (d.getStatus() == DockStatus.FREE) {
                d.occupy(bike);
                return d.getDockId();
            }
        }

        throw new IllegalStateException("No free docks available at station " + name);
    }

    /**
     * Dock by bike id: construct a minimal Bike stub if you only have id and optional type.
     * Returns dockId assigned.
     
    public synchronized int dockBikeById(String bikeId, String bikeType) {
        if (bikeId == null) throw new IllegalArgumentException("bikeId required");
        Bike stub = new Bike(bikeId, bikeType); // assumes this constructor exists; adjust if needed
        return dockBike(stub);
    }

    /**
     * Release the dock that currently holds the given bike (matching by bike id).
     * Returns the dockId released or -1 if bike not found at this station.
     
    public synchronized int releaseBike(Bike bike) {
        if (bike == null) return -1;
        for (Dock d : docks) {
            Bike b = d.getBike();
            if (b != null && equalsBikeId(b, bike)) {
                d.release();
                return d.getDockId();
            }
        }
        return -1;
    }

    /**
     * Release by bike id.
     * Returns dockId released or -1 if not found.
     
    public synchronized int releaseBikeById(String bikeId) {
        if (bikeId == null) return -1;
        for (Dock d : docks) {
            Bike b = d.getBike();
            if (b != null && bikeId.equals(String.valueOf(b.getId()))) {
                d.release();
                return d.getDockId();
            }
        }
        return -1;
    }

    /**
     * Release the dock by dockId.
     * Throws IllegalStateException if the dock is already free.
     
    public synchronized void releaseDock(int dockId) {
        for (Dock d : docks) {
            if (d.getDockId() == dockId) {
                d.release();
                return;
            }
        }
        throw new IllegalArgumentException("Invalid dockId: " + dockId);
    }

    private boolean equalsBikeId(Bike a, Bike b) {
        if (a == null || b == null) return false;
        // try common getter names; adapt if your Bike class uses different accessors
        try {
            Object ida = a.getId();
            Object idb = b.getId();
            return Objects.equals(String.valueOf(ida), String.valueOf(idb));
        } catch (Throwable t) {
            // fallback to object equality
            return a == b;
        }
    }
        */
}
