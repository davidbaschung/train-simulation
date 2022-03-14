package railway;

import sx.SX;

/**
 * Represents a switch of the miniature railway system.
 */
public class Switch extends RailwayElement {

	/**
	 * byte address of the switch
	 */
	byte address;

	/**
	 * bit position address of the switch
	 */
	byte bitpos;

	/**
	 * sector of the switch
	 */
	private Sector sector;

	/**
	 * create a Switch, based on
	 *
	 * @param id      its id.
	 * @param address its decoder address.
	 * @param bitpos  its bit position on the decoder.
	 */
	public Switch(String id, byte address, byte bitpos, Sector sector) {
		this.id = id;
		this.address = address;
		this.bitpos = bitpos;
		this.sector = sector;
	}

	/**
	 * @return the saved position of the switch. May be false if
	 * the switch has been moved manually.
	 */
	public boolean getPosition() {
		return SX.instance().getStatusBit(address, bitpos);
	}

	/**
	 * sets the position of the switch
	 *
	 * @param statusBit the new status bit value
	 */
	public void setPosition(boolean statusBit) {
		SX.instance().setStatusBit(address, bitpos, statusBit);
	}

	/**
	 * @return the sector on which the switch is.
	 */
	public Sector getSector() {
		return sector;
	}
}