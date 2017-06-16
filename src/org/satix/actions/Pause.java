package org.satix.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.BrowserDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * This action will pause Satix runtime untill you hit ENTER.
 *
 */
public class Pause extends Action {
	private static final String CLASSNAME = Pause.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);	

	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String msg = new String("Satix is paused, Press Enter to continue Satix.");
			logger.log(Level.INFO, msg);
			br.readLine();
			br.close();
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
					+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (IOException e) {
			throw new ActionException(this, e);
		}
	}
	
	@Override
	public WebDriver getBrowserDriver() throws ActionException {
		WebDriver driver = BrowserDriver.getBrowserDriver();
		return driver;
	}
}
