package org.satix.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.BrowserDriver;
import org.satix.constants.Browsers;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * It defines the action how to open a browser.
 *
 */
public class OpenBrowser extends Action {
	private static final String CLASSNAME = CloseBrowser.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		BrowserDriver.openBrowser(null == getValue() ? Browsers.FF.getName() : getValue());
		logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
				+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
				+ (System.currentTimeMillis() - start_time) + "ms");
	}
	
	@Override
	public WebDriver getBrowserDriver() {
		return null;
	}
}
