package railway;

import apps.Main;
import sx.SX;

import java.util.logging.Logger;

/**
 * Represents a locomotive of the miniature railway system.
 */
public class Locomotive extends RailwayElement implements Runnable {
    /**
     * Must absolutely be equals to 0, for it is the speed a locomotive
     * must have when it is stopped.
     */
    public final static byte stop = 0;
    /**
     * Maximum speed of the locomotive.
     */
    public final static byte driveMax = 31;
    /**
     * Speed "40" of the locomotive
     */
    public final static byte drive40 = 20;
    /**
     * Speed "60" of the locomotive
     */
    public final static byte drive60 = 25;
    /**
     * Speed 90 of the locomotive
     */
    public final static byte drive90 = 29;
    /**
     * Speed of the locomotive before a stop
     */
    public final static byte driveShort = 15;

    private byte speedBeforeEmergency;

    /**
     * This variable is set to true when the locomotive is in an
     * emergency stop state.
     */
    private boolean emergency = false;
    /**
     * Byte address of the locomotive.
     */
    private byte address;
    /**
     * Desired speed of the locomotive. The locomotive will change
     * its speed slowly to make it match this value.
     */
    private byte desiredSpeed = 0;
    /**
     * Thread used for accelerating the locomotive slowly.
     */
    private Thread speedingThread;
    /**
     * Length of the locomotive
     */
    private int length;
    /**
     * inertia of the locomotive
     */
    private int inertia;

    private Logger logger;

    /**
     * Constructor of the class
     *
     * @param id
     * @param address
     * @param length
     * @param inertia
     */
    public Locomotive(String id, byte address, int length, int inertia) {
        this.id = id;
        this.address = address;
        this.length = length;
        this.inertia = inertia;
    }

    /**
     * Makes the locomotive stop very fast. It can be used in order to
     * avoid a collision. Emergency should be set to true and the current
     * desiredSpeed should be saved for later usage.
     */
    public void emergencyStop() {
        this.speedBeforeEmergency = this.desiredSpeed;
        this.setSpeed((byte) 0);
        this.emergency = true;
    }

    /**
     * Tells a locomotive that it can resume its journeys after an
     * emergency stop. (use speed stored in emergencyStop()).
     */
    public void emergencyContinue() {
        this.emergency = false;
        reachSpeed(speedBeforeEmergency);
    }

    /**
     * @return true if the locomotive is in an emergency stop state.
     */
    public boolean isEmergencyStopped() {
        return emergency;
    }

    /**
     * @return the current speed of the locomotive.
     */
    public byte getSpeed() {
        int status = SX.instance().getStatusByte(address);
        int speed = status & 31;

        return (byte) speed;
    }

    /**
     * Sets immediately the speed of a locomotive to
     * a given speed (if speed is ranged from 0 to 31).
     *
     * @param speed
     */
    private void setSpeed(byte speed) {

        SX.instance().writeLock(address);
        byte stat = SX.instance().getStatusByte(address);
        stat = (byte) (stat & 0b11100000);
        SX.instance().setStatusByte(address, (byte) (speed | stat));
        SX.instance().writeUnlock(address);
    }

    /**
     * @return the desired speed of the locomotive
     */
    public int getDesiredSpeed() {
        return desiredSpeed;
    }


    public void setDesiredSpeed(byte desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }
    /**
     * @return the speed of the locomotive before an
     * emergency stop.
     */
    public byte getSpeedBeforeEmergency() {
        /**
         * Backup of the speed of the locomotive before an emergency stop, so
         * that when the emergency is over the locomotive can continue its
         * journey.
         */
        return speedBeforeEmergency;
    }

    /**
     * Tells the locomotive to reach a given speed.
     *
     * @param speed
     */
    public void reachSpeed(byte speed) {
        if (!emergency) {
            this.desiredSpeed = speed;
            // create and start new speed thread if needed
            if (speedingThread == null || !speedingThread.isAlive()) {
                this.speedingThread = new Thread(this);
                this.speedingThread.start();
            }
        }
    }

    /**
     * acceleration process.
     * program executed by the drivingThread of the locomotive.
     * special handling in case of emergency
     */
    @Override
    public void run() {
        if (emergency) {
            this.setSpeed(stop);
        } else {
            while (this.desiredSpeed != this.getSpeed() && !emergency) {
                if (this.desiredSpeed < this.getSpeed()) {
                    this.setSpeed((byte) (this.getSpeed() - 1));
                    try {
                        Thread.sleep(50 + this.getSpeed() * 10);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    this.setSpeed((byte) (this.getSpeed() + 1));
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    /**
     * @return the direction of the locomotive. (false is forward)
     */
    public boolean getDirection() {                 //TODO: implement
        return SX.instance().getStatusBit(address, (byte) 5);
    }

    /**
     * Changes the direction of the locomotive. This method should be
     * called only when the locomotive is stopped.
     *
     * @param b direction
     */
    public void setDirection(boolean b) {
        SX.instance().setStatusBit(address, (byte) 5, b);
    }

    /**
     * @return the state of the locomotive lights.
     */
    public boolean isLightOn() {
        return SX.instance().getStatusBit(address, (byte) 6);
    }

    /**
     * sets the state of the locomotive lights on or off.
     *
     * @param on
     */
    public void setLight(boolean on) {
        SX.instance().setStatusBit(address, (byte) 6, on);
    }


    /**
     * Returns the length of the locomotive, in centimeters
     *
     * @return the length
     */
    public int getLength() {
		/*
	    Length of the locomotive, in centimeters.
	    */
        return length;
    }

    /**
     * Returns the address of the locomotive
     *
     * @return the address
     */
    public int getAddress() {
        return address;
    }

    /**
     * Returns the inertia of the locomotive
     *
     * @return the inertia
     */
    public int getInertia() {
		/*
		Inertia of the locomotive. It should be equals to 0 in the XML file
		for the yellow locomotive, 1 for the other ones. A value of 0 will
		greatly increase the accelerations of the locomotive.
	    */
        return inertia;
    }
}
