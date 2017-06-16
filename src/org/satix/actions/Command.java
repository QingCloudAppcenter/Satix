package org.satix.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * This action is used to execute external program. If external program 
 * output message, it can also verify if output message is correct or not.
 *
 */
public class Command extends Action {
	private static final String CLASSNAME = Click.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private String commandPath;
	private String command;
	private String parameter;
	private String condition;
	private String resultString;
	private String stopIfException;

	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		String path = getCommandPath();
		String command = "";
		if (null==path || "".equals(path.trim())) { //command in system path
			path = null;
			command = getCommand() + " " + getParameter();
		} else if (!getCommandPath().endsWith(File.separator)) {
			command = path + File.separator + getCommand();
		} else if (getCommandPath().endsWith(File.separator)) {
			command = path + getCommand();
		}
		
		if (path != null) {
			File file = new File(command);
			if (!file.exists()) {
				throw new ActionException(this, "Command does not exist: " + command);
			}
			command = command + " " + getParameter();
		}
		 
		logger.log(Level.INFO, "Command is : " + command);
		
		InputStream normalIn = null;
		BufferedReader normalBr = null;
		InputStream errorIn = null;
		BufferedReader errorBr = null;
		try {
			boolean ret = true;
			Process p = Runtime.getRuntime().exec("" + command);

			StringBuffer sb = new StringBuffer();
			String line = null;

			normalIn = p.getInputStream();
			normalBr = new BufferedReader(new InputStreamReader(normalIn));
			line = normalBr.readLine();
			while (line != null) {
				logger.log(Level.INFO, line);
				sb.append(line);
				line = normalBr.readLine();
			}
			errorIn = p.getErrorStream();
			errorBr = new BufferedReader(new InputStreamReader(errorIn));
			line = errorBr.readLine();
			while (line != null) {
				logger.log(Level.INFO, line);
				sb.append(line);
				line = errorBr.readLine();
			}
			if (condition != null && condition.equals("contain")) {
				String[] verifyStrings = getResultString().split("\\|");
				boolean stringVerificationResult = true;
				for (int i=0; i<verifyStrings.length; i++) {					
					if (sb.indexOf(verifyStrings[i]) == -1) {
						stringVerificationResult = (stringVerificationResult & false);
					} else {					
						stringVerificationResult = (stringVerificationResult & true);
					}
				}
				
				if (!(stringVerificationResult)) {
					ret = false;
				}
			} else if (condition!=null && condition.equals("not-contain")) {
				String[] verifyStrings = getResultString().split("\\|");
				boolean stringVerificationResult = true;
				for (int i=0; i<verifyStrings.length; i++) {					
					if (sb.indexOf(verifyStrings[i]) == -1) {					
						stringVerificationResult = (stringVerificationResult & true);
					} else {
						stringVerificationResult = (stringVerificationResult & false);
					}
				}
				
				if (!(stringVerificationResult)) {
					ret = false;
				}
			}

			p.waitFor();

			if (!ret) {
				getTestcase().setStatus("Failed");
				getTestcase().setErrorLog("Action \"" + getName() + "\" line \"" 
						+ getLine() + "\", command failed!");
			} else {
				logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName()
						+ "\" number \"" + getNumber() + "\" line \"" + getLine() 
						+ "\" " + "successfully, elapsed time:"
						+ (System.currentTimeMillis() - start_time) + "ms");
			}
			if ("true".equalsIgnoreCase(getStopIfException())) {
				if (!ret) {
					throw new ActionException(this, "Action: name \"" + getName() 
							+ "\" number \"" + getNumber() + "\" line \"" + getLine()
							+ "\" command failed!, elapsed time:"
							+ (System.currentTimeMillis() - start_time) + "ms");
				}
			} else {
				if (!ret) {
					logger.log(Level.SEVERE, "********" + getTestcase() + "Action: name \"" 
							+ getName() + "\" number \"" + getNumber() + "\" line \"" 
							+ getLine() + "\" command failed, elapsed time:"
							+ (System.currentTimeMillis() - start_time) + "ms");
				}
			}
		} catch (Exception e) {
			throw new ActionException(this, "Action \"" + getName()
					+ "\" line \"" + getLine() + "\", " + " return ERROR", e);
		} finally {
			try {
				normalIn.close();
				normalBr.close();
				errorIn.close();
				normalBr.close();
			} catch (IOException e) {
				throw new ActionException(this, "Action \"" + getName()
						+ "\" line \"" + getLine() + "\", " + " return ERROR", e);
			}
		}
	}

	@Override
	public WebDriver getBrowserDriver() throws ActionException {
		return null; //no need browser
	}
	
	public String getCommandPath() {
		return commandPath;
	}

	public void setCommandPath(String commandPath) {
		this.commandPath = commandPath;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}

	public String getStopIfException() {
		return stopIfException;
	}

	public void setStopIfException(String stopIfException) {
		this.stopIfException = stopIfException;
	}
}
