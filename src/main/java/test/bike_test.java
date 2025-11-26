package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.veloshare.domain.Bike;

public class bike_test {
	@Test
	public void testId() {
	    Bike b = new Bike("bike1", "standard");
	    assertEquals("bike1", b.getId());
	}
	
	@Test
	public void testType() {
	    Bike b = new Bike("bike1", "standard");
	    assertEquals("standard", b.getType());
	}
	
	@Test
	public void testAvailable() {
	    Bike b = new Bike("bike1", "standard");
	    assertEquals("Available", b.getState());
	}
	
	@Test
	public void testIsAvailable() {
	    Bike b = new Bike("bike1", "standard");
	    assertTrue(b.isAvailable());
	}
	
	@Test
	public void testReserveFromAvailable() {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	    assertTrue(b.isReserved());
	}
	
	@Test
	public void testcancelReservationFromReserved() {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	    b.cancelReservation();
	    assertTrue(b.isAvailable());
	}
	
	@Test
	public void testStartTripFromReserved() {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	    b.startTrip();
	    assertTrue(b.isOnTrip());
	}
	
	@Test
	public void testEndTripFromOnTrip() {
	    Bike b = new Bike("bike1", "standard");
	    b.startTrip();
	    b.endTrip();
	    assertTrue(b.isAvailable());
	}
	
	@Test
	public void testSetMaintenanceFromAvailable() {
	    Bike b = new Bike("bike1", "standard");
	    b.setMaintenance();
	    assertTrue(b.isUnderMaintenance());
	}
	
	@Test
	public void testClearMaintenanceFromMaintenance() {
	    Bike b = new Bike("bike1", "standard");
	    b.setMaintenance();
	    b.clearMaintenance();
	    assertTrue(b.isAvailable());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testEndTripFromAvailable() {
	    Bike b = new Bike("bike1", "standard");
	    b.endTrip();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testCancelReservationFromAvailable() {
	    Bike b = new Bike("bike1", "standard");
	    b.cancelReservation();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testReserveFromReserved() {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	    b.reserve(new Date(System.currentTimeMillis() + 2000));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSetMaintenanceFromReserved() {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	    b.setMaintenance();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testReserveWhileOnTrip() {
	    Bike b = new Bike("bike1", "standard");
	    b.startTrip();
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStartTripFromOnTrip() {
	    Bike b = new Bike("bike1", "standard");
	    b.startTrip();
	    b.startTrip();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testCancelReservationFromOnTrip() {
	    Bike b = new Bike("bike1", "standard");
	    b.startTrip();
	    b.cancelReservation();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSetMaintenanceFromOnTrip() {
	    Bike b = new Bike("bike1", "standard");
	    b.startTrip();
	    b.setMaintenance();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testReserveFromInMaintenance() {
	    Bike b = new Bike("bike1", "standard");
	    b.setMaintenance();
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStartTripFromInMaintenance() {
	    Bike b = new Bike("bike1", "standard");
	    b.setMaintenance();
	    b.startTrip();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testEndTripFromInMaintenance() {
	    Bike b = new Bike("bike1", "standard");
	    b.setMaintenance();
	    b.endTrip();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testCancelReservationFromInMaintenance() {
	    Bike b = new Bike("bike1", "standard");
	    b.setMaintenance();
	    b.cancelReservation();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSetMaintenanceFromInMaintenance() {
	    Bike b = new Bike("bike1", "standard");
	    b.setMaintenance();
	    b.setMaintenance();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStartTripFromReservedExpired() throws InterruptedException {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 50));
	    Thread.sleep(120);
	    b.startTrip();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testEndTripFromReserved() {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	    b.endTrip();
	}
	
	@Test
	public void testStateReserved() {
	    Bike b = new Bike("bike1", "standard");
	    b.reserve(new Date(System.currentTimeMillis() + 1000));
	    assertEquals("Reserved", b.getState());
	}
}
