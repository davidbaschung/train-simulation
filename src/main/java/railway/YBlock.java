package railway;

import apps.Main;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Extends blocks from a shape like | to a shape like Y, with a switch of
 * direction in the middle.
 *
 * @author ms
 */
public class YBlock extends Block {

	/**
	 * Hashset containing the IDs of the YBlocks, to make sure that there are
	 * no two with the same ID
	 */
	private static final Set<String> idSet = new HashSet<>();
	/**
	 * List of the switchpositions which are after the stop
	 */
	private final List<SwitchPosition> nextSwitches = new LinkedList<>();
	/**
	 * List of the sectors after the stop
	 */
	private final List<Sector> endSectors = new LinkedList<>();
	/**
	 * List of the sectors which are after the stop and not in the incoming
	 * block.
	 */
	private final List<Sector> tailSectors = new LinkedList<>();
	/**
	 * First sector of the YBlock, belongs to first Block
	 */
	private Sector firstSector = null;
	/**
	 * First signal of the YBlock
	 */
	private Signal startSignal = null;
	/**
	 * Last sector of the first block, that is the position at which
	 * the train will stop
	 */
	private Sector stopSector = null;
	/**
	 * Stop signal of the YBlock
	 */
	private Signal stopSignal = null;
	/**
	 * ID of the stop signal
	 */
	private String stopId = null;
	/**
	 * Last sector of the last block
	 */
	private Sector endSector = null;
	/**
	 * Last signal of the YBlock
	 */
	private Signal endSignal = null;
	/**
	 * Switchless length between the last switch and the stop signal
	 */
	private float switchlessLength;

	private Logger logger;

	/**
	 * Constructs a YBlock with the two blocks passed as parameter. The
	 * static method IsYBlockPossible() should be called first to make
	 * sure that the YBlock is possible.
	 *
	 * @param from
	 * @param to
	 */
	public YBlock(Block from, Block to) {
		/*
		 * Set the most important sectors
		 */
		firstSector = from.getFirstSector();
		stopSector = from.getLastSector();
		endSector = to.getLastSector();

		/*
		 * Add all the sectors of the first block to the list
		 */
		float length = 0.0f;
		for (Sector s : from.getSectors()) {
			getSectors().add(s);
			length += s.getLength();
		}

		/*
		 * Add all the sectors of the last block which are not in the first
		 * block, but which are after the stop sector, must be added
		 */
		boolean sectorAfterStop = false;
		boolean switchAfterStop = false;
		for (Sector s : to.getSectors()) {
			if (sectorAfterStop) {
				if (!from.getSectors().contains(s)) {
					getSectors().add(s);
					tailSectors.add(s);
				} else {
					endSectors.add(s);
				}
			}
			if (s == stopSector) {
				sectorAfterStop = true;
			}
			if (sectorAfterStop && s.hasSwitch()) {
				switchAfterStop = true;
			}
			if (sectorAfterStop && !switchAfterStop) {
				switchlessLength += s.getLength();
			}
			if (sectorAfterStop) {
				length += s.getLength();
			}
		}

		/*
		 * Set the length of the YBlock. It is multiplied by two
		 * because the train has to stop on it, and therefore requires
		 * more time to cross it.
		 */
		setLength(2 * length);

		/*
		 * Id of the YBlock
		 */
		id = generateId(from, to);
		idSet.add(id);

		logger = Main.getRailwayLogger("Yblock." + this.id);


		/*
		 * Id's of the signals
		 */
		startId = from.getStartId();
		stopId = from.getEndId();
		endId = to.getEndId();

		/*
		 * Let us define that the direction is the exit direction of the
		 * YBlock. There is no need to store the incoming direction because
		 *  - it can be easily computed : getDirection()^1
		 *  - the train already has this direction when it arrives
		 */
		setDirection(to.getDirection());

		/*
		 * The speed must be set to the speed of the outgoing block, so
		 * that when a train leaves the YBlock after a stop, it'll go
		 * at the correct speed
		 */
		setMaxSpeed(to.getMaxSpeed());

		/*
		 * Add all switchPositions of "from"
		 */
		getSwitchPositions().addAll(from.getSwitchPositions());

		/*
		 * Add all switchPositions of "to"
		 */
		for (SwitchPosition sp : to.getSwitchPositions()) {
			if (getSectors().contains(sp.getSector())) {
				nextSwitches.add(sp);
			}
		}

		logger.finest(id + " initialized.");
	}

	/**
	 * @param from
	 * @param to
	 * @return true if a YBlock can be made out of the two given blocks
	 */
	static public boolean isYBlockPossible(Block from, Block to) {
		/*
		 * A YBlock must not be made from two other YBlocks. It should not be
		 * necessary and if that was allowed, that could lead to a never ending
		 * loop.
		 */
		if (from instanceof YBlock || to instanceof YBlock) {
			return false;
		}


		/*
		 * The two blocks must have different directions
		 */
		if (from.getDirection() == to.getDirection()) {
			return false;
		}

		/*
		 * The last sector of the first block must be in the last block.
		 */
		if (!to.getSectors().contains(from.getLastSector())) {
			return false;
		}

		/*
		 * The last sector of the first block must not contain a switch
		 */
		if (from.getLastSector().hasSwitch()) {
			return false;
		}

		/*
		 * There must not already be a YBlock with the same if
		 */
		String id = generateId(from, to);
		return !idSet.contains(id);
	}

	/**
	 * Generates the ID of a YBlock
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	private static String generateId(Block from, Block to) {
		return from.getStartId()
				+ "-" + from.getEndId()
				+ "-" + to.getEndId();
	}

	/**
	 * Set the switches which are after the stop signal in the correct
	 * position. It must not be called before a train has stopped at
	 * the correct position. It might not be an error though if the
	 * railway has just been started and the train was not put exactly
	 * in the correct place. If that is the case, that is not a problem.
	 */
	public void setNextSwitches() {
		if (!isStopSectorOccupied()) {
			Main.getRailwayLogger("YBlock." + id).info("Warning, setting next switches but no train"
					+ " is at the stop sector. This is normal if the"
					+ "first block is a Yblock.");
		}
		for (SwitchPosition sp : nextSwitches) {
			sp.setPosition();
		}
	}

	/**
	 * @return the ID of the signal which is a stop for the trains.
	 */
	public String getStopId() {
		return stopId;
	}

	/**
	 * @param stopId the stopID to set
	 */
	public void setStopId(RailwayElement stopId) {
		this.stopId = stopId.getId();
	}

	/**
	 * @return true when a train enters in the YBlock.
	 */
	@Override
	public boolean isFirstSectorOccupied() {
		return firstSector.isOccupied();
	}

	/**
	 * @return true when a train enters in the YBlock.
	 */
	@Override
	public Sector getLastSector() {
		return endSector;
	}

	/**
	 * @return true when the last sector (which is after the stop)
	 * is occupied.
	 */
	@Override
	public boolean isLastSectorOccupied() {
		return endSector.isOccupied();
	}

	/**
	 * @return true when the sector in front of the stop signal
	 * is occupied.
	 */
	public boolean isStopSectorOccupied() {
		return getStopSector().isOccupied();
	}

	/**
	 * @return true if the (Y)Block contains a stop. If
	 * it is a YBlock, it does contain a stop, therefor the return value
	 * must be true. By default, a normal block returns false.
	 * The main purpose of this method is to remove the necessary of
	 * the instanceof operator.
	 */
	@Override
	public boolean containsStop() {
		return true;
	}

	/**
	 * the YBlock.
	 *
	 * @return a displayable name for the YBlock, and also tells the length of
	 */
	@Override
	public String toString() {
		return "YBlock[" + getId() + "]<" + (int) switchlessLength + ">";
	}

	/**
	 * Returns the length without switches before the stop
	 *
	 * @return the switchlessLength
	 */
	public float getSwitchlessLength() {
		return switchlessLength;
	}

	/**
	 * @return the stopSector
	 */
	private Sector getStopSector() {
		return stopSector;
	}

	/**
	 * Tells whether the tail (ie. the second branch of the Y ) is securable
	 */
	public boolean isTailSecurable() {
		for (Sector s : tailSectors) {
			if (s.isLocked() || s.isOccupied())
				return false;
		}
		return true;
	}

	/**
	 * Locks the last sectors of the YBlock. That is used when a train
	 * starts a journey from the stop sector of a YBlock : only half
	 * of it needs to be locked.
	 */
	public void lockEndSectors() {
		for (Sector s : endSectors) {
			s.lock(this);
		}
	}

	/**
	 * @return the lastSignal
	 */
	public Signal getEndSignal() {
		return endSignal;
	}

	/**
	 * @param endSignal the lastSignal to set
	 */
	public void setEndSignal(Signal endSignal) {
		this.endSignal = endSignal;
	}

	/**
	 * @return the stopSignal
	 */
	public Signal getStopSignal() {
		return stopSignal;
	}

	/**
	 * @param stopSignal the stopSignal to set
	 */
	public void setStopSignal(Signal stopSignal) {
		this.stopSignal = stopSignal;
	}

	/**
	 * @return the start signal of the YBlock
	 */
	@Override
	public Signal getStartSignal() {
		return startSignal;
	}

	/**
	 * @param startSignal the firstSignal to set
	 */
	public void setFirstSignal(Signal startSignal) {
		this.startSignal = startSignal;
	}

	/**
	 * @return the ID of the first signal
	 */
	@Override
	public String getStartId() {
		return startId;
	}
}
