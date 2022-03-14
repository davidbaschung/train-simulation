package railway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sx.SX;

import static org.junit.jupiter.api.Assertions.*;

class SectorTest {

	private byte address;
	private byte bitpos;
	private Sector instance;

	@BeforeEach
	void setUp() {
		SX.startEmulation();
		SX.instance().configPort("", 0, 0, 0, 0);
		SX.instance().initPort();
		address = 12;
		bitpos = 3;
		instance = new Sector("testSector", address, bitpos, 35);
	}

	@Test
	void isLocked() {

	}

	@Test
	void isLockedBy() {
	}

	@Test
	void isOccupied() {
		assertFalse(instance.isOccupied());
		SX.instance().setStatusBit(address, bitpos, true);
		assertTrue(instance.isOccupied());
		SX.instance().setStatusBit(address, bitpos, false);
		assertFalse(instance.isOccupied());
	}

	@Test
	void getLength() {
		instance.setLength(15);
		assertEquals(15, instance.getLength());
	}

	@Test
	void setLength() {
		instance.setLength(25);
		assertEquals(25, instance.getLength());
	}

	@Test
	void lock() {
	}

	@Test
	void hasSwitch() {
		instance.setHasSwitch(false);
		assertEquals(false, instance.hasSwitch());
		instance.setHasSwitch(true);
		assertEquals(true, instance.hasSwitch());
	}

	@Test
	void setHasSwitch() {
		instance.setHasSwitch(false);
		assertEquals(false, instance.hasSwitch());
		instance.setHasSwitch(true);
		assertEquals(true, instance.hasSwitch());
	}

	@Test
	void unlock() {
	}
}