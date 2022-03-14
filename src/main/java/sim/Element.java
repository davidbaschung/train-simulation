/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

/**
 * This represents an element of the simulation. It may be a simulated train,
 * a sector, ... All it needs is a nice update() function. It will be called
 * at about 13Hz, as it is the case with the update of the railway's buffer.
 *
 * @author ms
 */
public interface Element {
	/**
	 * Updates the data of the element, logs changes.
	 */
	public void update();
}
