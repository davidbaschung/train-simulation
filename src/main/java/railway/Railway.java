package railway;

import events.Observer;

import java.util.ArrayList;

/**
 * Represents the miniature railway system.
 */
public class Railway extends RailwayElement {

	//Singleton pattern
	private static Railway instance = null;
	/**
	 * List of the sectors of the railway
	 */
	private final ArrayList<Sector> sectors = new ArrayList<>();
	/**
	 * List of the switches of the railway
	 */
	private final ArrayList<Switch> switches = new ArrayList<>();
	/**
	 * List of the blocks of the railway
	 */
	private final ArrayList<Block> blocks = new ArrayList<>();
	/**
	 * List of the stations of the railway
	 */
	private final ArrayList<Station> stations = new ArrayList<>();
	/**
	 * List of the lines of the railway
	 */
	private final ArrayList<Line> lines = new ArrayList<>();
	/**
	 * List of the locomotives of the railway
	 */
	private final ArrayList<Locomotive> locomotives = new ArrayList<>();
	/**
	 * List of the trains of the railway
	 */
	private final ArrayList<Train> trains = new ArrayList<>();
	/**
	 * List of the signals of the railway
	 */
	private final ArrayList<Signal> signals = new ArrayList<>();


	private final ArrayList<Observer> observers = new ArrayList<>();


	private boolean isEmergencyStopped = false;

	private Railway() {
	}

	public static Railway instance() {
		if (instance == null)
			instance = new Railway();
		return instance;
	}

	/**
	 * Sets all signals to stop
	 */
	public void initSignals() {
		for (Signal s : signals) {
			s.setStop();
		}
	}


	/**
	 * Returns a sector, the parameter is its ID
	 *
	 * @param ref the id
	 * @return the sector identified by given ref.
	 */
	public Sector getSectorById(String ref) {
		for (Sector each : sectors) {
			if (each.getId().equalsIgnoreCase(ref)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * @return the list of sectors
	 */
	public ArrayList<Sector> getSectors() {
		return sectors;
	}

	/**
	 * Adds a sector to the railway
	 *
	 * @param s
	 */
	public void addSector(Sector s) {
		sectors.add(s);
	}

	/**
	 * @param ref
	 * @return the switch identified by a given id.
	 */
	public Switch getSwitchById(String ref) {
		for (Switch each : switches) {
			if (each.getId().equalsIgnoreCase(ref)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * Adds a switch to the railway
	 *
	 * @param s
	 */
	public void addSwitch(Switch s) {
		switches.add(s);
	}

	/**
	 * @return the list of switches
	 */
	public ArrayList<Switch> getSwitches() {
		return switches;
	}

	/**
	 * @param ref
	 * @return the block identified by a given id.
	 */
	public Block getBlockById(String ref) {
		for (Block each : blocks) {
			if (each.getId().equalsIgnoreCase(ref)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * @return the list of blocks
	 */
	public ArrayList<Block> getBlocks() {
		return blocks;
	}

	/**
	 * Adds a block to the railway
	 *
	 * @param b
	 */
	public void addBlock(Block b) {
		blocks.add(b);
	}

	/**
	 * @param ref
	 * @return an ArrayList of blocks identified by a given start id.
	 */
	public ArrayList<Block> getBlocksByStartId(String ref) {
		ArrayList<Block> result = new ArrayList<>();
		for (Block block : blocks) {
			if (ref.equalsIgnoreCase(block.getStartId())) {
				result.add(block);
			}
		}
		return result;
	}

	/**
	 * @param ref
	 * @return a list of blocks which end signal id matches the
	 * parameter
	 */
	public ArrayList<Block> getBlocksByEndId(String ref) {
		ArrayList<Block> result = new ArrayList<>();
		for (Block block : blocks) {
			if (ref.equalsIgnoreCase(block.getEndId())) {
				result.add(block);
			}
		}
		return result;
	}

	/**
	 * @param ref
	 * @return a list of YBlocks (casted into blocks) which stop signal
	 * id matches the parameter
	 */
	public ArrayList<Block> getBlocksByStopId(String ref) {
		ArrayList<Block> result = new ArrayList<>();
		for (Block block : blocks) {
			if (!block.containsStop())
				continue;
			if (ref.equalsIgnoreCase(((YBlock) block).getStopId())) {
				result.add(block);
			}
		}
		return result;
	}

	/**
	 * @param ref
	 * @return the line identified by a given id.
	 */
	public Line getLineById(String ref) {
		for (Line each : lines) {
			if (each.getId().equalsIgnoreCase(ref)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * @return the list of the lines
	 */
	public ArrayList<Line> getLines() {
		return lines;
	}

	/**
	 * @param ref
	 * @return the station identified by a given id.
	 */
	public Station getStationById(String ref) {
		for (Station each : stations) {
			if (each.getId().equalsIgnoreCase(ref)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * Adds a station to the railway
	 *
	 * @param s
	 */
	public void addStation(Station s) {
		stations.add(s);
	}

	/**
	 * @param ref
	 * @return the train having the id ref
	 */
	public Train getTrainById(String ref) {
		for (Train each : trains) {
			if (each.getId().equalsIgnoreCase(ref)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * Delete a Train from the railway
	 *
	 * @param t
	 */
	public void deleteTrain(Train t) {
		trains.remove(t);
		t.isDeleted = true;
	}

	/**
	 * Adds a Train to the railway
	 *
	 * @param t
	 */
	public void addTrain(Train t) {
		trains.add(t);
	}

	/**
	 * @return the list of trains
	 */
	public ArrayList<Train> getTrains() {
		return trains;
	}

	/**
	 * @param ref
	 * @return the locomotive which has the given id
	 */
	public Locomotive getLocomotiveById(String ref) {
		for (Locomotive each : locomotives) {
			if (each.getId().equalsIgnoreCase(ref)) {
				return each;
			}
		}
		return null;
	}

	/**
	 * @return the list of locomotives
	 */
	public ArrayList<Locomotive> getLocomotives() {
		return locomotives;
	}

	/**
	 * Adds a locomotive to the railway
	 *
	 * @param l
	 */
	public void addLocomotive(Locomotive l) {
		locomotives.add(l);
	}

	/**
	 * Delete a Locomotive from the railway
	 *
	 * @param l
	 */
	public void deleteLocomotive(Locomotive l) {
		locomotives.remove(l);
	}

	/**
	 * @return the list of stations
	 */
	public ArrayList<Station> getStations() {
		return stations;
	}

	/**
	 * Adds a signal to the railway
	 *
	 * @param s
	 */
	public void addSignal(Signal s) {
		signals.add(s);
	}

	/**
	 * @param Id
	 * @return a signal given by an id
	 */
	public Signal getSignalById(String Id) {
		for (Signal s : signals) {
			if (s.getId().equals(Id)) {
				return s;
			}
		}
		System.err.println("Cannot find signal " + Id);
		return null;
	}

	/**
	 * @return the list of signals
	 */
	public ArrayList<Signal> getSignals() {
		return signals;
	}

	public boolean isEmergencyStopped() {
		return isEmergencyStopped;
	}

	public void emergencyStop() {
		for (Train t : trains){
			t.getLocomotive().emergencyStop();
		}
		isEmergencyStopped = true;
	}

	public void emergencyContinue() {
		for (Train t : trains){
			t.getLocomotive().emergencyContinue();
		}
		isEmergencyStopped = false;
	}

	public ArrayList<Observer> getObservers() {
		return observers;
	}

	public Observer getObserverById(String id) {
		for (Observer o : observers) {
			if (o.getId().equals(id)) {
				return o;
			}
		}
		return null;
	}
}
