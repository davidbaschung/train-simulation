package gui;

import org.apache.batik.swing.JSVGCanvas;
import railway.Block;
import railway.Railway;
import railway.Sector;
import railway.Train;

import java.awt.*;
import java.util.ArrayList;


public class RailwayMap extends JSVGCanvas implements Runnable {

    static final int free = 0;
    static final int locked = 1;
    static final int occupied = 2;
    private static RailwayMap instance;
    private ArrayList<Sector> registeredSectors;

    private RailwayMap() {
        registeredSectors = new ArrayList<>();
        setURI("data/railway_plan.svg");
        Thread t = new Thread(this);
        t.start();
    }

    public static RailwayMap instance() {
        if (instance == null) {
            instance = new RailwayMap();
        }
        return instance;
    }

    public void register(Sector s) {
        if (!registeredSectors.contains(s)) {
            registeredSectors.add(s);
        }
    }

    public void register(Block b) {
        for (Sector s : b.getSectors()) {
            register(s);
        }
    }

    public void unregister(Sector s) {
        registeredSectors.remove(s);
    }

    public void unregister(Block b) {
        for (Sector s : b.getSectors()) {
            unregister(s);
        }
    }

    /**
     * forces an update on the map to have an idea of
     * what your program think its doing.
     * To be used only for debugging.
     *
     * @param sector
     * @param status free, locked or occupied
     */
    public void update(Sector sector, int status) {
        String color;
        if (status == occupied) {
            color = "#f0f";
        } else if (status == locked) {
            color = "#0ff";
        } else {
            color = "#00f";
        }
        svgDocument.getElementById(sector.getId()).setAttribute("stroke", color);
        this.setDocument(svgDocument);
    }

    void update() {
        for (Sector s : registeredSectors) {
            String color;
            if (s.isOccupied()) {
                color = "#f00";
            } else if (s.isLocked()) {
                color = "#0f0";
            } else {
                color = "#000";
            }
            svgDocument.getElementById(s.getId()).setAttribute("stroke", color);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            update();
            this.setDocument(svgDocument);

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}