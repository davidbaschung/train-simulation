package sx;

/**
 * SX acts as a proxy to the unique SelectrixInterface of a program. It either
 * instanciate a SelectrixCommunicator for real communication to the miniature
 * railway, or a SelectrixEmulator for emulating communication to the miniature
 * railway (TBA later)
 *
 * @author benoitpointet
 */
public class SX {

	private static boolean isEmulating = false;
	private static SelectrixInterface instance = null;

	private SX() {
	}

	public static SelectrixInterface instance() {
		if (instance == null) {
			if (isEmulating) {
				instance = new SelectrixEmulator();
			} else {
				instance = new SelectrixCommunicator();
			}
		}
		return instance;
	}

	public static void startEmulation() {
		isEmulating = true;
		instance = null;
		instance();
		instance.initPort();
	}
}
