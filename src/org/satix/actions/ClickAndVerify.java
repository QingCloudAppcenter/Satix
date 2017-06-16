package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action expects some new element appears after clicking a specific 
 * element. If the new element doesn¡¯t appear, then try to click the specific
 * element again. This action is helpful in some special situation, for example,
 * clicking a collapsed tree. Sometimes, even the label of the tree is clicked,
 * the tree is not expanded. So in this case, we have to click the label again 
 * until the tree is expanded.
 * 
 * @author Ray
 *
 */
public class ClickAndVerify extends Action {
	private static final String CLASSNAME = ClickAndVerify.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private final int COUNT = 5; // try five times
	
	private String verifyElement;
	private String verifyElementBy;
	
	public void setVerifyElement(String verifyElement) {
		this.verifyElement = verifyElement;
	}

	public String getVerifyElement() {
		return verifyElement;
	}

	public void setVerifyElementBy(String verifyElementBy) {
		this.verifyElementBy = verifyElementBy;
	}

	public String getVerifyElementBy() {
		return verifyElementBy;
	}

	@Override
	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();
		int tries = 0;
		for (tries = 0; tries <= COUNT; tries++) {
			try {
				WaitForWebElementToLoad(driver, getElement(), getElementBy()).click();
				try {
					WaitForWebElementToLoad(driver, getVerifyElement(), getVerifyElementBy()).isDisplayed();
					logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() 
							+ "\" number \"" + getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:" 
							+ (System.currentTimeMillis() - start_time) + "ms");
				} catch (Exception e) {
					throw new ActionException(this, "Action error: name \"" + getName() + "\" number \"" + getNumber() 
							+ "\" line \"" + getLine() + "\" column \"" + getColumn() + "\" Element \"" + getVerifyElement()
							+ " not found");
				}
				break;
			} catch (Exception e) {
				if (tries == COUNT) {
					try {
						ScreenshotCapturer.saveScreenShot(driver, getName());
					} catch (IOException e1) {
						throw new ActionException(this, "Save screenshot error!", e1);
					}
					if (e instanceof ActionException) {
						throw (ActionException)e;
					} else {
						throw new ActionException(this, e);
					}
				} else {
					continue;
				}
			}
		}
	}

}



