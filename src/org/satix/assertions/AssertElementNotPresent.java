package org.satix.assertions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action is used to check if the specified element does not exist in the page.
 *
 */
public class AssertElementNotPresent extends Assert {
	private static final String CLASSNAME = AssertElementNotPresent.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		try {
			WaitForWebElementToLoad(driver, getElement(), getElementBy());
		} catch (ActionException e) {
			/*
			 * It is all right while this exception occurs, that means the element can not be found.
			 * If it is all right for this try block, then we are sure the element exist, and then 
			 * we catch the screen shot and throw ActionException.
			 */
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
					+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
			return;
		}
		try {
			ScreenshotCapturer.saveScreenShot(driver, getName());
		} catch (IOException e1) {
			throw new ActionException(this, "Save screenshot error!", e1);
		}
		throw new ActionException(this, "Action error: name \"" + getName() + "\" number \"" 
				+ getNumber() + "\" line \"" + getLine() + "\" column \"" + getColumn()
				+ "\" Element \"" + (getElement() != null ? getElement() : getValue()) + "\" found.");
	}
}