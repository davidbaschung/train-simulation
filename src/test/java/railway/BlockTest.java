package railway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sx.SX;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

	private Signal a1;
	private Signal a2;
	private Block instance;
	private ArrayList<Block> nextBlocks;

	private Sector s101, s102, s103;
	private ArrayList<Sector> sectors;
	private SwitchPosition sp131;
	private Switch sw131;
	private ArrayList<SwitchPosition> switchPositions;

	@BeforeEach
	void setUp() {
		SX.startEmulation();
		SX.instance().configPort("", 0, 0, 0, 0);
		SX.instance().initPort();
		s101 = new Sector("s101", (byte) 10, (byte) 1, 10);
		s102 = new Sector("s102", (byte) 10, (byte) 2, 15);
		s103 = new Sector("s103", (byte) 10, (byte) 3, 20);
		sectors = new ArrayList<>();
		sectors.add(s101);
		sectors.add(s102);
		sectors.add(s103);
		a1 = new Signal("A1", (byte) 11);
		a2 = new Signal("A2", (byte) 12);
		sw131 = new Switch("sw13", (byte) 13, (byte) 1, s102);
		sp131 = new SwitchPosition(sw131, true);
		switchPositions = new ArrayList<SwitchPosition>();
		switchPositions.add(sp131);
		nextBlocks = new ArrayList<Block>();
		instance = new Block("block", "A1", "A2", false, (byte) 23, sectors, switchPositions);
	}

	@Test
	void addNextBlocks() {
		Block b2 = new Block("block", "A1", "A2", true, (byte) 23, sectors, switchPositions);
		nextBlocks.add(b2);
		instance.addNextBlocks(nextBlocks);
		assertTrue(instance.getNextBlocks().contains(b2));
		assertEquals(1, instance.getNextBlocks().size());
	}

	@Test
	void getStartId() {
		assertTrue(instance.getStartId().equals("A1"));
	}

	@Test
	void getEndId() {
		assertTrue(instance.getEndId().equals("A2"));
	}

	@Test
	void getMaxSpeed() {
		assertEquals((byte) 23, instance.getMaxSpeed());
	}

	@Test
	void setMaxSpeed() {
		instance.setMaxSpeed((byte) 17);
		assertEquals((byte) 17, instance.getMaxSpeed());

		// Reset
		instance.setMaxSpeed((byte) 23);
	}

	@Test
	void getDirection() {
		assertFalse(instance.getDirection());
	}

	@Test
	void setDirection() {
		instance.setDirection(true);
		assertTrue(instance.getDirection());
		instance.setDirection(false);
		assertFalse(instance.getDirection());
	}

	@Test
	void getFirstSector() {
		assertEquals(s101, instance.getFirstSector());
	}

	@Test
	void getLastSector() {
		assertEquals(s103, instance.getLastSector());
	}

	@Test
	void getNextBlocks() {
	}

	@Test
	void getSectors() {
		assertTrue(instance.getSectors() != null);
		assertEquals(3, instance.getSectors().size());
		assertTrue(instance.getSectors().contains(s101));
		assertTrue(instance.getSectors().contains(s102));
		assertTrue(instance.getSectors().contains(s103));
	}

	@Test
	void getLength() {
		assertEquals(45.0f, 0.1f, instance.getLength());
	}

	@Test
	void setLength() {
		instance.setLength(54.0f);
		assertEquals(54.0f, 0.1f, instance.getLength());
		// Reset
		instance.setLength(45.0f);
	}

	@Test
	void getCrossingTime() {
		assertEquals(instance.getLength(), 45.0, 01.f);
		assertEquals(instance.getMaxSpeed(), 23, 0.1f);
		assertEquals(45.0f / 23.0f, instance.getCrossingTime(), 0.1f);
	}

	@Test
	void getStartSignal() {
	}

	@Test
	void setStartSignal() {
		instance.setStartSignal(a2);
		assertEquals(a2, instance.getStartSignal());
	}

	@Test
	void getEndSignal() {
	}

	@Test
	void setEndSignal() {
	}

	@Test
	void isOccupied() {
		assertFalse(instance.isOccupied());
		SX.instance().setStatusBit(s102.address, s102.bitpos, true);
		assertTrue(instance.isOccupied());
		SX.instance().setStatusBit(s102.address, s102.bitpos, false);
	}

	@Test
	void isLocked() {
	}

	@Test
	void isSecurable() {
		assertTrue(instance.isSecurable());
		s102.lock(s103);
		assertFalse(instance.isSecurable());
		s102.unlock(s103);
	}

	@Test
	void lockSectors() {
		assertFalse(s101.isLocked());
		assertFalse(s102.isLocked());
		assertFalse(s103.isLocked());
		instance.lockSectors();
		assertTrue(s101.isLockedBy(instance));
		assertTrue(s102.isLockedBy(instance));
		assertTrue(s103.isLockedBy(instance));
	}

	@Test
	void unlockSectors() {
		instance.unlockSectors();
		assertFalse(s101.isLockedBy(instance));
		assertFalse(s102.isLockedBy(instance));
		assertFalse(s103.isLockedBy(instance));
	}

	@Test
	void setSwitches() {
		assertFalse(SX.instance().getStatusBit(sw131.address, sw131.bitpos));
		instance.setSwitches();
		assertTrue(SX.instance().getStatusBit(sw131.address, sw131.bitpos));
		s102.lock(instance);
		instance.setSwitches();
		assertTrue(SX.instance().getStatusBit(sw131.address, sw131.bitpos));
	}

	@Test
	void isFirstSectorOccupied() {
		assertFalse(instance.isFirstSectorOccupied());
		SX.instance().setStatusBit(s101.address, s101.bitpos, true);
		assertTrue(instance.isFirstSectorOccupied());
		SX.instance().setStatusBit(s101.address, s101.bitpos, false);
	}

	@Test
	void isLastSectorOccupied() {
		assertFalse(instance.isLastSectorOccupied());
		SX.instance().setStatusBit(s103.address, s103.bitpos, true);
		assertTrue(instance.isLastSectorOccupied());
		SX.instance().setStatusBit(s103.address, s103.bitpos, false);
	}

	@Test
	void getSwitchPositions() {
		assertEquals(switchPositions, instance.getSwitchPositions());
	}

	@Test
	void containsStop() {
		assertEquals(false, instance.containsStop());
	}
}