package railway;

import apps.Main;
import sx.SX;

import java.util.logging.Logger;

/**
 * This class represents a signal of the railway.
 *
 * @author tcstrain
 */
public class Signal extends RailwayElement {

	/**
	 * Constant describing the speed 40.
	 */
	private final static byte speed40 = 40;
	/**
	 * Constant describing the speed 60
	 */
	private final static byte speed60 = 60;
	/**
	 * Constant describing the speed 90
	 */
	private final static byte speed90 = 90;
	/**
	 * Id of the signal
	 */
	private final String Id;
	/**
	 * Address of the signal
	 */
	private final byte address;
	/**
	 * Bit position of a LED
	 */
	private byte g1bitpos;
	/**
	 * Bit position of a LED
	 */
	private byte g2bitpos;
	/**
	 * Bit position of a LED
	 */
	private byte r1bitpos;
	/**
	 * Bit position of a LED
	 */
	private byte r2bitpos;
	/**
	 * Bit position of a LED
	 */
	private byte o1bitpos;
	/**
	 * Bit position of a LED
	 */
	private byte o2bitpos;
	/**
	 * Bit position of a LED
	 */
	private byte numbitpos;
	/**
	 * Tells whether a light exists or not.
	 */
	private boolean existsg1 = false;
	/**
	 * Tells whether a light exists or not.
	 */
	private boolean existsg2 = false;
	/**
	 * Tells whether a light exists or not.
	 */
	private boolean existsr1 = false;
	/**
	 * Tells whether a light exists or not.
	 */
	private boolean existsr2 = false;
	/**
	 * Tells whether a light exists or not.
	 */
	private boolean existso1 = false;
	/**
	 * Tells whether a light exists or not.
	 */
	private boolean existso2 = false;
	/**
	 * Tells whether a light exists or not.
	 */
	private boolean existsnum = false;
	/**
	 * The speed indicated by the signal
	 */
	private byte speed = 0;

	private Logger logger;

	/**
	 * Constructor of the class. Takes an id and an address
	 * as parameters
	 *
	 * @param Id
	 * @param address
	 */
	public Signal(String Id, byte address) {
		this.Id = Id;
		this.address = address;
		this.logger = Main.getRailwayLogger("Signal." + Id);
	}

	/**
	 * @return the ID of the signal
	 */
	@Override
	public String getId() {
		return Id;
	}

	/**
	 * Give true to indicate that the signal has this light
	 *
	 * @param existsg1
	 */
	public void setExistsg1(boolean existsg1) {
		this.existsg1 = existsg1;
	}

	/**
	 * Give true to indicate that the signal has this light
	 *
	 * @param existsg2
	 */
	public void setExistsg2(boolean existsg2) {
		this.existsg2 = existsg2;
	}

	/**
	 * Give true to indicate that the signal has this light
	 *
	 * @param existsnum
	 */
	public void setExistsnum(boolean existsnum) {
		this.existsnum = existsnum;
	}

	/**
	 * Give true to indicate that the signal has this light
	 *
	 * @param existso1
	 */
	public void setExistso1(boolean existso1) {
		this.existso1 = existso1;
	}

	/**
	 * Give true to indicate that the signal has this light
	 *
	 * @param existso2
	 */
	public void setExistso2(boolean existso2) {
		this.existso2 = existso2;
	}

	/**
	 * Give true to indicate that the signal has this light
	 *
	 * @param existsr1
	 */
	public void setExistsr1(boolean existsr1) {
		this.existsr1 = existsr1;
	}

	/**
	 * Give true to indicate that the signal has this light
	 *
	 * @param existsr2
	 */
	public void setExistsr2(boolean existsr2) {
		this.existsr2 = existsr2;
	}

	/**
	 * Sets the bit of the led
	 *
	 * @param g1bitpos
	 */
	public void setG1bitpos(byte g1bitpos) {
		this.g1bitpos = g1bitpos;
	}

	/**
	 * Sets the bit of the led
	 *
	 * @param g2bitpos
	 */
	public void setG2bitpos(byte g2bitpos) {
		this.g2bitpos = g2bitpos;
	}

	/**
	 * Sets the bit of the led
	 *
	 * @param numbitpos
	 */
	public void setNumbitpos(byte numbitpos) {
		this.numbitpos = numbitpos;
	}

	/**
	 * Sets the bit of the led
	 *
	 * @param o1bitpos
	 */
	public void setO1bitpos(byte o1bitpos) {
		this.o1bitpos = o1bitpos;
	}

	/**
	 * Sets the bit of the led
	 *
	 * @param o2bitpos
	 */
	public void setO2bitpos(byte o2bitpos) {
		this.o2bitpos = o2bitpos;
	}

	/**
	 * Sets the bit of the led
	 *
	 * @param r1bitpos
	 */
	public void setR1bitpos(byte r1bitpos) {
		this.r1bitpos = r1bitpos;
	}

	/**
	 * Sets the bit of the led
	 *
	 * @param r2bitpos
	 */
	public void setR2bitpos(byte r2bitpos) {
		this.r2bitpos = r2bitpos;
	}

	/**
	 * Switch all leds off
	 */
	public void setNone() {
		if (existsg1) {
			SX.instance().setStatusBit(address, g1bitpos, false);
		}
		if (existsg2) {
			SX.instance().setStatusBit(address, g2bitpos, false);
		}
		if (existsr1) {
			SX.instance().setStatusBit(address, r1bitpos, false);
		}
		if (existsr2) {
			SX.instance().setStatusBit(address, r2bitpos, false);
		}
		if (existso1) {
			SX.instance().setStatusBit(address, o1bitpos, false);
		}
		if (existso2) {
			SX.instance().setStatusBit(address, o2bitpos, false);
		}
		if (existsnum) {
			SX.instance().setStatusBit(address, numbitpos, false);
		}
	}

	/**
	 * Switch all leds on
	 */
	public void setAll() {
		if (existsg1) {
			SX.instance().setStatusBit(address, g1bitpos, true);
		}
		if (existsg2) {
			SX.instance().setStatusBit(address, g2bitpos, true);
		}
		if (existsr1) {
			SX.instance().setStatusBit(address, r1bitpos, true);
		}
		if (existsr2) {
			SX.instance().setStatusBit(address, r2bitpos, true);
		}
		if (existso1) {
			SX.instance().setStatusBit(address, o1bitpos, true);
		}
		if (existso2) {
			SX.instance().setStatusBit(address, o2bitpos, true);
		}
		if (existsnum) {
			SX.instance().setStatusBit(address, numbitpos, true);
		}
	}

	/**
	 * Sets the signal to drive max, the maximal speed of the locomotive.
	 */
	private void setDriveMax() {
		speed = Locomotive.driveMax;
		logger.finer("Signal " + this + " set to Free");
		if (existsg1) {
			SX.instance().setStatusBit(address, g1bitpos, true);
		}
		if (existsg2) {
			SX.instance().setStatusBit(address, g2bitpos, false);
		}
		if (existsr1) {
			SX.instance().setStatusBit(address, r1bitpos, false);
		}
		if (existsr2) {
			SX.instance().setStatusBit(address, r2bitpos, false);
		}
		if (existso1) {
			SX.instance().setStatusBit(address, o1bitpos, false);
		}
		if (existso2) {
			SX.instance().setStatusBit(address, o2bitpos, false);
		}
		if (existsnum) {
			SX.instance().setStatusBit(address, numbitpos, false);
		}
	}

	/**
	 * Sets the signal to drive 40, a low speed of the locomotive
	 */
	private void setDrive40() {
		speed = Locomotive.drive40;
		logger.finer("Signal " + this + " set to Free40");
		if (existsg1) {
			SX.instance().setStatusBit(address, g1bitpos, true);
		}
		if (existsg2) {
			SX.instance().setStatusBit(address, g2bitpos, false);
		}
		if (existsr1) {
			SX.instance().setStatusBit(address, r1bitpos, false);
		}
		if (existsr2) {
			SX.instance().setStatusBit(address, r2bitpos, false);
		}
		if (existso1) {
			SX.instance().setStatusBit(address, o1bitpos, true);
		}
		if (existso2) {
			SX.instance().setStatusBit(address, o2bitpos, false);
		}
		if (existsnum) {
			SX.instance().setStatusBit(address, numbitpos, false);
		}
	}

	/**
	 * Sets the signal to drive 60, an average speed of the locomotive
	 */
	private void setDrive60() {
		speed = Locomotive.drive60;
		logger.finer("Signal " + this + " set to Free60");
		if (existsg1) {
			SX.instance().setStatusBit(address, g1bitpos, true);
		}
		if (existsg2) {
			SX.instance().setStatusBit(address, g2bitpos, true);
		}
		if (existsr1) {
			SX.instance().setStatusBit(address, r1bitpos, false);
		}
		if (existsr2) {
			SX.instance().setStatusBit(address, r2bitpos, false);
		}
		if (existso1) {
			SX.instance().setStatusBit(address, o1bitpos, false);
		}
		if (existso2) {
			SX.instance().setStatusBit(address, o2bitpos, false);
		}
		if (existsnum) {
			SX.instance().setStatusBit(address, numbitpos, true);
		}
	}

	/**
	 * Sets the signal to drive short, the slowest speed of the locomotive
	 */
	public void setShort() {
		speed = Locomotive.driveShort;
		logger.finer("Signal " + this + " set to Short");
		if (existsg1) {
			SX.instance().setStatusBit(address, g1bitpos, false);
		}
		if (existsg2) {
			SX.instance().setStatusBit(address, g2bitpos, false);
		}
		if (existsr1) {
			SX.instance().setStatusBit(address, r1bitpos, false);
		}
		if (existsr2) {
			SX.instance().setStatusBit(address, r2bitpos, false);
		}
		if (existso1) {
			SX.instance().setStatusBit(address, o1bitpos, true);
		}
		if (existso2) {
			SX.instance().setStatusBit(address, o2bitpos, true);
		}
		if (existsnum) {
			SX.instance().setStatusBit(address, numbitpos, false);
		}
	}

	/**
	 * Sets the signal to stop - red
	 */
	public void setStop() {
		speed = Locomotive.stop;
		logger.finer("Signal " + this + " set to Stop");
		if (existsg1) {
			SX.instance().setStatusBit(address, g1bitpos, false);
		}
		if (existsg2) {
			SX.instance().setStatusBit(address, g2bitpos, false);
		}
		if (existsr1) {
			SX.instance().setStatusBit(address, r1bitpos, true);
		}
		if (existsr2) {
			SX.instance().setStatusBit(address, r2bitpos, true);
		}
		if (existso1) {
			SX.instance().setStatusBit(address, o1bitpos, false);
		}
		if (existso2) {
			SX.instance().setStatusBit(address, o2bitpos, false);
		}
		if (existsnum) {
			SX.instance().setStatusBit(address, numbitpos, false);
		}
	}

	/**
	 * Returns the speed given by the signal
	 *
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}


	/**
	 * Sets the signal. The parameter must be either Signal.speed40,
	 * Signal.speed60 or Signal.speed90.
	 *
	 * @param speed the speed to set
	 */
	public void setSpeed(byte speed) {
		switch (speed) {
			case speed40:
				setDrive40();
				break;
			case speed60:
				setDrive60();
				break;
			case speed90:
				setDriveMax();
				break;
			default:
				System.err.println("Unknown speed: " + speed);
				setDrive40();
				break;
		}
	}
}
