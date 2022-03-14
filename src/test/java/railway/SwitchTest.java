package railway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sx.SX;

import static org.junit.jupiter.api.Assertions.*;

class SwitchTest {

	private Sector s101;
	private Switch instance;

	@BeforeEach
	void setUp() {
		SX.startEmulation();
		SX.instance().configPort("", 0, 0, 0, 0);
		SX.instance().initPort();
		s101 = new Sector("s101", (byte) 10, (byte) 1, 10);
		instance = new Switch("sw131", (byte) 13, (byte) 1, s101);
	}

	@Test
	void getPosition() {
		assertFalse(instance.getPosition());
		SX.instance().setStatusBit(instance.address, instance.bitpos, true);
		assertTrue(instance.getPosition());
	}

	@Test
	void setPosition() {
		instance.setPosition(false);
		assertFalse(instance.getPosition());
		instance.setPosition(true);
		assertTrue(instance.getPosition());
		instance.setPosition(false);
		assertFalse(instance.getPosition());
	}

	@Test
	void getSector() {
		assertEquals(s101, instance.getSector());
	}
}