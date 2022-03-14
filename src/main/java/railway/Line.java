package railway;

import apps.Main;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Represents a line of the miniature railway system. It is mainly a list
 * of stations to visit and the number of the last visited station.
 */
public class Line extends RailwayElement {

	/**
	 * the sequence of stations to visit
	 */
	private final ArrayList<Station> stations;

	/**
	 * index of the last visited station
	 */
	int visitIndex = 0;
	private Logger logger;

	/**
	 * Constructor of the class. The parameters are the id of the line and
	 * the list of stations to visit.
	 *
	 * @param id
	 * @param stations
	 */
	public Line(String id, ArrayList<Station> stations) {
		this.id = id;
		this.stations = stations;
		this.logger = Main.getRailwayLogger("Line." + this.id);
		logger.finest(this.id + " initialized.");

	}

	/**
	 * @return the last station the train visited, using the list of stations
	 * and the index
	 */
	public Station getCurrentStation() {
		Station currentstation = stations.get(visitIndex);
		return currentstation;
	}


	public boolean isAtLastStation() {
		if (visitIndex == stations.size()-1){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * @return the next station to visit, using the list of stations and the
	 * index. If we have reached the end of the list, go back to the first
	 * element.
	 */
	public Station getNextStation() {
		if (stations.size()-1==visitIndex){
			return stations.get(0);
		} else {
			return stations.get(visitIndex+1);
		}

	}

	/**
	 * tells the line that next station has been reached. (increment the
	 * visitIndex). If we have reached the end of the list, go back to the first
	 * element.
	 */
	public void reachedNextStation() {

		if(stations.size()-1 == visitIndex ){
			visitIndex = 0;
		} else {
			visitIndex = visitIndex +1;
		}
	}

	public ArrayList<Station> getStations() {
		return stations;
	}
}