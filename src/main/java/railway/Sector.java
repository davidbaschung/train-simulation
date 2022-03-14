package railway;

import apps.Main;
import sx.SX;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents a sector of the miniature railway system.
 */
public class Sector extends RailwayElement {
    /**
     * byte address of the sector
     */
    public byte address;

    /**
     * bit position address of the sector
     */
    public byte bitpos;

    /**
     * Estimation of the length of the sector
     */
    private int length;

    /**
     * True if there is a switch on the sector
     */
    private boolean hasSwitch;

    /**
     * Set containing the IDs of the objects which have currently put a lock
     * on the sector.
     */
    private Set<String> locks = new HashSet<>();

    private Logger logger;

    /**
     * create a new Sector.
     *
     * @param id      its id.
     * @param address its decoder address.
     * @param bitpos  its bit position on the decoder.
     */
    public Sector(String id, byte address, byte bitpos, int length) {
        this.id = id;
        this.address = address;
        this.bitpos = bitpos;
        this.length = length;
    }

    /**
     * @return true if the sector is locked, false otherwise
     */
    public boolean isLocked() {
        if (locks.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param e
     * @return true if the specified RailwayElement has put a lock
     * on this sector, false otherwise.
     */
    public boolean isLockedBy(RailwayElement e) {
        if (!locks.isEmpty()) {
            for (String railwayElement : locks) {
                if (e.id.equals(railwayElement)) {
                    return true;
                } else {

                    return false;
                }
            }
        }
        return false;
    }

    /**
     * @return whether the sector is physically occupied or not
     */
    public boolean isOccupied() {
        boolean occupation = SX.instance().getStatusBit(this.address, this.bitpos);
        if (occupation == true) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns an estimation of the length of the sector
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the length of the sector
     *
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Adds a lock to the element.
     *
     * @param id
     */
    public void lock(RailwayElement id) {
        locks.add(id.id);
    }

    /**
     * Returns true if the sector contains a switch, false otherwise
     *
     * @return hasSwitch
     */
    public boolean hasSwitch() {
        return hasSwitch;
    }

    /**
     * Sets the hasSwitch value, which indicates if the sector contains
     * a switch or not.
     *
     * @param hasSwitch
     */
    public void setHasSwitch(boolean hasSwitch) {
        this.hasSwitch = hasSwitch;
    }

    /**
     * Removes a lock from the object.
     *
     * @param id
     */
    public void unlock(RailwayElement id) {
        locks.remove(id.id);
    }
}
