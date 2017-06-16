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
 * This action is used to drag and drop a draggable element in the web page.
 * This action works well in IE but not in Firefox
 * 
 */

public class DragAndDrop extends Action {
	private static final String CLASSNAME = DragAndDrop.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private String xDistance;
	private String yDistance;

	@Override
	public void run(WebDriver driver) throws ActionException {

		String element = getElement();
		String elementBy = getElementBy();
		String xDistance = getXDistance();
		String yDistance = getYDistance();

		try {
			long start_time = System.currentTimeMillis();

			int x = Integer.parseInt(xDistance);
			int y = Integer.parseInt(yDistance);
			WebElement source = WaitForWebElementToLoad(driver, element, elementBy);
			Actions builder = new Actions(driver);
			builder.dragAndDropBy(source, x, y).build().perform();

			long end_time = System.currentTimeMillis();
			long elapsed_time = end_time - start_time;
			logger.log(Level.INFO, getTestcase() + "Action #" + getNumber() + " \"" + getName() + "\", Element \"" 
					+ element + "\" and X \"" + xDistance + "\"  and Y \"" + yDistance 
					+ "\" passed, elapsed time:" + elapsed_time + "ms");
		} catch (ActionException e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, e);
		}
	}

	public String getXDistance() {
		return xDistance;
	}

	public void setXDistance(String xDistance) {
		this.xDistance = xDistance;
	}

	public String getYDistance() {
		return yDistance;
	}

	public void setYDistance(String yDistance) {
		this.yDistance = yDistance;
	}
}
