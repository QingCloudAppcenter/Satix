package org.satix.assertions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * The assert body text action checks if the specified text will show in the 
 * HTML source code or not which can check if a page is loaded or not.
 *
 */
public class AssertBodyText extends Assert {
	private static final String CLASSNAME = AssertBodyText.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			bodyTextExist(driver, getValue(), null);
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() 
					+ "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e);
			}
			throw new ActionException(this, e);
		}
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
		if(!exist){
			throw new ActionException(this);
		}
	}
}
