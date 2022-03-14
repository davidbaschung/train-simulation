package railway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sx.SX;

import static org.junit.jupiter.api.Assertions.*;

class LocomotiveTest {

	private Locomotive instance;

	@BeforeEach
	void setUp() {
		SX.startEmulation();
		SX.instance().configPort("", 0, 0, 0, 0);
		SX.instance().initPort();
		instance = new Locomotive("myLoc", (byte) 10, 30, 0);
		instance.reachSpeed((byte) 10);
	}

	@Test
	void emergencyStop() {
		try {
			Thread.sleep(2500); // make it wait longer so the locomotive is properly initialized for the test
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		instance.emergencyStop();
		try {
			Thread.sleep(1000); // make it wait a bit longer maybe to be sure the simulation had time to register the action
		} catch (InterruptedException ignored) {
		}
		assertEquals((byte) 0, instance.getSpeed());
		assertTrue(instance.isEmergencyStopped());
		assertTrue(instance.getSpeedBeforeEmergency() > 0);
		instance.emergencyContinue();
	}

	@Test
	void emergencyContinue() {
		instance.emergencyStop();
		instance.emergencyContinue();
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}
		assertTrue(instance.getSpeed() > 0);
		assertFalse(instance.isEmergencyStopped());
	}

	@Test
	void isEmergencyStopped() {
	}

	@Test
	void getSpeed() {
		byte s = (byte) (SX.instance().getStatusByte((byte) 10) & 0b00011111);
		assertEquals(s, instance.getSpeed());
	}

	@Test
	void getDesiredSpeed() {
		instance.reachSpeed((byte) 17);
		assertEquals(17, instance.getDesiredSpeed());
	}

	@Test
	void getSpeedBeforeEmergency() {
		assertFalse(instance.isEmergencyStopped());
		instance.reachSpeed((byte) 7);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}
		instance.emergencyStop();
		assertEquals(7, instance.getSpeedBeforeEmergency());
		instance.emergencyContinue();
	}

	@Test
	void reachSpeed() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		instance.reachSpeed((byte) 7);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException ignored) {
		}
		assertEquals((byte) 7, instance.getSpeed());
	}

	@Test
	void run() {
	}

	@Test
	void getDirection() {
		assertFalse(instance.getDirection());
		instance.setDirection(true);
		assertTrue(instance.getDirection());
		instance.setDirection(false);
		assertFalse(instance.getDirection());
	}

	@Test
	void setDirection() {
	}

	@Test
	void isLightOn() {
		SX.instance().setStatusBit((byte) 10, (byte) 6, false);
		assertFalse(instance.isLightOn());
		SX.instance().setStatusBit((byte) 10, (byte) 6, true);
		assertTrue(instance.isLightOn());
	}

	@Test
	void setLight() {
	}

	@Test
	void getLength() {
		assertEquals(30, instance.getLength());
	}

	@Test
	void getAddress() {
	}

	@Test
	void getInertia() {
	}
}
