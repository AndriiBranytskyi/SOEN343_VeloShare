package com.veloshare.main;

import com.veloshare.domain.Bike;
import com.veloshare.domain.Dock;
import com.veloshare.domain.Station;
import com.veloshare.domain.Trip;
import com.veloshare.domain.Reservation;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        // Create a station with 2 docks
        Station station = new Station("Station A", 45.5, -73.5, 2);
        System.out.println("Initial Station: " + station.getName() + ", Status: " + station.getStatus() +
                           ", Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks());

        // Create a bike
        Bike bike = new Bike("bike1", "standard");
        System.out.println("Created Bike: " + bike.getId() + ", State: " + bike.getState());

        // Occupy a dock with the bike
        Dock dock1 = station.getDocks().get(0);
        dock1.occupy(bike);
        station.updateCounts();
        System.out.println("After occupying dock: Status: " + station.getStatus() +
                           ", Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks());

        // Reserve the bike
        Reservation reservation = new Reservation("res1", bike.getId(), "user1", 1); // 1 minute hold
        bike.setReservationExpiry(reservation.getExpiresAt());
        bike.setBikeStatus("reserved");
        System.out.println("After reserving: Bike State: " + bike.getState() +
                           ", Expires At: " + reservation.getExpiresAt());

        // Start a trip with sample cost and distance
        Trip trip = new Trip("trip1", bike.getId(), "user1", station, 5.50, 2.3); // $5.50 cost, 2.3 km
        bike.setBikeStatus("on_trip");
        dock1.release();
        station.updateCounts();
        System.out.println("After checkout (trip start): Status: " + station.getStatus() +
                           ", Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks() +
                           ", Trip Active: " + trip.isActive() + ", Cost: $" + trip.getCost() + ", Distance: " + trip.getDistance() + " km");

        // End the trip (simulate return)
        station.getDocks().get(1).occupy(bike);
        trip.endTrip(station);
        bike.setBikeStatus("available");
        station.updateCounts();
        System.out.println("After return (trip end): Status: " + station.getStatus() +
                           ", Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks() +
                           ", Trip Active: " + trip.isActive() + ", Cost: $" + trip.getCost() + ", Distance: " + trip.getDistance() + " km" +
                           ", Duration: " + trip.getDuration() + " min");

        // Test reservation expiry (simulate time passing)
        System.out.println("Reservation Active: " + reservation.isValid() + " at " + new Date());
        try {
            Thread.sleep(61000); // Wait > 1 minute to expire
            reservation.checkExpiry();
            System.out.println("After expiry wait: Reservation Active: " + reservation.isValid() + " at " + new Date());
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
        }

        // Test out-of-service
        station.setOutOfService(true);
        System.out.println("After setting OOS: Status: " + station.getStatus());
        station.setOutOfService(false);
        station.updateCounts();
        System.out.println("After resetting OOS: Status: " + station.getStatus());
    }
}