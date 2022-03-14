package railway;


import apps.Main;
import events.*;
import gui.GUI;
import gui.TrainLogPanel;
import logging.WindowFormatter;
import logging.WindowHandler;
import routing.Route;
import routing.RouteFactory;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Represents a train of the miniature railway system.
 */
public class Train extends RailwayElement {

    /** If the route has already been chosen, used to start a Route or a Line */
    private boolean routeChosen;
    /** If the train is moving, used to start a Route or a Line */
    private boolean moving;
    /** If the train is slowing down and must have a limited speed, e.g. when arriving to a station */
    private boolean SLOWING_DOWN = false;
    /** If this is the first station of the Line, so the Logger can put initializing informations */
    private boolean isFirstStation = true;
    /** If the train must stop at stations during a Line */
    private boolean stopAtStation = false;

    boolean isDeleted = false;
    /**
     * The last destination that a user requested.
     * Used to set back the destination name in the destinations choice, when we re-choose this train in train choice
     */
    private String stationName = null;

    /** logger of this train */
    private final Logger logger;
    private final WindowHandler handler;

    public static GUI gui;
    /** state of this train */
    private states state ;
    /** the states of the train. Used to manage the GUI buttons and the Line commuting */
    public enum states {
        MANUAL,
        WAITROUTING,
        WAITCOMMUTING,
        ROUTING,
        COMMUTING;
    }
    /**
     * the line (sequence of stations) this train follows
     */
    private Line line = null;
    /**
     * the route (sequence of blocks) this train follows
     */
    private Route route = null;
    /**
     * the locomotive driving this train
     */
    private Locomotive locomotive = null;
    /**
     * The ID of the signal at the head of the train
     */
    private String signalId = "";


    /**
     * creates a Train.
     *
     * @param id         its unique id.
     * @param locomotive its locomotive.
     */
    public Train(String id, Locomotive locomotive) {
        this.id = id;
        this.locomotive = locomotive;
        this.routeChosen = false;
        this.state = states.MANUAL;
        logger = Main.getTrainLogger(this);

        // The GUI is not yet created, it will print on the console
        handler = new WindowHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.FINEST);
        logger.addHandler(handler);
        logger.finest("Train " + id + " with locomotive " + locomotive.getId() + " initialized");
    }

    /**
     * This method is called when the GUI is being made, to assign a panel to
     * the handler
     *
     * @param logPanel
     */
    public void setLoggerWindowHandler(TrainLogPanel logPanel) {
        handler.setLogPanel(logPanel);
        handler.setFormatter(new WindowFormatter());
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * @return an estimation of the length of the train
     */
    public int getLength() {
        return locomotive.getLength();
    }

    /**
     * @return the line of the train
     */
    public Line getLine() {
        return line;
    }

    /**
     * Sets the line of the train
     *
     * @param line
     */
    public void setLine(Line line) {
        this.line = line;
    }

    /**
     * @return the locomotive of the train
     */
    public Locomotive getLocomotive() {
        return locomotive;
    }

    /**
     * Sets the locomotive of the train
     *
     * @param locomotive
     */
    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    /**
     * @return the route of the train
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Sets the route of the train
     *
     * @param route
     */
    public void setRoute(Route route) {
        if (route != null) {
            logger.info("got route " + route.getId());
            if (state==states.WAITROUTING) {
                setState(states.ROUTING);
                if (getId().equals(gui.cbChoiceTrain.getSelectedItem()))
                    gui.routeRequester.setAutoState();
            } else if (state==states.WAITCOMMUTING) {
                setState(states.COMMUTING);
                if (getId().equals(gui.cbChoiceTrain.getSelectedItem()))
                    gui.commuting.setAutoState();
            }
//            if (state==states.COMMUTING)
//                getLocomotive().setDirection(route.getCurrentBlock().getDirection());
            if (getId().equals(gui.cbChoiceTrain.getSelectedItem()))
                gui.slSpeed.setValue(gui.slSpeed.getMaximum());
            else getLocomotive().reachSpeed(Locomotive.driveMax);
        } else {
            logger.info("got route removed");
            logger.info("");
            if (getState() != states.COMMUTING)
                setState(states.MANUAL);
        }
        this.route = route;
    }

    /**
     * Returns
     *
     * @return the id at the signal at which the train is stopped
     */
    public String getSignalId() {
        return signalId;
    }

    /**
     * Sets the signal at which the train is stopped
     *
     * @param signalId
     */
    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    /**
     * Makes the train start its line.
     * 1. Start an OnRouteSecured observer for the train
     * 2. Request a route by calling the requestRoute method
     * of the RouteFactory.
     */

    public void startLine() {
        String stations = new String();
        if (isFirstStation) {
            for (Station s : getLine().getStations())
                stations += s.getId() + ", ";
            logger.info("Line commuting started for stations : " + stations);
            isFirstStation = false;
        }
        this.startRoute(getLine().getCurrentStation().getId());
    }

    public void startRoute(String routeId) {
        logger.info("Route requested to " + routeId);
        this.setStationName(routeId);
        this.setRouteChosen(false);
        this.setMoving(true);
        Railway.instance().getObservers().add(new OnRouteSecured(this));
        RouteFactory.instance().requestRoute(this, Railway.instance().getStationById(routeId));
    }

    public void unreserveBlock() {
        Railway.instance().getObservers().add(new OnBlockLeft(this));
    }

    public void switchDirection() {
        Railway.instance().getObservers().add(new OnTrainStopped(this));
    }

    public void slowDown() {
        Railway.instance().getObservers().add(new OnTrainMustSlowDown(this));
    }

    public void stopTrain() { Railway.instance().getObservers().add(new OnTrainMustStop(this)); }

//    public void detectEmergencies() { Railway.instance().getObservers().add(new OnEmergency(this)); }


    public boolean getRouteChosen() {
        return routeChosen;
    }

    public void setRouteChosen(boolean bool) {
        this.routeChosen = bool;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String station) {
        this.stationName = station;
    }

    public boolean getMoving(){
        return moving;
    }

    public void setMoving(boolean bool){
        this.moving = bool;
    }

    public Logger getLogger(){
        return logger;
    }

    public void setIsFirstStation(boolean firstStation) { this.isFirstStation = firstStation; }

    public states getState() { return state; }

    public void setState(states state) { this.state = state; }


    public void setSLOWING_DOWN(boolean bool){
        this.SLOWING_DOWN = bool;
    }

    public boolean getSLOWING_DOWN(){
        return SLOWING_DOWN;
    }

    public void setStopAtStation (boolean bool){
        this.stopAtStation = bool;
    }

    public boolean getStopAtStation(){
        return stopAtStation;
    }
}