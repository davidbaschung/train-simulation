package railway;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Represents a block of the miniature railway system.
 */
public class Block extends RailwayElement {

    /**
     * the ID of the starting signal
     */
    String startId = "";
    /**
     * the ID of the starting signal at the end of the block
     */
    String endId = "";
    /**
     * Array containing the sectors of the block, in the correct
     * order.
     */
    private ArrayList<Sector> sectors = new ArrayList();
    /**
     * the switches in the block and their required positions
     */
    //indique quelle branche le train prend
    private ArrayList<SwitchPosition> switchPositions = new ArrayList();
    /**
     * the Blocks following this block's end signal in SAME direction
     */
    private ArrayList<Block> nextBlocks = new ArrayList();
    /**
     * the maximum speed for a train driving through the block. By default
     * 31 but is changed in the constructor.
     */
    private byte maxSpeed;
    /**
     * Reference to the signal which is at the beginning of the block.
     */
    private Signal startSignal = null;
    /**
     * Reference to the signal which is at the end of the block.
     */
    private Signal endSignal = null;
    /**
     * Approximation of the length of the block, in centimeters.
     */
    private float length;

    /**
     * Direction of the block, 0 clockwise, 1 otherwise
     */
    private boolean direction;

    private Logger logger;

    Block() {
        // to allow inheritance from YBlock, without this constructor
        // ont would have to pre-build a YBlock outside of its constructor.
    }

    /**
     * Creates a Block.
     *
     * @param id              its unique id.
     * @param startID         the ID of the starting signal.
     * @param endID           the ID of the starting signal of the following block.
     * @param maxSpeed        its maximum speed.
     * @param sectors         its sectors.
     * @param switchPositions its switchPositions.
     */
    public Block(String id, String startID, String endID, boolean direction, byte maxSpeed,
                 ArrayList<Sector> sectors,
                 ArrayList<SwitchPosition> switchPositions) {
        this.id = id;
        this.startId = startID;
        this.endId = endID;
        this.direction = direction;
        this.maxSpeed = maxSpeed;
        this.sectors = sectors;
        this.switchPositions = switchPositions;
        for (Sector sector : sectors) {
            this.length = sector.getLength() + length;
        }

    }

    /**
     * to add set the nextblocks ArrayList
     *
     * @param next a block following this block
     */
    public void addNextBlocks(ArrayList<Block> next) {
        nextBlocks = next;
    }


    /**
     * @return the IDs of the starting signals.
     */
    public String getStartId() {
        return startId;
    }

    /**
     * @return the ID of the starting signal of the following block.
     */
    public String getEndId() {
        return endId;
    }

    /**
     * @return the max speed a train may drive in this block.
     */
    public byte getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Sets the maximum speed allowed on the block.
     *
     * @param s
     */
    public void setMaxSpeed(byte s) {
        maxSpeed = s;
    }


    /**
     * Returns the direction of the block.
     *
     * @return either 0 or 1
     */
    public boolean getDirection() {
        /**
         * Direction of the Block
         * Fribourg-->Schwarzssee = 0
         */
        return this.direction;
    }

    /**
     * Sets the direction of the block. The parameter must be either
     * 0 or 1.
     *
     * @param dir
     */
    public void setDirection(boolean dir) {
        this.direction = dir;
    }

    /**
     * @return the first sector of the block. It is used for detecting
     * when a train enters it.
     */
    public Sector getFirstSector() {
        return sectors.get(0);
    }

    /**
     * @return the last sector of the block. It is used for detecting
     * when a train is about to leave it.
     */
    public Sector getLastSector() {
        return sectors.get(sectors.size() - 1);
    }

    /**
     * @return the list of the blocks which start signal is the end signal
     * of the instance whose method is called.
     */
    public ArrayList<Block> getNextBlocks() {
        return nextBlocks;
    }

    /**
     * @return a list containing the sectors of the block. They are in
     * the correct order.
     */
    public ArrayList<Sector> getSectors() {
        return sectors;
    }

    /**
     * @return an estimation of the length of the block, in centimeters.
     */
    public float getLength() {
        return length;
    }

    /**
     * Sets the length of the block, in centimeters.
     *
     * @param length
     */
    public void setLength(float length) {
        this.length = length;
    }

    /**
     * @return the crossing time of the block : the length divided by
     * the maximal allowed speed. The result it <b>not</b> in seconds.
     * If the block is a YBlock, multiply the value by 5.
     */
    public float getCrossingTime() {
        float time;
        if (this.getClass() == YBlock.class) {
            time = (length / maxSpeed) * 5;
        } else {
            time = length / maxSpeed;
        }
        return time;
    }

    /**
     * @return the start signal of the block
     */
    public Signal getStartSignal() {
        return startSignal;
    }

    /**
     * Sets the start signal of the block
     *
     * @param s
     */
    public void setStartSignal(Signal s) {
        startSignal = s;
    }

    /**
     * @return the end signal of the block
     */
    public Signal getEndSignal() {
        return endSignal;
    }

    /**
     * Sets the end signal of the block
     *
     * @param s
     */
    public void setEndSignal(Signal s) {
        endSignal = s;
    }

    /**
     * @return true if any sector of the block is occupied, otherwise false.
     */
    public boolean isOccupied() {
        int counter = 0;
        while (counter < sectors.size()) {
            if (sectors.get(counter).isOccupied()) {
                return true;
            }
            counter = counter + 1;
        }
        return false;
    }

    /**
     * @return true if any sector of the block is locked, otherwise false.
     */
    public boolean isLocked() {
        int counter = 0;
        while (counter < sectors.size()) {
            if (sectors.get(counter).isLocked()) {
                return true;
            }
            counter = counter + 1;
        }
        return false;
    }

    /**
     * @return true if the block is securable, that is not occupied nor locked.
     */
    public boolean isSecurable() {
        if (!isOccupied() && !isLocked()) {
            return true;
        }
        return false;
    }

    /**
     * Locks all sectors of the block.
     */
    public void lockSectors() {
        for (Sector sector : sectors) {
            sector.lock(this);
        }
    }

    /**
     * Unlocks all sectors of the block.
     */
    public void unlockSectors() {
        for (Sector sector : sectors) {
            sector.unlock(this);
        }
    }

    /**
     * Set the switches of the block in the correct position, if they
     * are locked by the block.
     */
    public void setSwitches() {
        for (SwitchPosition switchPosition : switchPositions) {
            switchPosition.setPosition();
        }
    }

    /**
     * Returns true if the first sector is occupied, otherwise false.
     */
    public boolean isFirstSectorOccupied() {
        return getFirstSector().isOccupied();
    }

    /**
     * Returns true if the last sector of the block is occupied, otherwise false
     */
    public boolean isLastSectorOccupied() {
        return getLastSector().isOccupied();
    }

    /**
     * Returns the list of switchPositions of the block.
     *
     * @return the switchPositions
     */
    public ArrayList<SwitchPosition> getSwitchPositions() {
        return switchPositions;
    }

    /**
     * Returns true if the block contains a stop. Only YBlocks contain stops,
     * therefore this method returns always false.
     */
    public boolean containsStop() {
        return false;
    }
}
