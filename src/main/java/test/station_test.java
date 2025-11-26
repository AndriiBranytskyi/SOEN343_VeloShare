package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.veloshare.domain.Bike;
import com.veloshare.domain.Station;
import com.veloshare.domain.StationReadModel;

public class station_test {
	
	private Station createStationWithTwoBikes() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    station.getDocks().get(0).occupy(new Bike("bike1", "standard"));
	    station.getDocks().get(1).occupy(new Bike("bike2", "e-bike"));
	    return station;
	}
	
    private StationReadModel createStationReadModel() {
        return new StationReadModel("Station A", 45.4532, -73.564, 4, 2, 2);
    }
	
	@Test
	public void testGetName() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertEquals("Station A", station.getName());
	}
	
	@Test
	public void testGetLatitude() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertEquals(45.4532, station.getLatitude(), 0.0);
	}
	
	@Test
	public void testGetLongitude() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertEquals(-73.564, station.getLongitude(), 0.0);
	}
	
	@Test
	public void testGetCapacity() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertEquals(4, station.getCapacity());
	}
	
	@Test
	public void testGetAddress() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertEquals("123 Sainte-Catherine St W", station.getAddress());
	}
	
	@Test
	public void testGetDocks() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertEquals(4, station.getDocks().size());
	}
	
	@Test
	public void testGetBikesAvailable() {
	    Station station = createStationWithTwoBikes();
	    assertEquals(2, station.getBikesAvailable());
	}
	
	@Test
	public void testGetFreeDocks() {
	    Station station = createStationWithTwoBikes();
	    assertEquals(2, station.getFreeDocks());
	}
	
	@Test
	public void testIsFull() {
		Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    station.getDocks().get(0).occupy(new Bike("bike1", "standard"));
	    station.getDocks().get(1).occupy(new Bike("bike2", "e-bike"));
	    station.getDocks().get(2).occupy(new Bike("bike3", "standard"));
	    station.getDocks().get(3).occupy(new Bike("bike4", "e-bike"));
	    assertTrue(station.isFull());
	}
	@Test
	public void testIsFullOnEmpty() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertFalse(station.isFull());
	}
	
	@Test
	public void testIsEmpty() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertTrue(station.isEmpty());
	}
	
	@Test
	public void testIsEmptyOnNotEmpty() {
	    Station station = createStationWithTwoBikes();
	    assertFalse(station.isEmpty());
	}
	
	@Test
	public void testSetOutOfService() {
	    Station station = createStationWithTwoBikes();
	    station.setOutOfService(true);
	    assertTrue(station.isOutOfService());
	}
	
	@Test
	public void testGetFillRatio() {
	    Station station = createStationWithTwoBikes();
	    assertEquals(0.5, station.getFillRatio(), 0.0);
	}
	
	@Test
	public void testGetFillRatioOnEmpty() {
	    Station station = new Station("Station A", 45.4532, -73.564, 0, "123 Sainte-Catherine St W");
	    assertEquals(1.0, station.getFillRatio(), 0.0);
	}
	
	@Test
	public void testIsBelowMinimumCapacity() {
	    Station station = createStationWithTwoBikes();
	    assertFalse(station.isBelowMinimumCapacity());
	}
	
	@Test
	public void testIsBelowMinimumCapacityOnEmpty() {
	    Station station = new Station("Station A", 45.4532, -73.564, 4, "123 Sainte-Catherine St W");
	    assertTrue(station.isBelowMinimumCapacity());
	}
	
	@Test
    public void testGetNameStationReadModel() {
        StationReadModel model = createStationReadModel();
        assertEquals("Station A", model.getName());
    }

    @Test
    public void testGetLatitudeStationReadModel() {
        StationReadModel model = createStationReadModel();
        assertEquals(45.4532, model.getLatitude(), 0.0);
    }

    @Test
    public void testGetLongitudeStationReadModel() {
        StationReadModel model = createStationReadModel();
        assertEquals(-73.564, model.getLongitude(), 0.0);
    }

    @Test
    public void testGetCapacityStationReadModel() {
        StationReadModel model = createStationReadModel();
        assertEquals(4, model.getCapacity());
    }

    @Test
    public void testGetBikesAvailableStationReadModel() {
        StationReadModel model = createStationReadModel();
        assertEquals(2, model.getBikesAvailable());
    }

    @Test
    public void testGetFreeDocksStationReadModel() {
        StationReadModel model = createStationReadModel();
        assertEquals(2, model.getFreeDocks());
    }
}
