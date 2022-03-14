/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Jean
 */
public class WindowFormatter extends Formatter {
	@Override
	public String format(LogRecord lr) {
		return lr.getMessage();
	}
}
