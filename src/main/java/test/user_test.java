package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.veloshare.domain.Bike;
import com.veloshare.domain.LoyaltyTier;
import com.veloshare.domain.Role;
import com.veloshare.domain.Station;
import com.veloshare.domain.Trip;
import com.veloshare.domain.User;

public class user_test {
	
	private User createUser() {
	    return new User("user1", "ww@gmail.com", Role.RIDER);
	}
	
	@Test
	public void testAddTrip() {
	    User user = createUser();
	    Trip trip = new Trip("trip1", "bike1", "user1",new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W"),10.0, 5.0, new Bike("bike1", "standard"));
	    user.addTrip(trip);
	    Trip t = user.getTrips().get(0);
	    assertEquals("trip1", t.getTripId());
	}
	
	@Test
	public void testDeductBalance() {
	    User user = createUser();
	    user.deductBalance(5.0);
	    assertEquals(-5.0, user.getBalance(), 0.0);
	}
	
	@Test
	public void testAddFlexDollars() {
	    User user = createUser();
	    user.addFlexDollars(10.0);
	    assertEquals(10.0, user.getFlexDollars(), 0.0);
	}
	
	@Test
	public void testAddFlexDollarsNegative() {
	    User user = createUser();
	    user.addFlexDollars(-5.0);
	    assertEquals(0.0, user.getFlexDollars(), 0.0);
	}
	
	@Test
	public void testApplyFlexToCostZero() {
	    User user = createUser();
	    user.addFlexDollars(10.0);
	    double result = user.applyFlexToCost(0.0);
	    assertEquals(0.0, result, 0.0);
	}
	
	@Test
	public void testApplyFlexToCost() {
	    User user = createUser();
	    user.addFlexDollars(5.0);
	    double result = user.applyFlexToCost(8.0);
	    assertEquals(3.0, result, 0.0);
	}
	
	@Test
	public void testApplyFlexToCostZerocost() {
	    User user = createUser();
	    user.addFlexDollars(5.0);
	    double result = user.applyFlexToCost(0.0);
	    assertEquals(0.0, result, 0.0);
	}
	
	@Test
	public void testSetAndGetTier() {
	    User user = createUser();
	    user.setTier(LoyaltyTier.GOLD);
	    assertEquals(LoyaltyTier.GOLD, user.getTier());
	}

	@Test
	public void testGetUserId() {
	    User user = createUser();
	    assertEquals("user1", user.getUserId());
	}
	
	@Test
	public void testGetName() {
	    User user = createUser();
	    assertEquals("ww@gmail.com", user.getName());
	}
	
	@Test
	public void testGetRole() {
	    User user = createUser();
	    assertEquals(Role.RIDER, user.getRole());
	}
	
}
