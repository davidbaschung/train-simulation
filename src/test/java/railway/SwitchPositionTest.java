package railway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sx.SX;

import static org.junit.jupiter.api.Assertions.*;

class SwitchPositionTest {

	private Switch sw101;
	private Sector s102;
	private SwitchPosition instance;

	@BeforeEach
	void setUp() {
		SX.startEmulation();
		SX.instance().configPort("", 0, 0, 0, 0);
		SX.instance().initPort();
		s102 = new Sector("s102", (byte) 10, (byte) 2, 15);
		sw101 = new Switch("sw101", (byte) 10, (byte) 1, s102);
		instance = new SwitchPosition(sw101, true);
	}

	@Test
	void setPosition() {
		assertFalse(sw101.getPosition());
		instance.setPosition();
		assertTrue(sw101.getPosition());
	}

	@Test
	void getPosition() {
	}

	@Test
	void getSector() {
		assertEquals(s102, instance.getSector());
	}

	@Test
	void getId() {
		assertTrue(instance.getId().equals("sw101"));
	}
}