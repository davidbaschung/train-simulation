package events;

import railway.Train;

/**
 * @author Alex Guardini & David Baschung
 *
 * Unlocks the last used block on a train's as soon as it is not occupied anymore
 */
public class OnBlockLeft extends Observer {

    public OnBlockLeft(Train train) {
        super(train);
        this.start();
    }

    /**
     * @return Tests the block for occupation
     */
    @Override
    protected boolean test() {
        return !(train.getRoute().getCurrentBlock().isOccupied());
    }

    /**
     * OnBlockLeft unlocks and unreserves the blocks.
     * It also tells the Route that we reached the next blok.
     */
    @Override
    protected void process() {
        logger.info("Unreserved block " + train.getRoute().getCurrentBlock().getId());
        train.getRoute().getCurrentBlock().unlockSectors();
        train.getRoute().reachedNextBlock();
        if (!train.getRoute().getCurrentBlock().getEndId().equals(train.getRoute().getLastBlock().getEndId())) {
            train.unreserveBlock();
        }
    }
}
