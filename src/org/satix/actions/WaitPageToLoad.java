package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.BrowserDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action waits a fixed time between the immediate previous action and the
 * immediate following action. We do not recommend using this action since it 
 * affects performance. However, if you do want to use it, or you have to use 
 * it in some situation, you are free to add this action in your test cases.
 * 
 * @author Ray
 *
 */
public class WaitPageToLoad extends Action {
	private static final String CLASSNAME = WaitPageToLoad.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private String elapse;

	@Override
	public WebDriver getBrowserDriver() throws ActionException {
		WebDriver driver = BrowserDriver.getBrowserDriver();
		return driver;
	}

	@Override
	public void run(WebDriver driver) throws ActionException {
		int value = Integer.valueOf(getElapse()).intValue();
		try {
			long start_time = System.currentTimeMillis();
			Thread.sleep(value);
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
					+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (InterruptedException e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, "Wait page to load exception.", e);
		}
	}

	public void setElapse(String elapse) {
		this.elapse = elapse;
	}

	public String getElapse() {
		return elapse;
	}
}
