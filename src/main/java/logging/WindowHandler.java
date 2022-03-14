/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logging;

import gui.TrainLogPanel;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author Jean
 */
public class WindowHandler extends Handler {

	//the panel to which the logging is done
	private TrainLogPanel logPanel;
	private boolean isFirstLog = true;

	/**
	 * This is the overridden publish method of the abstract super class
	 * Handler. This method writes the logging information to the associated
	 * Panel. This method is synchronized to make it thread-safe. In case there
	 * is a problem, it reports the problem with the ErrorManager, only once and
	 * silently ignores the others.
	 *
	 * @record the LogRecord object
	 */
	@Override
	public synchronized void publish(LogRecord record) {
		String message = null;
		//check if the record is loggable
		if (!isLoggable(record)) {
			return;
		}
		try {
			message = getFormatter().format(record);
		} catch (Exception e) {
			reportError(null, e, ErrorManager.FORMAT_FAILURE);
		}
		// If the gui has not been made yet
		if (logPanel == null) {
			System.err.println("GUI not made yet, printing log on console");
			//System.out.println(message);
		} else {
			try {
				if (isFirstLog) {
					logPanel.showInfo(record.getLoggerName().substring(12));
					isFirstLog = false;
				}
				logPanel.showInfo(message);
			} catch (Exception ex) {
				reportError(null, ex, ErrorManager.WRITE_FAILURE);
			}
		}

	}

	public void setLogPanel(TrainLogPanel logPanel) {
		this.logPanel = logPanel;
	}

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}
}
