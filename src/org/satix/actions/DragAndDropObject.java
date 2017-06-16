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
 * This action is used to drag and drop a draggable element to the destination 
 * element in the web page. It works well in IE but not in firefox
 * 
 * @author Yuanyuan Wu
 * 
 */

public class DragAndDropObject extends Action {
	private static final String CLASSNAME = DragAndDrop.class.getName();
	private static Logger logger = SatixLogger.getLogger(CLASSNAME);
	private String destElement;
	private String destElementBy;

	@Override
	public void run(WebDriver driver) throws ActionException {

		String srcElement = getElement();
		String srcElementBy = getElementBy();
		destElement = getDestElement();
		destElementBy = getDestElementBy();

		try {
			long start_time = System.currentTimeMillis();
		
			WebElement source = WaitForWebElementToLoad(driver, srcElement, srcElementBy);
			WebElement dest = WaitForWebElementToLoad(driver, destElement, destElementBy);
			Actions builder = new Actions(driver);
			builder.dragAndDrop(source, dest).build().perform();
			//builder.dragAndDropBy(source, x, y).build().perform();

			long end_time = System.currentTimeMillis();
			long elapsed_time = end_time - start_time;
			logger.log(Level.INFO, "Action #" + getNumber() + " \"" + getName() + "\", Element \"" + srcElement + "\" and destElement \"" + destElement + "\" passed, elapsed time:"
					+ elapsed_time + "ms");
		} catch (ActionException e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, e);
		}

		// RenderedWebElement fromElement = (RenderedWebElement )WaitForWebElementToLoad(driver, element, elementBy);
		// RenderedWebElement toElement = (RenderedWebElement)WaitForWebElementToLoad(driver, targetElement, targetElementBy);

		// fromElement.dragAndDropOn(toElement);

		// fromElement.dragAndDropBy(15,-140);//(int)(toElement.getLocation().getX() - fromElement.getLocation().getX()),(int)(toElement.getLocation().getY()

	}

	public String getDestElement() {
		return destElement;
	}

	public void setDestElement(String destElement) {
		this.destElement = destElement;
	}

	public String getDestElementBy() {
		return destElementBy;
	}

	public void setDestElementBy(String destElementBy) {
		this.destElementBy = destElementBy;
	}

	/*
	 * private void moveWidget(WebDriver driver, FreeformContainer widget, int xOffset, int yOffset) { Frame.MAIN.doSwitch(driver); WebElement widgetFrame = driver.findElement(By.id(widget .getId()));
	 * System.out.println("XOFF:" + xOffset + " YOFF:" + yOffset + " LEFT:" + Integer.parseInt(widgetFrame.getAttribute("offsetLeft")) + " TOP:" +
	 * Integer.parseInt(widgetFrame.getAttribute("offsetTop"))); int moveRightBy = xOffset - Integer.parseInt(widgetFrame.getAttribute("offsetLeft")); int moveDownBy = yOffset -
	 * Integer.parseInt(widgetFrame.getAttribute("offsetTop")); System.out.println("MR:" + moveRightBy + " MD:" + moveDownBy); widget.getTitle().dragAndDropBy(moveRightBy, moveDownBy); while
	 * (Integer.parseInt(widgetFrame.getAttribute("offsetTop")) < yOffset) widgetFrame.sendKeys(Keys.ARROW_DOWN); // ActionChainsGenerator //
	 * builder=((HasInputDevices)browser.driver).actionsBuilder(); // Action // drag =builder.clickAndHold(widget.getTitle()).moveByOffset(moveRightBy,moveDownBy ).build(); // drag.perform();
	 * 
	 * // widgetFrame=content.findElement(By.xpath("div[last()]")); System.out.println("22XOFF:" + xOffset + " YOFF:" + yOffset + " LEFT:" + Integer.parseInt(widgetFrame.getAttribute("offsetLeft")) +
	 * " TOP:" + Integer.parseInt(widgetFrame.getAttribute("offsetTop"))); }
	 */

}
