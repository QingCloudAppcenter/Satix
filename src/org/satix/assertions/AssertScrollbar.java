package org.satix.assertions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.satix.actions.Action;
import org.satix.constants.Browsers;
import org.satix.constants.ElementBy;
import org.satix.exceptions.ActionException;
import org.satix.properties.GlobalVariables;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * Thid action is used to verify if scrollbar of the specified element 
 * exists or not.
 *
 */
public class AssertScrollbar extends Action {
	private static final String CLASSNAME = AssertScrollbar.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private String condition;

	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			StringBuilder js = new StringBuilder();
			String browser = GlobalVariables.getBrowserType();
			if (ElementBy.ID.getName().equals(this.getElementBy())) {
				js.append("var b = document.getElementById(\"" + getElement() + "\");");
			} else if (ElementBy.XPATH.getName().equals(this.getElementBy())) {
				if (!Browsers.IE.getName().equals(browser)) {
					js.append("var b = document.evaluate(\"" + getElement()
							+ "\", document, null, XPathResult.ANY_TYPE, null).iterateNext();");
				} else {
					throw new Exception("Action error: name \""	+ getName() + "\" number \"" 
							+ getNumber() + "\" line \"" + getLine() + "\" column \"" + getColumn() 
							+ "\" xpath is not supported in assertScrollBarAction");
				}
			} else if (ElementBy.CSSSELECTOR.getName().equals(getElementBy())) {
				js.append("var b = document.querySelector(\"" + getElement() + "\");");
			} else {
				throw new Exception("Action error: name \"" + getName() + "\" number \"" 
						+ getNumber() + "\" line \"" + getLine() + "\" column \""
						+ getColumn() + "\" Element by error");
			}
			String getWidthHeightScript = js.toString()
					+ "var o = new Array(); o.push(b.clientHeight); o.push(b.scrollHeight); o.push(b.clientWidth); o.push(b.scrollWidth); return o;";
			condition = condition.trim();
			js.append("return ");
			try {
				if (condition.contains("and")) {
					String[] vh = condition.split("and");
					if (vh[0].trim().contains("!v")) {
						js.append("b.clientHeight == b.scrollHeight");
					} else {
						js.append("b.clientHeight != b.scrollHeight");
					}
					js.append(" && ");
					if (vh[1].trim().contains("!h")) {
						js.append("b.clientWidth == b.scrollWidth");
					} else {
						js.append("b.clientWidth != b.scrollWidth");
					}
				} else if (condition.contains("or")) {
					String[] vh = condition.split("or");
					if (vh[0].trim().contains("!v")) {
						js.append("b.clientHeight == b.scrollHeight");
					} else {
						js.append("b.clientHeight != b.scrollHeight");
					}
					js.append(" || ");
					if (vh[1].trim().contains("!h")) {
						js.append("b.clientWidth == b.scrollWidth");
					} else {
						js.append("b.clientWidth != b.scrollWidth");
					}
				} else if (condition.contains("v")) {
					if (condition.contains("!v")) {
						js.append("b.clientHeight == b.scrollHeight");
					} else {
						js.append("b.clientHeight != b.scrollHeight");
					}
				} else if (condition.contains("h")) {
					if (condition.contains("!h")) {
						js.append("b.clientWidth == b.scrollWidth");
					} else {
						js.append("b.clientWidth != b.scrollWidth");
					}
				}
				js.append(";");
			} catch (Exception e) {
				throw new Exception("Action error: name \"" + getName() + "\" number \"" 
						+ getNumber() + "\" line \"" + getLine() + "\" column \"" 
						+ getColumn() + "\" condition error in assertScrollbarAction");
			}
			try {
				logger.log(Level.INFO, "Execute script : " + js.toString());
				Object o = ((JavascriptExecutor) driver).executeScript(js
						.toString());
				logger.log(Level.INFO, "Result : " + o.toString());

				logger.log(Level.INFO, "Execute script : "
						+ getWidthHeightScript);
				Object wh = ((JavascriptExecutor) driver)
						.executeScript(getWidthHeightScript);
				logger.log(Level.INFO, "Result : " + wh.toString());
			} catch (Exception e) {
				throw new Exception("Action error: name \"" + getName() + "\" number \"" 
						+ getNumber() + "\" line \"" + getLine() + "\" column \""
						+ getColumn() + "\" javascript execute error : "
						+ e.getMessage() + ", javascript : " + js.toString());
			}

			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName()
							+ "\" number \"" + getNumber() + "\" line \""
							+ getLine() + "\" passed, elapsed time:"
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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
