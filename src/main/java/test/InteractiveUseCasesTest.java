package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.veloshare.application.Result;
import com.veloshare.application.dto.EndTripCmd;
import com.veloshare.application.dto.ReserveBikeCmd;
import com.veloshare.application.dto.StartTripCmd;
import com.veloshare.application.tasks.ReservationTasks;
import com.veloshare.application.usecases.BillingService;
import com.veloshare.application.usecases.LoyaltyService;
import com.veloshare.application.usecases.ReservationService;
import com.veloshare.application.usecases.TripService;
import com.veloshare.domain.Billing;
import com.veloshare.domain.RideHistoryAdapter;
import com.veloshare.domain.Role;
import com.veloshare.domain.TripFactory;
import com.veloshare.domain.User;
import com.veloshare.domain.bmsService;

public class InteractiveUseCasesTest {
	
	bmsService bms;
	User user;
	
	@Before
	public void before_init() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		user = new User("1ef5I5NH4qXYtJX2B55BBvy8jVc2", "admin1@gmail.com", Role.OPERATOR);
		bms = new bmsService();
		bms.loadConfig("./config.json");
		
		// Reset private static counter in TripFactory
		Field counterField = TripFactory.class.getDeclaredField("tripCounter");
	    counterField.setAccessible(true); // bypass private
	    counterField.setInt(null, 0); // reset static field to 0
	}
	
	@After 
	public void after_clean() {
		user =null;
		bms =null;
	}
	
	@Test
    public void happy_path() {
		
		// Check if stations are loaded
		int nStations = bms.getStations().size();
		assertTrue("Stations not loaded", nStations>0);
		
		// Reserve with admin1 (dual role) account
	    ReserveBikeCmd reserveCmd = new ReserveBikeCmd(user.getUserId(), "bike1", "Station A", 10);
	    ReservationService resService = new ReservationService(bms);
		Result<Void> rReserv = resService.reserve(reserveCmd);
		assertTrue("Reservation must return ok", rReserv.isOk());
		
        // Start trip and check id
        BillingService billService = new BillingService();
        LoyaltyService loyaltySevice = new LoyaltyService(new RideHistoryAdapter(bms));
        TripService tripService = new TripService(bms, billService, loyaltySevice);
        StartTripCmd startCmd = new StartTripCmd(user.getUserId(), "bike1", "Station A", 0.0, 0.0);
        Result<String> rStartTrip = tripService.startTrip(startCmd, user);
        assertNotNull("startTrip must return trip id",rStartTrip.getValue());
        
        // End trip and check bill and summary
		EndTripCmd endCmd = new EndTripCmd(rStartTrip.getValue(), "Station C");
		Result<Billing> rBill = tripService.endTripAndBill(endCmd, user, user.getRole() == Role.OPERATOR);
		assertEquals("Price billed is wrong",189, rBill.getValue().getAmountCents()); //price
		assertEquals("Wrong departure station","Station A", rBill.getValue().getOriginStation()); //departure
		assertEquals("Wrong arrival station","Station C", rBill.getValue().getArrivalStation()); // arrival
		assertEquals("Wrong duration",1, rBill.getValue().getMinutesBilled()); // duration of trip
		assertEquals("Wrong bike id","bike1", rBill.getValue().getBikeId()); // bike id
		assertEquals("Wrong trip id","trip1", rBill.getValue().getTripId()); // trip id
		assertEquals("Wrong user id","1ef5I5NH4qXYtJX2B55BBvy8jVc2", rBill.getValue().getUserId()); // user id 
		assertNotNull("Start time must be valid",rBill.getValue().getStartTime()); // start time valid
		assertNotNull("End time must be valid",rBill.getValue().getEndTime()); // end time valid
		assertNotNull("Paymnet id must be valid",rBill.getValue().getPaymentId()); // payment id valid
    }
	
	@Test
	public void station_full() {
		
		// Check if stations are loaded
		int nStations = bms.getStations().size();
		assertTrue("Stations not loaded", nStations>0);
		
		// Reserve with admin1 (dual role) account
	    ReserveBikeCmd reserveCmd = new ReserveBikeCmd(user.getUserId(), "bike1", "Station A", 10);
	    ReservationService resService = new ReservationService(bms);
		Result<Void> rReserv = resService.reserve(reserveCmd);
		assertTrue("Reservation must return ok", rReserv.isOk());
		
        // Start trip and check id
        BillingService billService = new BillingService();
        LoyaltyService loyaltySevice = new LoyaltyService(new RideHistoryAdapter(bms));
        TripService tripService = new TripService(bms, billService, loyaltySevice);
        StartTripCmd startCmd = new StartTripCmd(user.getUserId(), "bike1", "Station A", 0.0, 0.0);
        Result<String> rStartTrip = tripService.startTrip(startCmd, user);
        assertNotNull("startTrip must return trip id",rStartTrip.getValue());
		
        // Attempt to end trip to full station (Station B)
		EndTripCmd endCmd = new EndTripCmd(rStartTrip.getValue(), "Station B"); 
		Result<Billing> rBill = tripService.endTripAndBill(endCmd, user, user.getRole() == Role.OPERATOR);
		
		assertNull(rBill.getValue()); //No billing must be computed
		assertEquals("Wrong error message","No free docks at Station B. Please return your bike to another nearby station", rBill.getError()); // Correct error message
		
		assertNotNull("Trip must be active",bms.getActiveTrip(rStartTrip.getValue())); // Trip is still active
		assertNull("End station must be null",bms.getActiveTrip(rStartTrip.getValue()).getEndStation()); // End station is null
		
	}
	
	@Test
	public void reservation_expiry() {
		// Check if stations are loaded
		int nStations = bms.getStations().size();
		assertTrue("Stations not loaded", nStations>0);
		
		// Reserve with admin1 (dual role) account
	    ReserveBikeCmd reserveCmd = new ReserveBikeCmd(user.getUserId(), "bike1", "Station A", 0);
	    ReservationService resService = new ReservationService(bms);
		Result<Void> rReserv = resService.reserve(reserveCmd);
		assertTrue("Reservation must return ok", rReserv.isOk()); // Immediately reservation is active
		
		ReservationTasks rt = new ReservationTasks(bms);
		try {
			// Usually automatically called every 30 seconds to check for expire reservations(application/tasks)
			// Then Frontend catches it and logs message
			Thread.sleep(10); // Slow down test so expire dates are correctly compared
			rt.expireReservations(); 
			fail("Reservation must be already expired and throw error message");
		}
		catch (Exception e) {
			assertEquals("Wrong message for expired reservation","Reservation resbike11ef5I5NH4qXYtJX2B55BBvy8jVc2 not found or already cancelled", e.getMessage());
		}
	}
	
	@Test 
	public void rebalancing() {
		// Check if stations are loaded
		int nStations = bms.getStations().size();
		assertTrue("Stations not loaded", nStations>0);
		
		//First trip to remove first bike from Station A
		// Reserve with admin1 (dual role) account
	    ReserveBikeCmd reserveCmd = new ReserveBikeCmd(user.getUserId(), "bike1", "Station A", 10);
	    ReservationService resService = new ReservationService(bms);
		Result<Void> rReserv = resService.reserve(reserveCmd);
		assertTrue("Reservation must return ok", rReserv.isOk());
		
        // Start trip and check id
        BillingService billService = new BillingService();
        LoyaltyService loyaltySevice = new LoyaltyService(new RideHistoryAdapter(bms));
        TripService tripService = new TripService(bms, billService, loyaltySevice);
        StartTripCmd startCmd = new StartTripCmd(user.getUserId(), "bike1", "Station A", 0.0, 0.0);
        Result<String> rStartTrip = tripService.startTrip(startCmd, user);
        assertNotNull("startTrip must return trip id",rStartTrip.getValue());
        
        // End trip and check bill and summary
		EndTripCmd endCmd = new EndTripCmd(rStartTrip.getValue(), "Station C");
		tripService.endTripAndBill(endCmd, user, user.getRole() == Role.OPERATOR);
		
		//Second trip to remove last bike from Station A
		// Reserve with admin1 (dual role) account
	    ReserveBikeCmd reserveCmd1 = new ReserveBikeCmd(user.getUserId(), "bike2", "Station A", 10);
	    ReservationService resService1 = new ReservationService(bms);
		Result<Void> rReserv1 = resService1.reserve(reserveCmd1);
		assertTrue("Second reservation must return ok", rReserv1.isOk());
		
        // Start trip and check id
        StartTripCmd startCmd1 = new StartTripCmd(user.getUserId(), "bike2", "Station A", 0.0, 0.0);
        Result<String> rStartTrip1 = tripService.startTrip(startCmd1, user);
        assertNotNull("startTrip must return trip id",rStartTrip1.getValue());
        
        // End trip and check bill and summary
		EndTripCmd endCmd1 = new EndTripCmd(rStartTrip1.getValue(), "Station C");
		tripService.endTripAndBill(endCmd1, user, user.getRole() == Role.OPERATOR);
		
		// Check if Station A is empty 
		// When station is empty message is shown to operator (logic in index.html #btnStartTrip)
		boolean isStationEmpty = bms.requireStation("Station A").getFreeDocks() == bms.requireStation("Station A").getCapacity();
		assertTrue("Station must be empty", isStationEmpty);
		
	}
	
}
