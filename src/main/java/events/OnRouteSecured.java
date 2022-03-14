package events;

import railway.Train;

/**
 * @author Alex Guardini & David Baschung
 *
 * Secures the route when it has been assigned to the train
 */
public class OnRouteSecured extends Observer {

    public OnRouteSecured(Train train) {
        super(train);
        this.start();
    }

    /**
     * @return Tests if the requested route has been granted to the train
     */
    protected boolean test() {
        return !(train.getRoute() == null);
    }

    /**
     * When the route is secured, is is locked, switches positions are set and the train reaches the max speed
     * It starts 3 other observers to test :
     * - if the train arrives and must slow down and stop
     * - if we passed a Y-block and must stop to reverse the direction
     * - if the last used block can be unreserved, iteratively, until we reach the end of the route.
     */
    protected void process() {
        train.getRoute().setNextSwitches();
        train.getLocomotive().setDirection(train.getRoute().getCurrentBlock().getDirection());
        train.getLocomotive().reachSpeed((byte) 31);
        train.getRoute().lockSectors();
        train.slowDown();
        train.stopTrain();
        if (train.getRoute().getBlocks().size() > 1){
            train.unreserveBlock();
        }
    }
}
