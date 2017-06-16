package org.satix.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.Cache;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * The clear cache is used to clear stored variables. The stored variables are
 * either from configuration file that is defined by <configFile> element in 
 * test case file, or from a storeElement action.
 * 
 *
 */
public class ClearCache  extends Action {
	
	private static final String CLASSNAME = ClearCache.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	
	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		Cache.clearTestCaseVars();
		logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
				+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
				+ (System.currentTimeMillis() - start_time) + "ms");
	}
	
}
