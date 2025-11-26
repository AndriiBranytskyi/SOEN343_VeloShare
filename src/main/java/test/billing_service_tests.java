package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.veloshare.application.usecases.BillingService;
import com.veloshare.domain.Bike;
import com.veloshare.domain.Billing;
import com.veloshare.domain.LoyaltyTier;
import com.veloshare.domain.Station;
import com.veloshare.domain.Trip;

public class billing_service_tests {
	
	private BillingService billingService;
	private Trip trip;
	private Station station;
	
	@Before
	public void setUp() {
	    billingService = new BillingService();
	    station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    Bike bike = new Bike("bike1", "standard");
	    trip = new Trip("trip1", bike.getId(), "user1", station, 10.0, 5.0, bike);
	    trip.endTrip(station);
	}
	
	@Test
	public void testGetFlexDollars() {
	    assertEquals(0.0, billingService.getFlexDollarsForUser("user1"), 0.0);
	}
	
	@Test
	public void testEarnFlexDollars() {
	    billingService.earnFlexDollars("user1", 5.0);
	    assertEquals(5.0, billingService.getFlexDollarsForUser("user1"), 0.0);
	}
	
	@Test
	public void testEarnFlexDollarsNegative() {
	    billingService.earnFlexDollars("user1", -2.0);
	    assertEquals(0.0, billingService.getFlexDollarsForUser("user1"), 0.0);
	}
	
	@Test
	public void testPreviewGeneratesBilling() {
	    Billing bill = billingService.preview("user1", trip, "Station B", new Date());
	    assertEquals(210, bill.getAmountCents());
	}
	
    @Test
    public void testPreviewNullDate() {
        Billing bill = billingService.preview("user1", trip, "Station B", null);
        assertEquals(210, bill.getAmountCents());
    }
    
    @Test
    public void testPreviewBlankArrival() {
        Billing bill = billingService.preview("user1", trip, "", new Date());
        assertEquals(210, bill.getAmountCents());
    }
	
	@Test
	public void testCalculateAndStoreNoDiscounts() {
	    Billing bill = billingService.calculateAndStore("user1", trip, false, LoyaltyTier.NONE);
	    assertEquals(210, bill.getAmountCents());
	}
	
	@Test
	public void testCalculateAndStoreOperatorDiscount() {
	    Billing bill = billingService.calculateAndStore("user1", trip, true, LoyaltyTier.NONE);
	    assertEquals(189, bill.getAmountCents());
	}
	
	@Test
	public void testCalculateAndStoreBronzeDiscount() {
	    Billing bill = billingService.calculateAndStore("user1", trip, false, LoyaltyTier.BRONZE);
	    assertEquals(200, bill.getAmountCents());
	}
	
	@Test
	public void testCalculateAndStoreSilverDiscount() {
	    Billing bill = billingService.calculateAndStore("user1", trip, false, LoyaltyTier.SILVER);
	    assertEquals(189, bill.getAmountCents());
	}
	
	@Test
	public void testCalculateAndStoreGoldDiscount() {
	    Billing bill = billingService.calculateAndStore("user1", trip, false, LoyaltyTier.GOLD);
	    assertEquals(179, bill.getAmountCents());
	}
	
	@Test
	public void testApplyFlexDollarsCostZero() {
	    billingService.earnFlexDollars("user1", 5.0);
	    trip.setCost(0.0);
	    Billing bill = billingService.calculateAndStore("user1", trip, false);
	    assertEquals(0, bill.getAmountCents());
	}

	@Test
	public void testApplyFlexDollarsPartialFlex() {
	    billingService.earnFlexDollars("user1", 1.0);
	    Billing bill = billingService.calculateAndStore("user1", trip, false);
	    assertEquals(110, bill.getAmountCents());
	}

	@Test
	public void testApplyFlexDollarsFullFlex() {
	    billingService.earnFlexDollars("user1", 100.0);
	    Billing bill = billingService.calculateAndStore("user1", trip, false);
	    assertEquals(0, bill.getAmountCents());
	}
	
	@Test
	public void testGetByTripIdReturnsNull() {
	    assertNull(billingService.getByTripId("qqqq"));
	}
	
	@Test
	public void testListForUserEmpty() {
	    assertTrue(billingService.listForUser("newUser").isEmpty());
	}
}
