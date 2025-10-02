package com.veloshare.domain;

public class Dock {
    private int dockId;
    private Bike bike;
    private int position; 
    private DockStatus status;

    public Dock(int dockId, int position) {
        this.dockId = dockId;
        this.position = position;
        this.status = DockStatus.FREE;
        this.bike = null;
    }

    public int getDockId() { 
        return dockId; 
    }
    public int getPosition() { 
        return position; 
    }
    public Bike getBike() {
        return bike;
    }

    public DockStatus getStatus() { 
        return status;
     }

     public boolean isOccupied() { 
        return status == DockStatus.OCCUPIED; 
    }

    public void occupy(Bike bike) {
        if (this.bike != null || this.status != DockStatus.FREE) {
            throw new IllegalStateException("Dock " + dockId + " is already occupied");
        }
        this.bike = bike;
        this.status = DockStatus.OCCUPIED;
    }

    public void release() {
        if (this.bike == null || this.status != DockStatus.OCCUPIED) {
            throw new IllegalStateException("Dock " + dockId + " is already empty");
        }
        this.bike = null;
        this.status = DockStatus.FREE;
    }

    public void setOutOfService() {
        if (this.bike != null) {
            throw new IllegalStateException("Cannot choose occupied dock " + dockId + " out of service");
        }
        this.status = DockStatus.OUT_OF_SERVICE;
    }
}

enum DockStatus {
    FREE,
    OCCUPIED,
    OUT_OF_SERVICE
}
