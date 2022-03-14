/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author Jean
 * <p>
 * Taken from here http://www.vogella.com/tutorials/Logging/article.html with a
 * few arrangments
 */
public class HtmlFormatter extends Formatter {

	@Override
	public String format(LogRecord lr) {
		StringBuilder sb = new StringBuilder(1000);
		String loggerName = lr.getLoggerName();

		sb.append("<tr class = '" + loggerName.replace('.', '-') + " " + lr.getLevel() + "'>\n");

		sb.append("\t<td>");
		sb.append(loggerName);
		sb.append("</td>\n");

		sb.append("\t<td>");
		sb.append(lr.getLevel());
		sb.append("</td>\n");

		sb.append("\t<td>");
		sb.append(calcDate(lr.getMillis()));
		sb.append("</td>\n");

		sb.append("\t<td>");
		sb.append(formatMessage(lr));
		sb.append("</td>\n");

		sb.append("</tr>\n");

		return sb.toString();
	}

	private String calcDate(long millisecs) {
		SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss:SSS");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

	// this method is called just after the handler using this formatter is created
	@Override
	public String getHead(Handler h) {
		return "<!DOCTYPE html>\n"
				+ "<head>\n"
				+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>"
				+ "<script src = \"script.js\"></script>"
				+ "<style>\n"
				+ "table { width: 100% }\n"
				+ "th { font:bold 10pt Tahoma; }\n"
				+ "td { font:normal 10pt Tahoma; }\n"
				+ "h1 { font:normal 11pt Tahoma;}\n"
				+ ".Trains-redTrain {background-color: #ffcccc;}\n"
				+ ".Trains-blueTrain {background-color: #ccccff;}\n"
				+ ".Trains-greenTrain {background-color: #ccffcc;}\n"
				+ "</style>\n"
				+ "</head>\n"
				+ "<body>\n"
				+ "<h1>" + (new Date()) + "</h1>\n"
				+ "<div class=\"before_table\"></div>"
				+ "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n"
				+ "<tr align=\"left\">\n"
				+ "\t<th style=\"width:5%\">Train</th>\n"
				+ "\t<th style=\"width:5%\">Loglevel</th>\n"
				+ "\t<th style=\"width:10%\">Time</th>\n"
				+ "\t<th style=\"width:85%\">Log Message</th>\n"
				+ "</tr>\n";
	}

	// this method is called just after the handler using this formatter is closed
	@Override
	public String getTail(Handler h) {
		return "</table>\n</body>\n</html>";
	}
}
