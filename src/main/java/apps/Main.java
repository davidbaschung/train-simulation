package apps;

import gnu.io.SerialPort;
import gui.GUI;
import gui.RailwayMap;
import logging.HtmlFormatter;
import railway.*;
import sim.Simulator;
import sx.SX;

import java.util.logging.*;

import static gui.GUI.cbChoiceStation;

/**
 * Simple application example to get you started.
 */
public class Main {

    private static final Logger logger = Logger.getLogger("Root");
    private static final boolean sim = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        // Setup the html file log and the xml file log
        // The HtmlFormatter is now deprecated
        logger.setLevel(Level.ALL);
        FileHandler htmlFileHandler = new FileHandler("log/log.html");
        FileHandler xmlFileHandler = new FileHandler("log/log.xml");
        Formatter htmlFormatter = new HtmlFormatter();
        Formatter xmlFormatter = new XMLFormatter();
        htmlFileHandler.setLevel(Level.ALL);
        xmlFileHandler.setLevel(Level.ALL);
        htmlFileHandler.setFormatter(htmlFormatter);
        xmlFileHandler.setFormatter(xmlFormatter);
        logger.addHandler(htmlFileHandler);
        logger.addHandler(xmlFileHandler);
        logger.config("Html file log set up");
        logger.config("Xml file log set up");
        logger.config("Console logger set up for info and above.");

        /*
         **************************** railway configuration
         */
        Railway railway = RailwayFactory.getInstance().getConfiguredRailway();

        Thread.sleep(500);

        /*
         *************************** railway system connection
         */
        SX.instance().configPort("/dev/ttyS0", 19200, SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        try {
            SX.instance().initPort();
            Thread.sleep(500);
        } catch (Throwable e) {
            System.out.println("[" + e.getMessage() + "]");
        }

        if (!SX.instance().hasValidConnection() || sim) {
            logger.warning("No valid connection. If you are using the " +
                    "computer of the lab, check if the railway " +
                    "is switched on and make sure that no other " +
                    "instance of this application is running.");
            SX.startEmulation();
            Simulator sim = new Simulator();
        }

        Thread.sleep(500);

        railway.initSignals();

        for (Switch s : railway.getSwitches()) {
            s.setPosition(false);
        }

        Thread.sleep(500);

        new GUI();      /** creates a GUI for the MANUAL and the 2 AUTOMATIC modes */

        for (Sector s : Railway.instance().getSectors()) {
            RailwayMap.instance().register(s);
        }

        /**
         * Log  the Train name and Signal in it's scrollpane, at the beginning
         */
        for (Train train : railway.getTrains()) {
            for (Station station : railway.getStations()) {
                if (station.getSignals().contains(train.getSignalId())) {
                    Main.getTrainLogger(train).info(train.getId() + " is in " + station.getId() + " (" + train.getSignalId() + ") ");
                }
            }
        }

        /**
         * Main loop! When a train is ready it will either start its Route or Line
         */
        while (true) {
            try {
                Thread.sleep(200);
                for (Train t : railway.getTrains()) {
                    if (t.getRouteChosen() && !t.getMoving()) {
                        if (t.getState() == Train.states.WAITCOMMUTING || t.getState() == Train.states.COMMUTING)
                            t.startLine();
                        else t.startRoute(cbChoiceStation.getSelectedItem().toString());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static Logger getTrainLogger(Train train) {
        return Logger.getLogger("Root.Trains." + train.getId());
    }

    public static Logger getObserverLogger(String id) {
        return Logger.getLogger("Root.Observers." + id);
    }

    public static Logger getRailwayLogger(String id) {
        return Logger.getLogger("Root.Railway." + id);
    }

    public static Logger getSXLogger(String id) {
        return Logger.getLogger("Root.SX." + id);
    }

    public static void quit() {
        System.out.println("Process Control IN.4028, UNIFR\nRailway Project, SP 2020\nGroup 9 :\n - Authors : Alex Guardini, David Baschung\n - Contributor : Enea Rusconi");
        Railway.instance().emergencyStop();
        for (Train t : Railway.instance().getTrains()) {
            t.getLocomotive().emergencyStop();
        }
        logger.exiting("Main", "main");
        SX.instance().closePort();
        System.exit(0);
    }
}
