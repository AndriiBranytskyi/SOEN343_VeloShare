package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.veloshare.domain.Reservation;

public class reservation_test {
	
	@Test
	public void testGetReservationId() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 5);
	    assertEquals("res1", r.getReservationId());
	}
	
	@Test
	public void testGetBikeId() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 5);
	    assertEquals("bike1", r.getBikeId());
	}
	
	@Test
	public void testGetUserId() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 5);
	    assertEquals("user1", r.getUserId());
	}
	
	@Test
	public void testGetCreatedAtNotNull() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 5);
	    assertNotNull(r.getCreatedAt());
	}
	
	@Test
	public void testGetExpiresAtNotNull() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 1);
	    assertNotNull(r.getExpiresAt());
	}
	
	@Test
	public void testIsActiveInitiallyTrue() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 1);
	    assertTrue(r.isActive());
	}
	
	@Test
	public void testSetActiveChangesState() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 1);
	    r.setActive(false);
	    assertFalse(r.isActive());
	}
	
	@Test
	public void testCheckExpirySetsInactiveWhenExpired() throws InterruptedException {
	    Reservation r = new Reservation("res1", "bike1", "user1", 0); 
	    Thread.sleep(10);
	    r.checkExpiry();
	    assertFalse(r.isActive());
	}
	
	@Test
	public void testIsValidReturnsTrueWhenNotExpired() {
	    Reservation r = new Reservation("res1", "bike1", "user1", 5);
	    assertTrue(r.isValid());
	}
}
