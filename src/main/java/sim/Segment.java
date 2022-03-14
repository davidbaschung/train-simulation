package sim;

import railway.Block;
import railway.Sector;
import railway.Switch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A segment is a part of the railway, between two sectors containing switches.
 * Each segment also stores the position of the switches. If many segments can
 * exist between two sectors, there is only one for each configuration of
 * the switches.
 * The segments are used for moving Movable objects.
 *
 * @author Mathias Seuret
 */
public class Segment {

	/**
	 * Id of the segment.
	 */
	public String id;
	/**
	 * Entry of the segment.
	 */
	public End start = new End();
	/**
	 * Exit of the segment.
	 */
	public End stop = new End();
	/**
	 * Direction of the segment.
	 */
	public boolean direction;
	/**
	 * Length of the segment.
	 */
	public double length = 0.0;
	/**
	 * List of the sectors, clockwise order.
	 */
	private List<Sector> sectors = new ArrayList<Sector>();

	/**
	 * Sets the start End of the segment as sector s on block b.
	 *
	 * @param b
	 * @param s
	 */
	public void setStart(Block b, Sector s) {
		start = new End(b, s);
		direction = b.getDirection();
		id = "Segment(" + start.code + "->" + stop.code + ")<" + direction + ">";
		addSector(s);
	}

	/**
	 * Returns the start end of the segment.
	 *
	 * @return
	 */
	public End getStart() {
		return start;
	}

	/**
	 * Returns the exit end of the segment.
	 *
	 * @return
	 */
	public End getStop() {
		return stop;
	}

	/**
	 * Makes an exit end on sector s, block b.
	 *
	 * @param b
	 * @param s
	 */
	public void setStop(Block b, Sector s) {
		stop = new End(b, s);
		id = "Segment(" + start.code + "->" + stop.code + ")<" + direction + ">";
		addSector(s);
	}

	/**
	 * Adds a sector to the segment.
	 *
	 * @param s
	 */
	public void addSector(Sector s) {
		sectors.add(s);
		length += s.getLength();
	}

	/**
	 * Reverses all elements of the segment.
	 */
	public void reverseSegment() {
		End e = start;
		start = stop;
		stop = e;
		id = "Segment(" + start.code + "->" + stop.code + ")<" + direction + ">";
		Stack<Sector> stack = new Stack<Sector>();
		for (Sector s : sectors) {
			stack.push(s);
		}
		sectors.clear();
		while (!stack.isEmpty()) {
			sectors.add(stack.pop());
		}
	}

	/**
	 * Returns the sector which is at the given offset from the start. If
	 * it's out of the segment, null is returned.
	 *
	 * @param offset
	 * @return
	 */
	public Sector getSectorAt(double offset) {
		if (offset < 0) {
			return null;
		}
		for (Sector s : sectors) {
			if (offset <= s.getLength()) {
				return s;
			} else {
				offset -= s.getLength();
			}
		}
		return null;
	}

	/**
	 * Returns the list of all sectors of the segment.
	 *
	 * @return
	 */
	public List<Sector> getSectors() {
		return sectors;
	}

	/**
	 * Returns the offset of the middle of the given sector.
	 *
	 * @param sec
	 * @return
	 */
	public double getSectorOffset(Sector sec) {
		double offset = 0.0;
		for (Sector s : sectors) {
			if (s == sec) {
				offset += s.getLength() / 2;
				break;
			} else {
				offset += s.getLength();
			}
		}
		return offset;
	}

	/**
	 * Returns the length of the segment.
	 *
	 * @return
	 */
	public double getLength() {
		return length;
	}

	/**
	 * Printable name of the segment.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return id;
	}

	/**
	 * This class stores an end of a segment, with all needed data.
	 */
	public class End {
		/**
		 * The sector
		 */
		Sector sector;

		/**
		 * 4 bytes integer, stored as
		 * [byte0: address of the sector]
		 * [byte1: bitpos of the sector]
		 * [byte2-3: one bit per switch, storing its position]
		 */
		int code;

		/**
		 * Sorted list of all switches contained by the sector (1-2).
		 */
		List<Switch> switches = new LinkedList<Switch>();

		/**
		 * If true, then the End is a dead-end. This means that no Movable
		 * object should be allowed to go further than it.
		 */
		boolean deadEnd = false;

		/**
		 * Default constructor, needed in order to avoid unneeded complexity
		 * to deal with the creation of ends.
		 */
		public End() {
		}

		/**
		 * Constructs an end on the sector s, with switches set for crossing
		 * block b.
		 *
		 * @param b
		 * @param s
		 */
		public End(Block b, Sector s) {
			sector = s;
			id = s.getId() + "(" + Simulator.get().getSwitchString(b, s) + ")";
			switches = Simulator.get().getSortedSwitches(s);
			code = Simulator.get().getSwitchCode(b, s);
		}

		/**
		 * Returns the current code corresponding to the sector of the end,
		 * that is the address of the sector, its bitpos, and the current
		 * position of its switches.
		 *
		 * @return
		 */
		public int getCurrentCode() {
			int result = sector.address | (sector.bitpos << 8);
			int offset = 16;
			for (Switch s : switches) {
				result |= ((s.getPosition() ? 1 : 0) << offset++);
			}
			return result;
		}

		/**
		 * @return true if the End is a dead-end
		 */
		public boolean isDeadEnd() {
			return deadEnd;
		}

		/**
		 * Indicates if the End is a dead-end or not.
		 *
		 * @param newValue
		 */
		public void setDeadEnd(boolean newValue) {
			deadEnd = newValue;
		}
	}
}

