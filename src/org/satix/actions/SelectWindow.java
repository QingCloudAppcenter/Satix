package org.satix.actions;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action is used to switch the focus to a popup window. 
 *
 */
public class SelectWindow extends Action {
	private static final String CLASSNAME = SelectWindow.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();

			String currentWindowHandle = driver.getWindowHandle();
			Set<String> openWindowsList = driver.getWindowHandles();
			logger.log(Level.INFO, "Open window count : " + openWindowsList.size());
			String popUpWindowHandle = null;
			for (String windowHandle : openWindowsList) { 
				if (!windowHandle.equals(currentWindowHandle)){ 
					popUpWindowHandle = windowHandle;
				}
			}
			driver.switchTo().window(popUpWindowHandle);
			
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() 
					+ "\" line \"" + getLine() + "\" passed, elapsed time:"
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
}
