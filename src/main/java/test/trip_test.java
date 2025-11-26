package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.veloshare.domain.Bike;
import com.veloshare.domain.Station;
import com.veloshare.domain.Trip;

public class trip_test {
	
	private Station createStation() {
	    return new Station("Station A", 45.4532, -73.564, 2, "123 Sainte-Catherine St W");
	}
	
	private Bike createBike() {
	    return new Bike("bike1", "standard");
	}
	
	private Trip createTrip() {
	    return new Trip("trip1", "bike1", "user1", createStation(), 10.0, 5.0, createBike());
	}
	
	@Test
	public void testSetAndGetBike() {
	    Trip trip = createTrip();
	    Bike newBike = new Bike("bike2", "e-bike");
	    trip.setBike(newBike);
	    assertEquals("bike2", trip.getBike().getId());
	}
	
	@Test
	public void testSetAndGetBaseCost() {
	    Trip trip = createTrip();
	    trip.setBaseCost(15.0);
	    assertEquals(15.0, trip.getBaseCost(), 0.0);
	}
	
	@Test
	public void testSetAndGetFlexUsed() {
	    Trip trip = createTrip();
	    trip.setFlexUsed(3.0);
	    assertEquals(3.0, trip.getFlexUsed(), 0.0);
	}
	
	@Test
	public void testEndTrip() {
	    Trip trip = createTrip();
	    Station endStation = new Station("Station B", 45.0, -73.5, 2, "456 Another St");
	    trip.endTrip(endStation);
	    assertEquals("Station B", trip.getEndStation().getName());
	    assertNotNull(trip.getEndTime());
	    assertFalse(trip.isActive());
	}
	
	@Test
	public void testGetDurationMillisZeroTimeNull() {
	    Trip trip = createTrip();
	    assertEquals(0L, trip.getDurationMillis());
	}
	
	@Test
	public void testGetDurationMillisEndTrip() throws InterruptedException {
	    Trip trip = createTrip();
	    Thread.sleep(5);
	    trip.endTrip(createStation());
	    assertTrue(trip.getDurationMillis() >= 0L);
	}
	
	@Test
	public void testSetAndGetCost() {
	    Trip trip = createTrip();
	    trip.setCost(20.0);
	    assertEquals(20.0, trip.getCost(), 0.0);
	}
	
	@Test
	public void testGetTripId() {
	    Trip trip = createTrip();
	    assertEquals("trip1", trip.getTripId());
	}
	
	@Test
	public void testGetBikeId() {
	    Trip trip = createTrip();
	    assertEquals("bike1", trip.getBikeId());
	}
	
	@Test
	public void testGetUserId() {
	    Trip trip = createTrip();
	    assertEquals("user1", trip.getUserId());
	}
	
	@Test
	public void testGetStartTimeNotNull() {
	    Trip trip = createTrip();
	    assertNotNull(trip.getStartTime());
	}
	
	@Test
	public void testGetStartStation() {
	    Trip trip = createTrip();
	    assertEquals("Station A", trip.getStartStation().getName());
	}
	
	@Test
	public void testGetDistance() {
	    Trip trip = createTrip();
	    assertEquals(5.0, trip.getDistance(), 0.0);
	}
}
