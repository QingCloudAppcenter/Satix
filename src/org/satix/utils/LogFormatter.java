package org.satix.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This class defines the formatter for logging.
 *
 */
public class LogFormatter extends Formatter {
  
    @Override
    public String format(LogRecord lr) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("[{0, date} {0, time}] ", new Object[]{ new Date(lr.getMillis()) } ));
        sb.append("[").append(lr.getLevel().getName()).append("] ");
        sb.append(lr.getSourceClassName()).append(".");
        sb.append(lr.getSourceMethodName()).append(": ");
        if (Level.SEVERE.equals(lr.getLevel())) {
        	sb.append(System.getProperty("line.separator"));
        }
        sb.append(formatMessage(lr)).append(System.getProperty("line.separator"));
        if (null != lr.getThrown()) {
            sb.append("Throwable occurred: ");
            Throwable t = lr.getThrown();
            PrintWriter pw = null;
            try {
                StringWriter sw = new StringWriter();
                pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                sb.append(sw.toString());
            } finally {
                if (pw != null) {
                    try {
                        pw.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
        
        return sb.toString();
    }
}