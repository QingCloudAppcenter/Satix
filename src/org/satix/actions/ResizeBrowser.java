package org.satix.actions;

import java.util.logging.Level;

import java.util.logging.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.JavascriptExecutor;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * Resize your browser by this action.
 *
 */
public class ResizeBrowser extends Action{
	private static final String CLASSNAME = OpenWebPage.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private String width;
	private String height;

	public void run(WebDriver driver) throws ActionException {
		long start_time = System.currentTimeMillis();		
		int width = Integer.valueOf(getWidth());
		int height = Integer.valueOf(getHeight());
		if (width<=0 || height<=0) {
			throw new ActionException(this, getTestcase() + "Action: name \"" 
					+ getName() + "\" number \"" + getNumber() + "\" line \"" + getLine() +
					" You must define valid width and height.");
		}
		Dimension dim = new Dimension(width, height);
		driver.manage().window().setSize(dim);
		logger.log(Level.INFO, getTestcase() + "Action: name \"" 
			+ getName() + "\" number \"" + getNumber() + "\" line \"" + getLine() 
			+ "\" passed, elapsed time:" + (System.currentTimeMillis() - start_time) + "ms");
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
}
