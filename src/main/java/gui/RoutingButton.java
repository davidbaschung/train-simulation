package gui;

import railway.Train;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Guardini & David Baschung
 *
 * Abstract Button to request a route or a line. When one button is clicked, all the other instances are
 * disabled / enabled, the train's status is updated and some Graphical components are adapted.
 */
public abstract class RoutingButton extends JButton {

	private static Color waitingColor = new Color(217, 255, 159);
	private static List<RoutingButton> instances = new ArrayList<>();
	private static GUI gui;

	public RoutingButton(GUI gui) {
		this.gui = gui;
		instances.add(this);
	}

	/**
	 * When we return to manual state, all the button have to be re-enabled.
	 * The Slider is also set to 0, which implies that the train will stop.
	 */
	public static void setAllInstancesToManualState() {
		for (RoutingButton b : instances) {
			b.setEnabled(true);
			b.setText();
			b.setBackground(b.getBackgroundColor());
			b.setOpaque(true);
		}
		gui.slSpeed.setBackground(Color.YELLOW);
		gui.slSpeed.setEnabled(true);
		gui.chDirection.setEnabled(true);
		gui.cbChoiceStation.setEnabled(true);
		gui.chLine.setEnabled(true);
		gui.chDirection.setSelected(gui.train.getLocomotive().getDirection());
	}

	/**
	 * When the train is waiting for a Route, all request buttons have to be disabled
	 */
	public static void setAllInstancesToWaitingState() {
		for (RoutingButton b : instances) {
			b.setEnabled(false);
			b.setText("<html><center>Route<br />requested<br />(wait)</center></html>");
			b.setBackground(waitingColor);
		}
		gui.slSpeed.setBackground(waitingColor);
		gui.slSpeed.setEnabled(false);
	}

	/**
	 * When an automatic state is set, only the clicked button remains enabled.
	 * The implementation of the behaviour, i.e. Route requesting / Line commuting, is implemented in the action-listener.
	 */
	public void setAutoState() {
		this.setEnabled(true);
		for (RoutingButton b : instances) {
			if (b != this) {
				b.setEnabled(false);
				b.setBackground(null);
			}
		}
		setText("<html><center>get to<br />MANUAL<br />Control</center></html>");
		setOpaque(true);
		setBackground(Color.YELLOW);
		gui.slSpeed.setBackground(getBackgroundColor());
		gui.slSpeed.setEnabled(true);
		gui.chDirection.setEnabled(false);
		gui.cbChoiceStation.setEnabled(false);
		gui.chLine.setEnabled(false);
	}

	/**
	 * Set the button's states, based on the train's state
	 * @param state
	 */
	public void setState(Train.states state) {
		switch (state) {
			case MANUAL: setAllInstancesToManualState(); break;
			case WAITROUTING: setAllInstancesToWaitingState(); break;
			case ROUTING: gui.routeRequester.setAutoState(); break;
			case COMMUTING: gui.commuting.setAutoState(); break;
		}
	}

	/**
	 * abstract basis for graphical settings. Must be implemented to differenciate the buttons behaviour.
	 */
	protected abstract void setText();

	protected abstract Color getBackgroundColor();

	@Override
	public void finalize() { instances.remove(this);	}
}
