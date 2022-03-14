package events;

import gui.GUI;
import gui.RoutingButton;
import railway.Station;
import railway.Train;

import java.util.ArrayList;

/**
 * @author Alex Guardini & David Baschung
 *
 * Makes the train Stop. After a Y-Block, it will reverse the direction of the train if it's necessary to continue on the Route.
 */
public class OnTrainMustStop extends Observer{
    public static GUI gui;

    public OnTrainMustStop(Train train) {
        super(train);
        this.start();
    }

    /** swap the direction or not*/
    private boolean swapDir;
    /** stop to next station or not */
    private boolean stopStation;

    /**
     * @return Tests if the train must be stopped. Returns true if :
     * - The train crossed a Y-block and must swap the direction to continue on the Route
     * - The train has been slowed down and must now be stopped
     */
    @Override
    protected boolean test() {
        boolean isYBlock = train.getRoute().getCurrentBlock().containsStop();
        boolean isAtEndOfYBlock = train.getRoute().getCurrentBlock().isLastSectorOccupied();
        boolean mustSwitchDirection = train.getRoute().getCurrentBlock().getDirection() != train.getLocomotive().getDirection();

        swapDir = isYBlock && isAtEndOfYBlock && mustSwitchDirection;

        boolean atEnd = train.getRoute().getLastBlock().isLastSectorOccupied();
        boolean sameDirection = train.getRoute().getLastBlock().getDirection() == train.getLocomotive().getDirection();

        boolean stopRequired = train.getStopAtStation();
        boolean currentEnd = train.getRoute().getCurrentBlock().isLastSectorOccupied();
        boolean firstStation = train.getRoute().getBlocks().get(0).getId().equals(train.getRoute().getCurrentBlock().getId());
        boolean currentIsStation = false;

        if (train.getRoute().getCurrentBlock() != train.getRoute().getLastBlock()){
            for (Station station : railway.getStations()){
                for (String signal : station.getSignals()){
                    if (signal.equals(train.getRoute().getCurrentBlock().getEndSignal().getId())){
                        currentIsStation = true;
                    }
                }
            }
        }

        stopStation = stopRequired && currentEnd && !firstStation && currentIsStation;

        return (atEnd && sameDirection) || swapDir || stopStation;
    }

    /**
     * Stops the train first and sets the speed slider to 0.
     * If a direction-swap was requested in test(), 2 observers are started, that will stop the train and change it's direction after.
     * Else, if no direction-swap was requested, we arrived to a station and need to stop. We unlock the remaining blocks of the route
     * and remove the unactivated observers. If a Line was activated, we request a route to the next station. Otherwise, we return to
     * MANUAL mode and adapt the GUI.
     */
    @Override
    protected void process() {
        train.getLocomotive().reachSpeed((byte) 0);
        if (getTrain().getId().equals(gui.cbChoiceTrain.getSelectedItem()))
            gui.slSpeed.setValue(0);
        else
            getTrain().getLocomotive().reachSpeed((byte) 0);

        if (swapDir) {
            train.switchDirection();
        } else if (stopStation) {
            train.setSLOWING_DOWN(false);
            train.getRoute().getCurrentBlock().unlockSectors();
            train.setSignalId(train.getRoute().getCurrentBlock().getEndId());

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            train.getLocomotive().reachSpeed((byte) 31);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            railway.getObservers().add(new OnTrainMustSlowDown(train));
            railway.getObservers().add(new OnTrainMustStop(train));
        } else {
            train.getLogger().info("The " + train.getId() + " arrived to "+getTrain().getStationName()+"!");
            train.setSLOWING_DOWN(false);
            train.getRoute().getCurrentBlock().unlockSectors();
            train.setSignalId(train.getRoute().getCurrentBlock().getEndId());
            ArrayList<Observer> obs = railway.getObservers();
            for (int i=0; i<obs.size(); ){
                if (obs.get(i).getTrain().getId().equals(train.getId())) {
                    obs.get(i).pauseThread();
                    obs.remove(obs.get(i));
                } else {
                    i++;
                }
            }
            train.setRoute(null);

            try{
                Thread.sleep(7000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            train.setMoving(false);
            if (train.getId().equals(gui.cbChoiceTrain.getSelectedItem()) && ! (train.getState()==Train.states.COMMUTING)) {
                RoutingButton.setAllInstancesToManualState();
            }
        }

        if (!stopStation && !swapDir){
            if (train.getState()==Train.states.COMMUTING) {
                getTrain().getLine().reachedNextStation();
                getTrain().setRouteChosen(true);
                getTrain().setMoving(false);
            } else // state = ROUTING
                getTrain().setState(Train.states.MANUAL);
        }
    }
}
