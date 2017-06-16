package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * The action types text into an input text box.
 *
 */
public class InputText extends Action {
	private static final String CLASSNAME = InputText.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		WebElement webElement = null;
		String element = getElement();
		String elementBy = getElementBy();

		try {
			long start_time = System.currentTimeMillis();
			webElement = WaitForWebElementToLoad(driver, element, elementBy);
			webElement.clear();
			webElement.sendKeys(getValue().replaceAll("\\\\n", System.getProperty("line.separator")));
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