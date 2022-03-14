package sim;

import apps.Main;
import railway.*;
import sim.Segment.End;

import java.util.*;

/**
 * This class simulates events on the railway. It is not absolutely accurate
 * and should be used for debugging purpose. Always test your project with
 * the real railway !
 *
 * @author Mathias Seuret
 */
public class Simulator extends Thread {
	/**
	 * Reference to the simulator.
	 */
	private static Simulator instance = null;

	/**
	 * Stores a reference to the railway.
	 */
	Railway railway;

	/**
	 * Used for building segments.
	 */
	Map<Block, Boolean> headBlock = new HashMap<Block, Boolean>();

	/**
	 * List of all segments of the railway.
	 */
	List<Segment> segments = new ArrayList<Segment>();

	/**
	 * List of all elements of the railway.
	 */
	List<Element> elements = new LinkedList<Element>();


	/**
	 * Constructor of the class. Takes the railway as parameter and
	 * may throw an exception if a train has an invalid signal ID when
	 * the program is started.
	 *
	 * @throws Exception
	 */
	public Simulator() throws Exception {
		Main.getRailwayLogger("Simulation").info("Construction of the simulator");
		this.railway = Railway.instance();
		instance = this;
		findHeadBlocks();
		generateSegments();
		loadTrains();
		addSimSwitches();
		addSimSignals();
		Main.getRailwayLogger("Simulation").info("Simulator constructed");
		start();
	}

	/**
	 * Returns the simulator instance.
	 *
	 * @return
	 */
	public static Simulator get() {
		return instance;
	}

	/**
	 * Used for building the simulator. Headblocks are basically dead-ends.
	 */
	private void findHeadBlocks() {
		// which blocks are heads ?
		Main.getRailwayLogger("Simulation").info("Sim: Looking for head blocks");
		for (Block b : railway.getBlocks()) {
			Boolean head = true;
			if (b.containsStop()) {
				head = false;
			} else {
				for (Block other : railway.getBlocks()) {
					if (other.getNextBlocks().contains(b)) {
						head = false;
						break;
					}
				}
			}
			headBlock.put(b, head);
		}
		Main.getRailwayLogger("Simulation").info("Sim: Done");
	}

	/**
	 * Used for building the simulator. Returns the list of all switches on
	 * the sector, sorted by name.
	 *
	 * @param s
	 * @return
	 */
	public List<Switch> getSortedSwitches(Sector s) {
		List<Switch> result = new ArrayList<Switch>();
		for (Switch sw : Railway.instance().getSwitches()) {
			if (sw.getSector() == s) {
				result.add(sw);
			}
		}
		// not optimized, but there's only one switch most of the time
		for (int i = 0; i < result.size(); i++) {
			for (int j = i + 1; j < result.size(); j++) {
				if (result.get(i).getId().compareTo(result.get(j).getId()) < 0) {
					Switch swap = result.get(i);
					result.set(i, result.get(j));
					result.set(j, swap);
				}
			}
		}
		return result;
	}

	/**
	 * Returns a string encoding with "0"'s and "1"'s the status of all
	 * switches of a given sector on a given block, when the switches
	 * are in the correct position.
	 *
	 * @param b
	 * @param s
	 * @return
	 */
	public String getSwitchString(Block b, Sector s) {
		String result = "";
		// we need the switches in the correct order, let's sort them
		ArrayList<SwitchPosition> positions = new ArrayList<SwitchPosition>();
		for (SwitchPosition sp : b.getSwitchPositions()) {
			if (sp.getSector() != s) continue;
			positions.add(sp);
		}
		// in an unefficient way - fortunately, in most cases there is
		// only one switch :-)
		for (int i = 0; i < positions.size(); i++) {
			for (int j = i + 1; j < positions.size(); j++) {
				if (positions.get(i).getId().compareTo(positions.get(j).getId()) < 0) {
					SwitchPosition swap = positions.get(i);
					positions.set(i, positions.get(j));
					positions.set(j, swap);
				}
			}
			result += positions.get(i).getPosition();
		}
		return result;
	}

	/**
	 * Similar to getSwitchString, but instead returns an integer which
	 * two first bytes are the address and bitpos of the sector, and following
	 * bytes the positions of the switches contained by the sector.
	 *
	 * @param b
	 * @param s
	 * @return
	 */
	public int getSwitchCode(Block b, Sector s) {
		int result = s.address | (s.bitpos << 8);
		int offset = 16;
		// we need the switches in the correct order, let's sort them
		ArrayList<SwitchPosition> positions = new ArrayList<SwitchPosition>();
		for (SwitchPosition sp : b.getSwitchPositions()) {
			if (sp.getSector() != s) continue;
			positions.add(sp);
		}
		// in an unefficient way - fortunately, in most cases there is
		// only one switch :-)
		for (int i = 0; i < positions.size(); i++) {
			for (int j = i + 1; j < positions.size(); j++) {
				if (positions.get(i).getId().compareTo(positions.get(j).getId()) < 0) {
					SwitchPosition swap = positions.get(i);
					positions.set(i, positions.get(j));
					positions.set(j, swap);
				}
			}
			result |= ((positions.get(i).getPosition() ? 1 : 0) << offset++);
		}
		return result;
	}

	/**
	 * Generates the segments of the simulator. Dirty code, my apologies if
	 * somebody has to debug it.
	 */
	private void generateSegments() {
		Main.getRailwayLogger("Simulation").info("Sim: Generating the segments");
		int nbSegments = 0;
		for (Block b : railway.getBlocks()) {
			if (b.containsStop()) {
				continue;
			}
			for (Sector s : b.getSectors()) {
				// a segment can start here if it's the first sector of
				// a headblock, or if there is a switch
				if ((b.getFirstSector() == s && headBlock.get(b) == true)
						|| s.hasSwitch()) {
					Segment seg = new Segment();
					seg.setStart(b, s);
					if ((b.getFirstSector() == s && headBlock.get(b) == true)) {
						seg.start.setDeadEnd(true);
						Main.getRailwayLogger("Simulation").info("Sim: end of railway found at sector " + s);
					}
					if (extendSegment(seg, b, s)) {
						if (b.getDirection()) {
							seg.reverseSegment();
						}
						// not optimized... a getter should be made
						boolean exists = false;
						for (Segment other : segments) {
							if (other.id.equals(seg.id)) {
								exists = true;
								break;
							}
						}
						if (!exists) {
							nbSegments++;
							segments.add(seg);
						}
					}
				}
			}
		}
		Main.getRailwayLogger("Simulation").info("Sim: Done. " + nbSegments + " segments generated");
	}

	/**
	 * Recursively adds sectors to a segment until it reaches a switch.
	 *
	 * @param seg
	 * @param b
	 * @param sec
	 * @return
	 */
	private boolean extendSegment(Segment seg, Block b, Sector sec) {
		boolean done = false;
		int sectorIndex = b.getSectors().indexOf(sec) + 1;
		if ((sectorIndex >= b.getSectors().size() && b.getNextBlocks().isEmpty())) {
			return false;
		}
		do {
			if (sectorIndex < b.getSectors().size()) {
				sec = b.getSectors().get(sectorIndex);
				boolean end = (b.getLastSector() == sec && b.getNextBlocks().isEmpty());
				if (sec.hasSwitch() || end) {
					seg.setStop(b, sec);
					seg.stop.setDeadEnd(end);
					if (end) {
						Main.getRailwayLogger("Simulation").info("Sim: end of railway found at sector " + sec);
					}
					done = true;
				} else {
					seg.addSector(sec);
					sectorIndex++;
				}
			} else {
				sectorIndex = 0;
				for (int i = 0; i < b.getNextBlocks().size(); i++) {
					if (!b.getNextBlocks().get(i).containsStop()) {
						b = b.getNextBlocks().get(0);
						break;
					}
				}
			}
		} while (!done);
		return true;
	}

	/**
	 * Returns the first segment containing the sector s.
	 *
	 * @param s
	 * @return
	 */
	public Segment getSegmentFromSector(Sector s) {
		for (Segment seg : segments) {
			if (seg.getSectors().contains(s)) {
				return seg;
			}
		}
		return null;
	}

	/**
	 * Searches for a segment ending on the start of "from", with the current
	 * position of the switches.
	 *
	 * @param from
	 * @return
	 */
	public Segment getSegmentByTail(Segment from) {
		End start = from.getStart();
		//String head = start.sector.getId()+"("+start.getCurrentString()+")";
		int head = start.getCurrentCode();
		for (Segment seg : segments) {
			//if (seg.stop.id.equals(head)) {
			if (seg.stop.code == head) {
				return seg;
			}
		}
		Main.getRailwayLogger("Simulation").severe("Sim: The simulation could not find a segment ending on " + start.sector + ", with ");
		if (start.switches.isEmpty()) {
			Main.getRailwayLogger("Simulation").severe("Sim:   no switches on the sector.");
		} else for (Switch s : start.switches) {
			Main.getRailwayLogger("Simulation").severe("Sim:   switch " + s + " to " + s.getPosition());
		}
		Main.getRailwayLogger("Simulation").severe("Sim: A null pointer exception will probably now crash the simulation. Please restart.");
		return null;
	}

	/**
	 * Same as getSegmentByTail, but ... well, that looks at the other end
	 * of the segment.
	 *
	 * @param from
	 * @return
	 */
	public Segment getSegmentByHead(Segment from) {
		End stop = from.getStop();
		//String head = stop.sector.getId()+"("+stop.getCurrentString()+")";
		int tail = stop.getCurrentCode();
		for (Segment seg : segments) {
			//if (seg.start.id.equals(head)) {
			if (seg.start.code == tail) {
				return seg;
			}
		}

		Main.getRailwayLogger("Simulation").severe("Sim: The simulation could not find a segment starting on " + stop.sector + ", with ");
		if (stop.switches.isEmpty()) {
			Main.getRailwayLogger("Simulation").severe("Sim:   no switches on the sector.");
		} else for (Switch s : stop.switches) {
			Main.getRailwayLogger("Simulation").info("Sim:   switch " + s + " to " + s.getPosition());
		}
		Main.getRailwayLogger("Simulation").severe("Sim: A null pointer exception will probably now crash the simulation. Please restart.");
		return null;
	}

	/**
	 * Load the trains and creates SimTrain objects.
	 *
	 * @throws Exception
	 */
	private void loadTrains() throws Exception {
		Main.getRailwayLogger("Simulation").info("Sim: Loading the trains");
		for (Train train : railway.getTrains()) {
			// get the start position
			List<Block> startBlocks = railway.getBlocksByEndId(train.getSignalId());
			if (startBlocks.isEmpty()) {
				Exception e = new Exception("No block for train " + train + " ending with the correct signal id");
				throw e;
			}
			Sector startSector = startBlocks.get(0).getLastSector();
			Segment start = getSegmentFromSector(startSector);
			double offset = start.getSectorOffset(startSector);

			Main.getRailwayLogger("Simulation").info("Sim: SimTrain " + train + " positionned on " + start);
			SimTrain st = new SimTrain(train, start, offset);
			elements.add(st);
		}
		Main.getRailwayLogger("Simulation").info("Sim: Done.");
	}

	/**
	 * Loads the switches.
	 */
	private void addSimSwitches() {
		for (Switch s : Railway.instance().getSwitches()) {
			SimSwitch sS = new SimSwitch(s);
			elements.add(sS);
		}
	}

	/**
	 * Loads the signals.
	 */
	private void addSimSignals() {
		for (Signal s : Railway.instance().getSignals()) {
			SimSignal sS = new SimSignal(s);
			elements.add(sS);
		}
	}

	/**
	 * Update the simulator. Pretty small function for such a huge task !
	 */
	@Override
	public void run() {
		Main.getRailwayLogger("Simulation").info("Simulation started");
		while (true) {
			// wait some time...
			try {
				Thread.sleep(77); // about 13Hz
			} catch (InterruptedException ie) {
			}

			// ... update the elements
			for (Element e : elements) {
				e.update();
			}
		}
	}
}
