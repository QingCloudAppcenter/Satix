package org.satix.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.utils.SatixLogger;

/**
 * Refresh your browser by this action.
 * 
 *
 */
public class RefreshBrowser extends Action {
	private static final String CLASSNAME = OpenWebPage.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	public void run(WebDriver driver) {
		long start_time = System.currentTimeMillis();
		driver.navigate().refresh();
		logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
				+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
				+ (System.currentTimeMillis() - start_time) + "ms");
	}

}
