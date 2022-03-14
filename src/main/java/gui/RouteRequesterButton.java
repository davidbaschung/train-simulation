package gui;

import java.awt.*;

/**
 * @author Alex Guardini & David Baschung
 *
 * Graphical implementation of the Route Requester Button
 */
public class RouteRequesterButton extends RoutingButton {

	static Color backGroundColor = new Color(170, 255,0);

	public RouteRequesterButton(GUI gui) { super(gui); }

	@Override
	public void setText() { setText("<html><center>Request<br />ROUTE TO<br />station</center></html>");	}

	@Override
	public Color getBackgroundColor() {
		return backGroundColor;
	}
}
