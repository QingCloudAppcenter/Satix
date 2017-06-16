package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * Sometimes the web page includes frames or iframes. We have to switch into the
 * frame or iframe we are interested in first, then snatch the web elements we 
 * want. This is extremely important; otherwise you will not get the result you 
 * expect. The value could be either frame/iframe id or frame/iframe name.
 *
 */
public class SwitchFrame extends Action {
	private static final String CLASSNAME = SwitchFrame.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private String relative;

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			SwitchToFrame(driver, getValue(), relative);
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
					+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, e);
		}
	}

	/*
	 * SwitchFrame will support RC mode, so do not know how to support "relative=up", so abandon this method
	 */
	public void SwitchToFrameNew(WebDriver driver, String value, String relative) throws ActionException {
		try {
			if (relative == null || relative.equalsIgnoreCase("top")) {
				driver.switchTo().defaultContent();
			}
			ExpectedCondition<WebDriver> condition = ExpectedConditions.frameToBeAvailableAndSwitchToIt(value);
			(new WebDriverWait(driver, 20)).until(condition);
		} catch (Exception e) {
			throw new ActionException(this, e);
		}
	}

	public void SwitchToFrame(WebDriver driver, String value, String relative) throws ActionException {

		int tries = 0;
		while (tries < MAX_TRIES) {
			try {
				if (relative == null || relative.equalsIgnoreCase("top")) {
					driver.switchTo().defaultContent();
				} else if (!relative.equalsIgnoreCase("child")) {
					throw new ActionException(this, "Action error: name \"" + getName() + "\" number \"" 
							+ getNumber() + "\" line \"" + getLine() + "\" column \"" + getColumn()
							+ "\" Relative value is not correct for switchFrame action!");
				}
				int index = -1;
				try {
					index = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					index = -1;
				}
				if (getElement() == null) {
					if(value != null){
						if (index == -1) {
							driver.switchTo().frame(value);
						} else {
							driver.switchTo().frame(index);
						}
					}
				} else {
					WebElement ele = WaitForWebElementToLoad(driver, getElement(), getElementBy());
					driver.switchTo().frame(ele);
				}
				break;
			} catch (NoSuchFrameException e) {
				if (tries == (MAX_TRIES - 1)) {
					throw new ActionException(this, e);
				}
				try {
					Thread.sleep(WAIT_TIME);
					tries++;
				} catch (InterruptedException e1) {
					throw new ActionException(this, "There is an error occured when waiting for find element.", e1);
				}
			} catch (Exception e) {
				if (tries == (MAX_TRIES - 1)) {
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
	}

	public String getRelative() {
		return relative;
	}

	public void setRelative(String relative) {
		this.relative = relative;
	}
}
