package org.satix.assertions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * 
 * @author huojiao
 *
 */
public class AssertElementValue extends Assert {
	private static final String CLASSNAME = AssertElementValue.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();

			checkElementWithValueExist(driver);
			
			logger.log(Level.INFO,
					getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() + "\" line \""
							+ getLine() + "\" passed, elapsed time:" + (System.currentTimeMillis() - start_time)
							+ "ms");

		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, e);
		}
	}

	public void checkElementWithValueExist(WebDriver driver) throws ActionException {
		boolean exist = false;
		int tries = 0;

		while (tries < MAX_TRIES) {
			try {
				WebElement element = WaitForWebElementToLoad(driver, getElement(), getElementBy());

				String eleValue = element.getText().trim();
				String verifyEleValue = getValue().trim();

				if (eleValue.equals(verifyEleValue)) {
					exist = true;
					break;
				} else {
					logger.log(Level.SEVERE, "**********Errors:Satix tries the  " + (tries + 1) + " Time(s). ");
					logger.log(Level.SEVERE,
							"**********Errors:element value does not equal to validation value**********");
					logger.log(Level.SEVERE, "**********Errors:Element value is " + eleValue + " ,validation Value is "
							+ verifyEleValue);
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