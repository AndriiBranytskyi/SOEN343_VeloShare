package com.veloshare.domain;

public class Main {

    public static void main(String[] args) {
        bmsService bmsService = new bmsService();
        try {
            bmsService.loadConfig("config.json");
        } catch (IllegalStateException e) {
            System.out.println("Failed to load config: " + e.getMessage());
            return;
        }

        Station station = bmsService.getStationReadModel("Station A").getName() != null
                ? new Station("Station A", 45.5, -73.5, 2, "-") : null;
        if (station == null) {
            throw new IllegalStateException("Station not loaded");
        }
        bmsService.addStation(station);

        System.out.println("Initial Station: " + station.getName() + ", Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks());

        Bike bike1 = new Bike("bike1", "standard");
        Dock dock1 = station.getDocks().get(0);
        dock1.occupy(bike1);
        System.out.println("After occupying dock: Bike " + bike1.getId() + " state: " + bike1.getState());
        station.updateCounts();
        System.out.println("After occupying dock: Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks());

        User user = new User("user1", "John Doe", Role.RIDER);
        try {
            bmsService.reserveBike("user1", "bike1", 1, station);
            System.out.println("After reserving: Bike State: " + bike1.getState());

            bmsService.startTrip("user1", "bike1", station, 5.50, 2.3, user);
            System.out.println("After checkout: Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            bmsService.endTrip("trip1", station); // Use trip1 for first trip
            System.out.println("After return: Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks());
            System.out.println("User " + user.getName() + " trip history: " + user.getTrips().size() + " trips");

            // Second trip
            bmsService.reserveBike("user1", "bike1", 1, station);
            System.out.println("After second reserve: Bike State: " + bike1.getState());

            bmsService.startTrip("user1", "bike1", station, 5.50, 2.3, user);
            System.out.println("After second trip start: Bikes: " + station.getBikesAvailable() + ", Free Docks: " + station.getFreeDocks());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            bmsService.endTrip("trip2", station); // Use trip2 for second trip
        } catch (IllegalAccessException e) {
            System.out.println("Access denied: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Operation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }

        // Test move with Operator role (optional, uncomment to test)
        /*
        User operator = new User("op1", "Operator Jane", Role.OPERATOR);
        try {
            bmsService.moveBike("bike1", station, station, operator);
            System.out.println("After move: Bike State: " + bike1.getState());
        } catch (IllegalAccessException e) {
            System.out.println("Access denied: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Move failed: " + e.getMessage());
        }
         */
    }
}
