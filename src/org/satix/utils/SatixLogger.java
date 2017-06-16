package org.satix.utils;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.satix.constants.ReservedVariables;
import org.satix.properties.SystemConfigurations;


public class SatixLogger extends Logger {
	private static final String CLASSNAME = SatixLogger.class.getSimpleName();
	private static Logger logger = Logger.getLogger(CLASSNAME);

	protected SatixLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}

	/**
	 * Attach three handlers to the logger, regular logging, error logging and console logging.
	 */
	public static void init() throws Exception {
		try {
			System.out.println("outLogPath...");
			String outLogPath = SystemConfigurations.getFolderPath(ReservedVariables.LOGOUTPATH.getName(), "." + File.separator + "logs");
			System.out.println("outLogPath: " + outLogPath);
			String errLogPath = SystemConfigurations.getFolderPath(ReservedVariables.LOGERRPATH.getName(), "." + File.separator + "logs");
			
			File outFile = new File(outLogPath);
			File errFile = new File(errLogPath);
			if (!outFile.exists()) {
				outFile.getParentFile().mkdirs();
				outFile.createNewFile();
				if (!outFile.exists()) {
					throw new Exception("Create out log file failed.");
				}
			} else if (outFile.isDirectory()) {
				outLogPath = outFile.getAbsolutePath() + File.separator + "SystemOut.log";
			}
			if (!errFile.exists()) {
				errFile.getParentFile().mkdirs();
				errFile.createNewFile();
				if (!errFile.exists()) {
					throw new Exception("Create error log file failed.");
				}
			} else if (errFile.isDirectory()) {
				errLogPath = errFile.getAbsolutePath() + File.separator + "SystemErr.log";
			}

			FileHandler fHandlerNormal = null;
			FileHandler fHandlerErr = null;

			fHandlerNormal = new FileHandler(outLogPath, 0, 1, true);
			fHandlerErr = new FileHandler(errLogPath, 0, 1, true);

			logger.setUseParentHandlers(false);// turn off the default handlers

			fHandlerNormal.setLevel(Level.INFO);
			fHandlerNormal.setFormatter(new LogFormatter());
			logger.addHandler(fHandlerNormal);

			fHandlerErr.setLevel(Level.SEVERE);
			fHandlerErr.setFormatter(new LogFormatter());
			logger.addHandler(fHandlerErr);

			ConsoleHandler cHandler = new ConsoleHandler();
			cHandler.setLevel(Level.INFO);
			cHandler.setFormatter(new LogFormatter());
			logger.addHandler(cHandler);
		} catch (Exception e) {
			throw e;
		}
	}

	public static Logger getLogger(String name) {
		return logger;
	}

}
