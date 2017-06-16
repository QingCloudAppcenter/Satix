package org.satix.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * This action is used to execute a group of actions within If action
 * and this action
 *
 */
public class EndIf extends Action{
	private static final String CLASSNAME = EndIf.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	
	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
				+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:" 
				+ (System.currentTimeMillis() - start_time) + "ms");
	}
}
