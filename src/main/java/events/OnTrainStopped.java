package events;

import railway.Train;
import static gui.GUI.slSpeed;

/**
 * @author Alex Guardini & David Baschung
 *
 * Makes the train continue it's route once it has been stopped.
 */
public class OnTrainStopped extends Observer {

    public OnTrainStopped(Train train) {
        super(train);
        this.start();
    }

    /**
     * @return Tests if the train has stopped
     */
    @Override
    protected boolean test() {
        return train.getLocomotive().getSpeed() == 0;
    }

    /**
     * Apply the conditions to continue the route : the direction, the switches, the speed.
     * This applies particularly when the train was stopped after Y-block.
     */
    @Override
    protected void process() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (train.getRoute() != null){
            train.getLocomotive().setDirection(!train.getLocomotive().getDirection());
            train.getRoute().setNextSwitches();
            train.getLocomotive().reachSpeed((byte) 31);
            train.slowDown();
            train.stopTrain();
        } else {
            train.getLocomotive().reachSpeed((byte) slSpeed.getValue());
        }
    }
}
