package org.satix.actions;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action defines how to select a value in the drop-down menu.
 *
 */
public class SelectOption extends Action {
	private static final String CLASSNAME = SelectOption.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		String element = getElement();
		String elementBy = getElementBy();

		try {
			long start_time = System.currentTimeMillis();
			WebElement select = WaitForWebElementToLoad(driver, element, elementBy);
			List<WebElement> allOptions = select.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
				if (getValue().equals(option.getAttribute("value"))) {
					option.click();
					break;
				}
			}
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
