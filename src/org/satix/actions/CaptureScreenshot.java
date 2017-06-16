package org.satix.actions;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.ScreenshotCapturer;


/**
 * This is an action that will save a screenshot to a file.
 * The action takes a screenName_ string as a parameter.
 * 
 * @author jack
 *
 */
public class CaptureScreenshot extends Action {	
	/**
	 * Internal variable storing the screenshot name.
	 */
	private String screenshotName;
	
	/**
	 * Set the screenshot name
	 * @param screenshotName
	 */
	public void setScreenshotName(String screenshotName) {
		this.screenshotName = screenshotName;
	}
	
	/**
	 * Gets the screenshot name
	 * @return - the screenshot name
	 */
	public String getScreenshotName() {
		return this.screenshotName;
	}
	
	/* (non-Javadoc)
	 * @see org.satix.actions.Action#run(org.openqa.selenium.WebDriver)
	 */
	@Override
	public void run(WebDriver driver) throws ActionException {
		//Just save a screen shot with the specified name.
		try {
			ScreenshotCapturer.saveScreenShot(getBrowserDriver(),
					getName()+"-" + screenshotName);
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			}
			catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, "Action: name \"" + getName()
					+ "\" number \"" + getNumber() + "\" line \"" + getLine()
					+ "\"" + " error", e);			
		}
	}
}
