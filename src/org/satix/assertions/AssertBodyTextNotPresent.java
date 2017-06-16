package org.satix.assertions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * 
 * @author huojiao
 *
 */

public class AssertBodyTextNotPresent extends Assert {
	private static final String CLASSNAME = AssertBodyTextNotPresent.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		try {
			bodyTextExist(driver, getValue(), null);
		} catch (ActionException e) {
			/*
			 * It is all right while this exception occurs, that means the
			 * BodyText can not be found. If it is all right for this try block,
			 * then we are sure the BodyText exist, and then we catch the screen
			 * shot and throw ActionException.
			 */
			logger.log(Level.INFO,
					getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() + "\" line \""
							+ getLine() + "\" passed, elapsed time:" + (System.currentTimeMillis() - start_time)
							+ "ms");
			return;
		}
		
		try {
			ScreenshotCapturer.saveScreenShot(driver, getName());
		} catch (IOException e1) {
			throw new ActionException(this, "Save screenshot error!", e1);
		}
		throw new ActionException(this,
				"Action error: name \"" + getName() + "\" number \"" + getNumber() + "\" line \"" + getLine()
						+ "\" column \"" + getColumn() + "\" Element \""
						+ (getElement() != null ? getElement() : getValue()) + "\" found.");
	}

	public void bodyTextExist(WebDriver driver, String element, String elementBy) throws ActionException {

		boolean exist = false;
		int tries = 0;

		while (tries < MAX_TRIES) {
			try {
				String source = driver.getPageSource();
				if (source != null && source.contains(element)) {
					exist = true;
					break;
				}

				try {
					Thread.sleep(WAIT_TIME);
				} catch (InterruptedException e) {
					throw new ActionException(this, e);
				}
				tries++;
			} catch (Exception e) {
				if (tries >= (MAX_TRIES - 1)) {
					throw new ActionException(this, e);
				}
				try {
					Thread.sleep(WAIT_TIME);
					tries++;
				} catch (InterruptedException e1) {
					throw new ActionException(this, "There is an error occured when waiting for find element.", e1);
				}
			}
		}
		if (!exist) {
			throw new ActionException(this);
		}
	}
}