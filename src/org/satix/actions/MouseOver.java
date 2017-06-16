package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;


/**
 * This class defines the mouseOver action which mouses overan element 
 * either by id, or name, or xpath, or css selector, or tag name.
 * 
 */

public class MouseOver extends Action {
	private static final String CLASSNAME = MouseOver.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	
	@Override
	public void run(WebDriver driver) throws ActionException {
		String element = getElement();
		String elementBy = getElementBy();

		try {
			long start_time = System.currentTimeMillis();			
			
			WebElement toElement = WaitForWebElementToLoad(driver, element, elementBy);
			Actions builder = new Actions(driver);
			builder.moveToElement(toElement).perform();
					
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
}
