package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.veloshare.domain.Bike;
import com.veloshare.domain.Dock;

public class dock_test {
	
	@Test
	public void testInitialStatus() {
	    Dock dock = new Dock(1, 10);
	    assertNotNull(dock.getStatus());
	}
	
	@Test
	public void testGetDockId() {
	    Dock dock = new Dock(2, 20);
	    assertEquals(2, dock.getDockId());
	}
	
	@Test
	public void testGetPosition() {
	    Dock dock = new Dock(3, 30);
	    assertEquals(30, dock.getPosition());
	}
	
	@Test
	public void testGetBikeInitiallyNull() {
	    Dock dock = new Dock(4, 40);
	    assertNull(dock.getBike());
	}
	
	@Test
	public void testIsOccupiedInitially() {
	    Dock dock = new Dock(5, 50);
	    assertFalse(dock.isOccupied());
	}
	
	@Test
	public void testOccupy() {
	    Dock dock = new Dock(6, 60);
	    Bike bike = new Bike("bike1", "Standard");
	    dock.occupy(bike);
	    assertTrue(dock.isOccupied());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testOccupyOccupy() {
	    Dock dock = new Dock(7, 70);
	    Bike bike1 = new Bike("bike1", "Standard");
	    Bike bike2 = new Bike("bike1", "Standard");
	    dock.occupy(bike1);
	    dock.occupy(bike2);
	}
	
	@Test
	public void testRelease() {
	    Dock dock = new Dock(8, 80);
	    Bike bike = new Bike("bike1", "Standard");
	    dock.occupy(bike);
	    dock.release();
	    assertFalse(dock.isOccupied());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testReleaseEmpty() {
	    Dock dock = new Dock(9, 90);
	    dock.release();
	}
	
	@Test
	public void testSetOutOfService() {
	    Dock dock = new Dock(10, 100);
	    dock.setOutOfService();
	    assertFalse(dock.isOccupied());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSetOutOfServiceIfOccupied() {
	    Dock dock = new Dock(11, 110);
	    Bike bike = new Bike("bike1", "Standard");
	    dock.occupy(bike);
	    dock.setOutOfService();
	}
}
