package org.satix.actions;

import java.util.logging.Level;

import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.JavascriptExecutor;
import org.satix.utils.SatixLogger;

/**
 * The action defines how to maximize browser.
 *
 */
public class MaximizeBrowser extends Action{
	private static final String CLASSNAME = MaximizeBrowser.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	public void run(WebDriver driver) {
		long start_time = System.currentTimeMillis();
		driver.manage().window().maximize();		
		logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() 
			+ "\" number \"" + getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:" 
			+ (System.currentTimeMillis() - start_time) + "ms");
	}
	
}
