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
        return state.getClass().getSimpleName().replace("State", "");
    }

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

    public void cancelReservation() {
        state.cancelReservation(); // Delegate to state
    }

    public void setMaintenance() {
        state.setMaintenance();
    }

    void setState(BikeState state) {
        this.state = state;
    }

    interface BikeState {

        void reserve(Date expiry);

        void startTrip();

        void endTrip();

        void cancelReservation();

        void setMaintenance();
    }

    // Add these methods in Bike (public)
    public boolean isAvailable() {
        return state instanceof AvailableState;
    }

    public boolean isReserved() {
        return state instanceof ReservedState;
    }

    public boolean isOnTrip() {
        return state instanceof OnTripState;
    }

    public boolean isUnderMaintenance() {
        return state instanceof MaintenanceState;
    }

    public void clearMaintenance() {
        if (state instanceof MaintenanceState) {
            // After maintenance a bike should become Available
            this.state = new AvailableState(this);
        }
    }

    private class AvailableState implements BikeState {

        private Bike bike;

        public AvailableState(Bike bike) {
            this.bike = bike;
        }

        public void reserve(Date expiry) {
            bike.setState(new ReservedState(bike, expiry));
        }

        public void startTrip() {
            bike.setState(new OnTripState(bike));
        }

        public void endTrip() {
            throw new IllegalStateException("Cannot end trip from available state");
        }

        public void cancelReservation() {
            throw new IllegalStateException("Cannot cancel reservation from available state");
        }

        public void setMaintenance() {
            bike.setState(new MaintenanceState(bike));
        }

    }

    private class ReservedState implements BikeState {

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
            if (new Date().after(expiry)) {
                throw new IllegalStateException("Reservation expired");
            }
            bike.setState(new OnTripState(bike));
        }

        public void endTrip() {
            throw new IllegalStateException("Cannot end trip from reserved state");
        }

        public void cancelReservation() {
            bike.state = new AvailableState(bike); // Transition to Available
            System.out.println("Bike " + bike.id + " state after cancellation: " + bike.getState()); // Debug
        }

        public void setMaintenance() {
            throw new IllegalStateException("Cannot set maintenance from reserved state");
        }
    }

    private class OnTripState implements BikeState {

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

        public void cancelReservation() {
            throw new IllegalStateException("Cannot cancel reservation during trip");
        }

        public void setMaintenance() {
            throw new IllegalStateException("Cannot set maintenance during trip");
        }
    }

    private class MaintenanceState implements BikeState {

        private Bike bike;

        public MaintenanceState(Bike bike) {
            this.bike = bike;
        }

        public void reserve(Date expiry) {
            throw new IllegalStateException("Cannot reserve in maintenance");
        }

        public void startTrip() {
            throw new IllegalStateException("Cannot start trip in maintenance");
        }

        public void endTrip() {
            throw new IllegalStateException("Cannot end trip in maintenance");
        }

        public void cancelReservation() {
            throw new IllegalStateException("Cannot cancel reservation in maintenance");
        }

        public void setMaintenance() {
            throw new IllegalStateException("Already in maintenance");
        }
    }
}
