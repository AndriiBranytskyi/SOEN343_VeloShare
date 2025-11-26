package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.veloshare.domain.Bike;
import com.veloshare.domain.Dock;
import com.veloshare.domain.Role;
import com.veloshare.domain.Station;
import com.veloshare.domain.User;
import com.veloshare.domain.bmsService;

public class BMS_tests {
	
	@Test
	public void testLoadConfig() {
		bmsService bms = new bmsService();
		bms.loadConfig("./config.json");
	    assertFalse("Stations should not be empty after loading config", bms.getStations().isEmpty());
	}
	
	@Test(expected = NullPointerException.class)
	public void testLoadConfigNull() {
		bmsService bms = new bmsService();
	    bms.loadConfig(null);
	}
	
	// startTrip

    @Test
    public void testStartTripHappyPath() throws Exception {
        bmsService bms = new bmsService();
        bms.loadConfig("./config.json");
        Station station = bms.requireStation("Station A");
        Bike bike = new Bike("bike1", "standard");
        User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
        bms.reserveBike(operator.getUserId(), bike.getId(), 5, station);
        String tripId = bms.startTrip(operator.getUserId(), bike.getId(), station, 10.0, 5.0, operator);

        assertEquals("Trip should be registered in activeTrips","bike1", bms.getActiveTrip(tripId).getBikeId());
    }
    
	@Test(expected = IllegalArgumentException.class)
	public void testStartTripNullUser() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	
	    bms.startTrip(null, bike.getId(), station, 10.0, 5.0, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testStartTripNullStation() throws Exception {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Bike bike = new Bike("bike1", "standard");
	    User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
	
	    bms.startTrip(operator.getUserId(), bike.getId(), null, 10.0, 5.0, operator);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStartTripNullBikeId() throws Exception {
        bmsService bms = new bmsService();
        bms.loadConfig("./config.json");
        Station station = bms.requireStation("Station A");
        User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
        bms.startTrip(operator.getUserId(), null, station, 10.0, 5.0, operator);
        
	}
	
	
	// endTrip
	
	@Test
	public void testEndTripHappyPath() throws Exception {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
	
	    // Reserve and start trip
	    bms.reserveBike(operator.getUserId(), bike.getId(), 5, station);
	    String tripId = bms.startTrip(operator.getUserId(), bike.getId(), station, 10.0, 5.0, operator);
	
	    // End trip 
	    Station stationC = bms.requireStation("Station C");
	    bms.endTrip(tripId, stationC);
	
	    assertTrue("Bike now in Station C", bms.requireStation("Station C").getFreeDocks() == 1);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testEndTripNullTripId() throws IllegalAccessException {
		bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
	
	    // Reserve and start trip
	    bms.reserveBike(operator.getUserId(), bike.getId(), 5, station);
	    bms.startTrip(operator.getUserId(), bike.getId(), station, 10.0, 5.0, operator);
	
	    // End trip 
	    Station stationC = bms.requireStation("Station C");
	    bms.endTrip(null, stationC);
	}
	
	@Test(expected = NullPointerException.class)
	public void testEndTripNullStation() throws Exception {
		bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
	
	    // Reserve and start trip
	    bms.reserveBike(operator.getUserId(), bike.getId(), 5, station);
	    String tripId = bms.startTrip(operator.getUserId(), bike.getId(), station, 10.0, 5.0, operator);
	
	    // End trip 
	    bms.endTrip(tripId, null);
	}
	
	// reserveBike
	
	@Test
	public void testReserveBikeHappyPath() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    bms.reserveBike("user1", bike.getId(), 5, station);
	    
	    assertEquals("Bike status must be reserved","Reserved", station.getDocks().get(0).getBike().getState());
	}

	@Test (expected = IllegalStateException.class)
	public void testReserveBikeDoubleReserv() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    
	    bms.reserveBike("user1", bike.getId(), 5, station);
	    bms.reserveBike("user1", bike.getId(), 5, station);
	    
	}

	@Test(expected = IllegalStateException.class)
	public void testReserveBikeNullBikeId() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");

	    bms.reserveBike("user1", null, 5, station);
	}

	@Test(expected = NullPointerException.class)
	public void testReserveBikeNullStation() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");

	    Bike bike = new Bike("bike1", "standard");

	    bms.reserveBike("user1", bike.getId(), 5, null);
	}
	
	
	// cancelReservation
	@Test
	public void testcancelReservationBikeHappyPath() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    bms.reserveBike("user1", bike.getId(), 5, station);
	    bms.cancelReservation("resbike1user1");
	    
	    assertEquals("Bike status must be reserved","Available", station.getDocks().get(0).getBike().getState());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testcancelReservationBikeNullId() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    bms.reserveBike("user1", bike.getId(), 5, station);
	    
	    bms.cancelReservation(null);
	}
	
	// moveBike
	@Test
	public void testMoveBikeHappyPath() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Station stationC = bms.requireStation("Station C");
	    Bike bike = new Bike("bike1", "standard");
	    User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
	    
	    bms.moveBike(bike.getId(), station, stationC, operator);
	    
	    assertTrue("Bike now in Station C", bms.requireStation("Station C").getFreeDocks() == 1);
	}
	
	@Test(expected = IllegalAccessException.class)
	public void testMoveBikeRiderAttempt() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Station stationC = bms.requireStation("Station C");
	    Bike bike = new Bike("bike1", "standard");
	    User user = new User("user1", "qqq@gmail.com", Role.RIDER);
	    
	    bms.moveBike(bike.getId(), station, stationC, user);
	}
	
	@Test(expected = IllegalAccessException.class)
	public void testMoveBikeNullBike() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Station stationC = bms.requireStation("Station C");
	    User user = new User("user1", "qqq@gmail.com", Role.RIDER);
	    
	    bms.moveBike(null, station, stationC, user);
	}
	
	@Test(expected = IllegalAccessException.class)
	public void testMoveBikeNullStationDep() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station stationC = bms.requireStation("Station C");
	    User user = new User("user1", "qqq@gmail.com", Role.RIDER);
	    
	    bms.moveBike(null, null, stationC, user);
	}
	
	@Test(expected = IllegalAccessException.class)
	public void testMoveBikeNullStationArr() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    User user = new User("user1", "qqq@gmail.com", Role.RIDER);
	    
	    bms.moveBike(null, station, null, user);
	}
	
	@Test(expected = NullPointerException.class)
	public void testMoveBikeNullUser() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Station stationC = bms.requireStation("Station C");
	    
	    bms.moveBike(null, station, stationC, null);
	}
	
	// setStationOutOfService
	
	@Test
	public void testSetStationOutOfServiceHappyPath() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	
	    bms.setStationOutOfService(station, true);
	    assertTrue("Station should be out of service", station.isOutOfService());
	}
	
	// setBikeMaintenance
	
	@Test
	public void testSetBikeMaintenanceHappyPath() {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Bike bike = new Bike("bike1", "standard");
	    
	    bms.setBikeMaintenance(bike.getId(), true);
	    assertEquals("Bike should be under maintenance","Maintenance", station.getDocks().get(0).getBike().getState());
	
	}
	
	// resetToInitial
	
	@Test
	public void testresetToInitialHappyPath() throws IllegalAccessException {
	    bmsService bms = new bmsService();
	    bms.loadConfig("./config.json");
	    Station station = bms.requireStation("Station A");
	    Station stationC = bms.requireStation("Station C");
	    Bike bike = new Bike("bike1", "standard");
	    User operator = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
	    
	    bms.moveBike(bike.getId(), station, stationC, operator);
	    bms.resetToInitial();
	    assertTrue("Bike reset to station A and not in Station C", bms.requireStation("Station C").getFreeDocks() == 2);
	}
}
