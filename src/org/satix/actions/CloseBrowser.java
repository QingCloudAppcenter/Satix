package org.satix.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * Close the browser after you finish running your test case.
 * 
 *
 */
public class CloseBrowser extends Action {
	private static final String CLASSNAME = CloseBrowser.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	
	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		driver.quit();
		logger.log(Level.INFO, getTestcase() + "Action: name \"" 
			+ getName() + "\" number \"" + getNumber() + "\" line \"" + getLine() 
			+ "\" passed, elapsed time:" + (System.currentTimeMillis() - start_time) + "ms");
	}
}
