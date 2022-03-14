package apps;

import gnu.io.SerialPort;
import railway.Railway;
import railway.RailwayFactory;
import railway.Switch;
import sx.SX;

public class StwitchTests {

	public static void main(String[] args) throws Exception {
		/*
		 **************************** railway configuration
		 */
		Railway railway = RailwayFactory.getInstance().getConfiguredRailway();


		/*
		 *************************** railway system connection
		 */
		SX.instance().configPort("/dev/ttyS0", 19200, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		try {
			SX.instance().initPort();
			Thread.sleep(100);
		} catch (Throwable e) {
			System.out.println("[" + e.getMessage() + "]");
		}


		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		railway.initSignals();


		for (Switch s : railway.getSwitches()) {
			for (int i = 0; i < 20; i++) {
				s.setPosition(i % 2 == 1);
				Thread.sleep(250);
			}
		}
	}
}
