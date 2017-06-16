package org.satix.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.utils.SatixLogger;

/**
 * The action defines how to open a web page.
 *
 */
public class OpenWebPage extends Action {
	private static final String CLASSNAME = OpenWebPage.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) {
		long start_time = System.currentTimeMillis();
		driver.get(getValue());
		logger.log(Level.INFO, getTestcase() + "Action: name \"" 
			+ getName() + "\" number \"" + getNumber() + "\" line \"" + getLine() 
			+ "\" passed, elapsed time:" + (System.currentTimeMillis() - start_time) + "ms");
	}
}
