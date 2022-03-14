/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

import apps.Main;
import railway.Signal;

/**
 * This classes watches a signal.
 *
 * @author ms
 */
public class SimSignal implements Element {
	/**
	 * The signal being watched.
	 */
	Signal sig;

	/**
	 * The last detected speed of the signal.
	 */
	int speed;

	/**
	 * Builds an instance of the class, watching the signal s.
	 *
	 * @param s
	 */
	public SimSignal(Signal s) {
		this.sig = s;
		this.speed = sig.getSpeed();
	}

	/**
	 * Checks if the speed indicated by the signal has changed. If that is the
	 * case, it is logged.
	 */
	@Override
	public void update() {
		int newSpeed = sig.getSpeed();
		if (newSpeed != speed) {
			Main.getRailwayLogger("Simulation").info("Sim: " + this + " set to speed " + newSpeed);
			speed = newSpeed;
		}
	}

	/**
	 * Displayable name.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "Sim:" + sig;
	}

}
