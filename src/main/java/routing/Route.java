package routing;

import railway.Block;
import railway.RailwayElement;
import railway.YBlock;

import java.util.ArrayList;

/**
 * Represents a route (sequence of blocks) a train follows to reach the next station on its line.
 */
public class Route extends RailwayElement {

    /**
     * The index of the block on which the train is
     */
    private int currentBlockIndex = 0;
    /**
     * the sequence of blocks leading to aim station
     */
    private ArrayList<Block> blocks;

    public Route(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }


    /**
     * @return the current block
     */
    public Block getCurrentBlock() {
        return blocks.get(currentBlockIndex);
    }

    /**
     * @return the next block
     */
    public Block getNextBlock() {
        return blocks.get(currentBlockIndex + 1);
    }

    /**
     * Tells the route that the train entered the next block (updates currentBlockIndex)
     **/
    public void reachedNextBlock() {
        currentBlockIndex++;
    }

    /**
     * @return the last block of the route
     */
    public Block getLastBlock() {
        return blocks.get(blocks.size() - 1);
    }

    /**
     * @return the first block of the route
     */
    private Block getFirstBlock() {
        return blocks.get(0);
    }

    /**
     * @return the blocks of the route
     */
    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    /**
     * locks the sectors of each block of the route (except for the first block).
     */
    public void lockSectors() {
        for (int i = 1; i < blocks.size() - 1; i++) {
            blocks.get(i).lockSectors();
        }
    }

    /**
     * unlocks the sectors of each block of the route (except for the first block).
     */
    public void unlockSectors() {
        for (int i = 1; i < blocks.size() - 1; i++) {
            blocks.get(i).unlockSectors();
        }
    }

    /**
     * @return true if the train is in the last block of its route
     */
    public boolean isInLastBlock() {
        return blocks.get(blocks.size() - 1).isOccupied();

    }

    /**
     * set the switches between the current position of the train and
     * the stop sector of the first encountered yblock - or the
     * end of the route
     */
    public void setNextSwitches() {
        for (int i = currentBlockIndex; i < blocks.size(); i++) {
            if (blocks.get(i).containsStop()) {
                if (i == currentBlockIndex) {
                    ((YBlock) blocks.get(i)).setNextSwitches();
                } else {
                    blocks.get(i).setSwitches();
                    return;
                }
            } else {
                //If the current block is not a YBlock, don't switch its switches.
                if (i > currentBlockIndex) {
                    blocks.get(i).setSwitches();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sets the signals between the current position of the train and the
     * stop of the first encountered yblock - or the end of the route
     */
    public void setNextSignals() {
        for (int i = currentBlockIndex; i < blocks.size(); i++) {
            boolean current = (i == currentBlockIndex);
            boolean last = (i == blocks.size() - 1);
            // is it a YBlock ?
            if (blocks.get(i).containsStop()) {
                YBlock yb = (YBlock) blocks.get(i);
                if (!current) {
                    yb.getStartSignal().setShort();
                    yb.getStopSignal().setStop();
                    yb.getEndSignal().setStop(); //safety measure
                    break;
                } else {
                    yb.getStartSignal().setStop(); //safety measure
                }
            } else {
                // then it is a Block
                Block b = blocks.get(i);
                if (!current && !last) {
                    b.getStartSignal().setSpeed(b.getMaxSpeed());

                } else if (!current) {
                    b.getStartSignal().setShort();
                    b.getEndSignal().setStop(); //safety measure
                }
            }
        }
    }

    /**
     * @return the id of the route
     */
    @Override
    public String getId() {
        return this.getFirstBlock().getId() + blocks.toString();
    }

    /**
     * @return the IDs of the blocks
     */
    @Override
    public String toString() {
        return blocks.toString();
    }

}
