package sim;

import apps.Main;
import railway.Sector;
import sx.SX;

/**
 * This class represents an element that can move. When this doc was written,
 * the only child class was the SimTrain - simulated train. It occupies a
 * sector and can be moved on the railway.
 * If some day somebody wants to implement the possibility to place wild
 * wagons on the tracks, they could use this class.
 *
 * @author Mathias Seuret
 */
public class Movable implements Element {
	/**
	 * Segment on which the Movable is.
	 */
	Segment position;

	/**
	 * Offset of the Movable from the start of the segment.
	 */
	double offset;

	/**
	 * Current speed in centimeters per second of the Movable.
	 */
	double speed;

	/**
	 * Current direction of the Movable.
	 * 0 : clockwise
	 * 1 : otherwise
	 */
	int direction;

	/**
	 * Last time (in milliseconds) it was updated.
	 */
	long lastUpdate;


	/**
	 * Builds a Movable and places it on the railway.
	 *
	 * @param position
	 * @param offset
	 */
	public Movable(Segment position, double offset) {
		this.position = position;
		this.offset = offset;
		lastUpdate = System.currentTimeMillis();

		Sector sector = position.getSectorAt(offset);
		Main.getRailwayLogger("Simulation").info("Sim: Movable placed on " + sector);
		SX.instance().setStatusBit(sector.address, sector.bitpos, true);
	}

	/**
	 * Updates the Movable. That means it will be moved and sectors will
	 * be occupied and freed accordingly to its new position.
	 */
	@Override
	public void update() {
		long time = System.currentTimeMillis();
		double dt = 0.001 * (time - lastUpdate);
		lastUpdate = time;

		// backup the position of the train
		Sector oldSector = position.getSectorAt(offset);

		// offset the train
		offset += dt * speed * direction;

		// will be modified in case the movable left the
		// current segment
		Segment newPosition = position;

		if (offset < 0) {
			// if there is a dead-end, the Movable is blocked
			if (position.start.isDeadEnd()) {
				offset = 0;
				Main.getRailwayLogger("Simulation").info("Sim: " + getId() + " reached an end at " + position.start.sector);
			} else {
				// we find the new position
				newPosition = Simulator.get().getSegmentByTail(position);

				// and now we can update the offset in the new position
				offset = newPosition.length + offset; // negative offset
			}
		} else if (offset > position.getLength()) {
			// in case of dead-end, the Movable is blocked
			if (position.stop.isDeadEnd()) {
				offset = position.getLength();
				Main.getRailwayLogger("Simulation").info("Sim: " + getId() + " reached an end at " + position.stop.sector);
			} else {
				newPosition = Simulator.get().getSegmentByHead(position);
				offset = offset - position.getLength();
			}
		}

		if (position != newPosition) {
			// log
			// Simulator.Main.getRailwayLogger("Simulation").info(getId()+" left "+position+" for "+newPosition);
			// not needed anymore

			// update
			position = newPosition;
		}

		// get new sector
		if (position.getSectorAt(offset) == null) {
			System.err.println("Sector is null");
		}
		Sector newSector = position.getSectorAt(offset);


		// is there any change ?
		if (newSector != oldSector) {
			Main.getRailwayLogger("Simulation").info("Sim: " + getId() + " has left " + oldSector + " for " + newSector);
			SX.instance().setStatusBit(newSector.address, newSector.bitpos, true);
			SX.instance().setStatusBit(oldSector.address, oldSector.bitpos, false);
		}
	}

	public String getId() {
		return "MovableInstance:" + this;
	}
}