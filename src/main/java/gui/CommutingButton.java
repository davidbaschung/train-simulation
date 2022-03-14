package gui;

import java.awt.*;

/**
 * @author Alex Guardini & David Baschung
 *
 * Graphical implementation of the Line Commuting Button
 */
public class CommutingButton extends RoutingButton {

	public CommutingButton(GUI gui) {
		super(gui);
	}

	@Override
	public void setText() { setText("<html><center>automatic<br />COMMUTING</center></html>"); }

	@Override
	public Color getBackgroundColor() {
		return Color.GREEN;
	}
}
