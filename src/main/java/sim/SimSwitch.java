/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

import apps.Main;
import railway.Switch;

/**
 * This classes watches a switch and logs any change.
 *
 * @author ms
 */
public class SimSwitch implements Element {
	/**
	 * The switch being watched.
	 */
	Switch sw;

	/**
	 * Its last detected position.
	 */
	boolean position;

	/**
	 * Constructor.
	 *
	 * @param s
	 */
	public SimSwitch(Switch s) {
		sw = s;
		position = sw.getPosition();
	}

	/**
	 * Logs any change.
	 */
	@Override
	public void update() {
		boolean newPosition = sw.getPosition();
		if (newPosition != position) {
			Main.getRailwayLogger("Simulation").info("Sim: " + this + " moved to position " + newPosition);
			position = newPosition;
		}
	}

	/**
	 * Displayable name.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "Sim:" + sw;
	}
}
