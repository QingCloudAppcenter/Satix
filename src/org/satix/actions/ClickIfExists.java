package org.satix.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * This action defines a click behavior on a web element, such as a button, 
 * a link, etc. Click the element when it exists, and do nothing when it 
 * does not exist.
 * 
 * @author Ray
 *
 */
public class ClickIfExists extends Action {
	private static final String CLASSNAME = ClickIfExists.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {

		String element = getElement();
		String elementBy = getElementBy();
		long start_time = System.currentTimeMillis();
		try {
			WaitForWebElementToLoad(driver, element, elementBy).click();
		} catch (Exception e) {
			// This element does not exist, then skip it
		} finally {
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
					+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		}
	}
}
