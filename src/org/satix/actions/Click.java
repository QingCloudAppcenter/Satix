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
 * This class defines the click action which clicks on an element either by id,
 * or name, or xpath, or css selector, or tag name.
 * 
 */
public class Click extends Action {
	private static final String CLASSNAME = Click.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		String element = getElement();
		String elementBy = getElementBy();

		try {
			long start_time = System.currentTimeMillis();

			
			//updated by Jo@20170601
			//Before click an element, firstly get its focus.
			//Otherwise, sometimes the issue that elements can not be found will happen.
			WebElement elementToClick = WaitForWebElementToLoad(driver, element, elementBy);			
			if ("input".equals(elementToClick.getTagName())) {
				elementToClick.sendKeys("");
			} else {
				new Actions(driver).moveToElement(elementToClick).perform();
			}
			
			elementToClick.click();

			// WaitForWebElementToLoad(driver, element, elementBy).click();

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
}
