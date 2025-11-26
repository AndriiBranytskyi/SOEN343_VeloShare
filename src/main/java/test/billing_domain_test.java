package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

import com.veloshare.domain.Billing;



public class billing_domain_test {
	
	private Billing createBilling(Date start, Date end) {
		return new Billing("trip1","user","bike1","station A","station B",start,end,15,350);
	}
	
	@Test
	public void testGetTripId() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertEquals("trip1", billing.getTripId());
	}
	
	@Test
	public void testGetUserId() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertEquals("user", billing.getUserId());
	}
	
	@Test
	public void testGetBikeId() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertEquals("bike1", billing.getBikeId());
	}
	
	@Test
	public void testGetOriginStation() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertEquals("station A", billing.getOriginStation());
	}
	
	@Test
	public void testGetArrivalStation() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertEquals("station B", billing.getArrivalStation());
	}
	
	@Test
	public void testGetStartTimeNotNull() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertNotNull(billing.getStartTime());
	}
	
	@Test
	public void testGetEndTimeNotNull() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertNotNull(billing.getEndTime());
	}
	
	@Test
	public void testGetStartTimeNull() {
	    Billing billing = createBilling(null, new Date());
	    assertNull(billing.getStartTime());
	}
	
	@Test
	public void testGetEndTimeNull() {
	    Billing billing = createBilling(new Date(), null);
	    assertNull(billing.getEndTime());
	}
	
	@Test
	public void testGetMinutesBilled() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertEquals(15, billing.getMinutesBilled());
	}
	
	@Test
	public void testGetAmountCents() {
	    Billing billing = createBilling(new Date(), new Date());
	    assertEquals(350, billing.getAmountCents());
	}
	
	@Test
	public void testSetAndGetPaymentId() {
	    Billing billing = createBilling(new Date(), new Date());
	    billing.setPaymentId("pay1");
	    assertEquals("pay1", billing.getPaymentId());
	}
	
	@Test
	public void testSetAndGetBaseAmountCents() {
	    Billing billing = createBilling(new Date(), new Date());
	    billing.setBaseAmountCents(200);
	    assertEquals(200, billing.getBaseAmountCents());
	}
	
	@Test
	public void testSetAndGetFlexUsedCents() {
	    Billing billing = createBilling(new Date(), new Date());
	    billing.setFlexUsedCents(50);
	    assertEquals(50, billing.getFlexUsedCents());
	}
	
	@Test
	public void testBaseFeeConstant() {
	    assertEquals(200, Billing.BASE_FEE_CENTS);
	}
	
	@Test
	public void testPerMinuteFeeConstant() {
	    assertEquals(10, Billing.PER_MINUTE_FEE_CENTS);
	}
}
