package events;

import railway.Station;
import railway.Train;

/**
 * @author Alex Guardini & David Baschung
 *
 * Makes the train slow down when it reaches the last Block of its route
 */
public class OnTrainMustSlowDown extends Observer {

    public OnTrainMustSlowDown(Train train) {
        super(train);
        this.start();
    }

    public static int LIMIT = 10;

    /**
     * @return Tests if the train reached the last block
     */
    @Override
    protected boolean test() {
        boolean stopRequired = train.getStopAtStation();
        boolean inStation = false;
        boolean firstStation = train.getRoute().getBlocks().get(0).getId().equals(train.getRoute().getCurrentBlock().getId());

        if (train.getRoute().getCurrentBlock() != train.getRoute().getLastBlock()) {
            for (Station station : railway.getStations()) {
                for (String signal : station.getSignals()) {
                    if (signal.equals(train.getRoute().getCurrentBlock().getEndSignal().getId())) {
                        inStation = true;
                    }
                }
            }
        }

        if (train.getRoute() != null) {
            return train.getRoute().getLastBlock().isOccupied() || (stopRequired && inStation && !firstStation);
        }
        return false;
    }

    /**
     * Makes the train reach a small speed (and sets the slider)
     */
    @Override
    protected void process() {
        train.setSLOWING_DOWN(true);
        train.getLocomotive().reachSpeed((byte) LIMIT);
    }
}
