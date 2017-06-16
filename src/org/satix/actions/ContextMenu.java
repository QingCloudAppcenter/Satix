package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Action;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;


/**
 * This class defines the right click action.
 * 
 */
public class ContextMenu extends org.satix.actions.Action {
	private static final String CLASSNAME = ContextMenu.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		String element = getElement();
		String elementBy = getElementBy();

		try {
			long start_time = System.currentTimeMillis();
			
			WebElement source = WaitForWebElementToLoad(driver, element, elementBy);
			Actions builder = new Actions(driver);
			builder.contextClick(source);
			Action rightClick = builder.build();
			rightClick.perform();
			
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() 
				+ "\" number \"" + getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
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
