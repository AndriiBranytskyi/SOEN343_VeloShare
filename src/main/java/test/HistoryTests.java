package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.veloshare.domain.Bike;
import com.veloshare.domain.RideHistoryAdapter;
import com.veloshare.domain.Role;
import com.veloshare.domain.Station;
import com.veloshare.domain.Trip;
import com.veloshare.domain.TripFactory;
import com.veloshare.domain.User;
import com.veloshare.domain.bmsService;

public class HistoryTests {
	
	private bmsService bms;
	private User user;
	private RideHistoryAdapter adapter;
	
	private Station createStation() {
	    return new Station("Station A", 45.4532, -73.564, 2, "123 Sainte-Catherine St W");
	}
	
	private Bike createBike() {
	    return new Bike("bike1", "standard");
	}
	
	private Trip createTrip() {
	    return new Trip("trip1", "bike1", user.getUserId(), createStation(), 10.0, 5.0, createBike());
	}
	
	@Before
	public void before_init() throws Exception {
	    user = new User("user-op", "admin1@gmail.com", Role.OPERATOR);
	    bms = new bmsService();
	    bms.loadConfig("./config.json");
	    adapter = new RideHistoryAdapter(bms);
	
	    // Reset TripFactory counter
	    Field counterField = TripFactory.class.getDeclaredField("tripCounter");
	    counterField.setAccessible(true);
	    counterField.setInt(null, 0);
	}
	
	@After
	public void after_clean() {
	    user = null;
	    bms = null;
	    adapter = null;
	}
	
	// searchByTripId tests
	@Test
	public void testSearchByTripId() {
	    Trip trip = createTrip();
	    trip.endTrip(new Station("Station B", 45.0, -73.5, 2, "456 Another St"));
	    bms.getRideHistory().recordCompleted(trip);
	
	    Object result = adapter.searchByTripId(trip.getTripId(), user);
	    assertEquals(trip, result);
	}
	
	@Test
	public void testSearchByTripIdNull() {
	    Object t = adapter.searchByTripId("qqq", user);
	    assertNull(t);
	}
	
	@Test
	public void testSearchByTripIdNullTripId() {
	    Object result = adapter.searchByTripId(null, user);
	    assertNull(result);
	}
	
	@Test (expected = Exception.class)
	public void testSearchByTripIdNullUser() {
	    Trip trip = createTrip();
	    trip.endTrip(new Station("Station B", 45.0, -73.5, 2, "456 Another St"));
	    bms.getRideHistory().recordCompleted(trip);
	
	    adapter.searchByTripId(trip.getTripId(), null);
	}
	
    // filter tests
    @Test
    public void testFilter() {
        Trip trip = createTrip();
        trip.endTrip(new Station("Station B", 45.0, -73.5, 2, "456 Another St"));
        bms.getRideHistory().recordCompleted(trip);

        List<?> results = adapter.filter(user, null, null, null);
        assertTrue(results.contains(trip));
    }

    @Test
    public void testFilterInvalidStartDate() {
        Trip trip = createTrip();
        trip.endTrip(createStation());
        bms.getRideHistory().recordCompleted(trip);

        Date future = new Date(System.currentTimeMillis() + 100000);
        List<?> results = adapter.filter(user, future, null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testFilterInvalidEndDate() {
        Trip trip = createTrip();
        trip.endTrip(createStation());
        bms.getRideHistory().recordCompleted(trip);

        Date past = new Date(System.currentTimeMillis() - 100000);
        List<?> results = adapter.filter(user, null, past, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testFilterMatchesBikeType() {
        Bike ebike = new Bike("bike2", "e-bike");
        Trip trip = new Trip("trip3", ebike.getId(), user.getUserId(), createStation(), 10.0, 5.0, ebike);
        trip.endTrip(createStation());
        bms.getRideHistory().recordCompleted(trip);

        List<?> results = adapter.filter(user, null, null, "e-bike");
        assertEquals(1, results.size());
    }

    @Test
    public void testFilterNoTrips() {
        List<?> results = adapter.filter(user, null, null, null);
        assertTrue(results.isEmpty());
    }
    
	// getDetails tests
	@Test
	public void testGetDetails() {
	    Trip trip = createTrip();
	    trip.endTrip(createStation());
	    bms.getRideHistory().recordCompleted(trip);
	
	    Map<String,Object> details = adapter.getDetails(trip.getTripId(), user);
	    assertEquals(trip.getTripId(), details.get("tripId"));
	}
	
	@Test
	public void testGetDetailsNull() {
	    Map<String,Object> details = adapter.getDetails("qqqqq", user);
	    assertNull(details);
	}
	@Test
	public void testGetDetailsHandlesNullBike() {
	    Trip trip = new Trip("tripNullBike", "", user.getUserId(), createStation(), 10.0, 5.0, null);
	    trip.endTrip(createStation());
	    bms.getRideHistory().recordCompleted(trip);
	
	    Map<String,Object> details = adapter.getDetails(trip.getTripId(), user);
	    assertEquals("standard", details.get("bikeType"));
	}
	
	// recordClaimedReservation / countClaimedReservations
	@Test
    public void testRecordAndCountClaimedReservation() {
        Date now = new Date();
        bms.getRideHistory().recordClaimedReservation(user.getUserId(), "res1", now);

        int count = adapter.countClaimedReservations(user.getUserId(),new Date(), new Date(now.getTime() + 10000));
        assertEquals(1, count);
    }

    @Test
    public void testCountClaimedReservationOutsideRange() {
        Date now = new Date();
        bms.getRideHistory().recordClaimedReservation(user.getUserId(), "res2", now);

        int count = adapter.countClaimedReservations(user.getUserId(),new Date(), new Date(now.getTime() - 1000));
        assertEquals(0, count);
    }

    // recordMissedReservation / countMissedReservations
    @Test
    public void testRecordAndCountMissedReservation() {
        Date now = new Date();
        bms.getRideHistory().recordMissedReservation(user.getUserId(), "res4", now);

        int count = adapter.countMissedReservations(user.getUserId(),new Date(now.getTime() - 1000), new Date(now.getTime() + 1000));
        assertEquals(1, count);
    }

    @Test
    public void testCountMissedReservationOutsideRange() {
        Date now = new Date();
        bms.getRideHistory().recordMissedReservation(user.getUserId(), "res5", now);

        int count = adapter.countMissedReservations(user.getUserId(),new Date(), new Date(now.getTime() - 1000));
        assertEquals(0, count);
    }

    // countTrips
    @Test
    public void testCountTrips() {
        Trip trip = createTrip();
        trip.endTrip(createStation());
        bms.getRideHistory().recordCompleted(trip);

        Date from = new Date(trip.getStartTime().getTime() - 1000);
        Date to = new Date(trip.getStartTime().getTime() + 1000);
        int count = adapter.countTrips(user.getUserId(), from, to);
        assertEquals(1, count);
    }

    @Test
    public void testCountTripsOutsideRange() {
        Trip trip = createTrip();
        trip.endTrip(createStation());
        bms.getRideHistory().recordCompleted(trip);

        Date from = new Date(trip.getStartTime().getTime() + 1000);
        Date to = new Date(trip.getStartTime().getTime() + 2000);
        int count = adapter.countTrips(user.getUserId(), from, to);
        assertEquals(0, count);
    }
    
}
