package railway;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a station of the miniature railway system. Stations are mainly
 * defined by a name and a list of signals at which the trains can stop.
 */
public class Station extends RailwayElement {
	/**
	 * List of the signal IDs which are at the ends of the platforms.
	 */
	private final List<String> signals = new LinkedList<>();

	/**
	 * Creates a Station
	 *
	 * @param id its unique id
	 */
	public Station(String id) {
		this.id = id;
	}

	/**
	 * Adds the ID of a signal which is at the end of a platform.
	 *
	 * @param sig
	 */
	public void addSignal(String sig) {
		signals.add(sig);
	}

	/**
	 * @return the list of the signal IDs which define the station.
	 */
	public List<String> getSignals() {
		return signals;
	}
}
