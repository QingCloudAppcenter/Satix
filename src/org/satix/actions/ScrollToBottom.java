package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.satix.constants.Browsers;
import org.satix.constants.ElementBy;
import org.satix.exceptions.ActionException;
import org.satix.properties.GlobalVariables;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;
/**
 * This class defines the scrollToBottom action which moves scrollBar on 
 * an element to bottom either by id, or xpath, or css selector.
 * 
 * @author Qi En Jiang
 * 
 */
public class ScrollToBottom extends Action{
	private static final String CLASSNAME = ScrollToBottom.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private int interval = 1000;

	public void run(WebDriver driver) throws ActionException {
		try{
			long start_time = System.currentTimeMillis();			
			StringBuilder js = new StringBuilder();
			String browser = GlobalVariables.getBrowserType();			

			if (ElementBy.ID.getName().equals(getElementBy())) {
				js.append("var b = document.getElementById(\"" + getElement() + "\");");
			} else if (ElementBy.XPATH.getName().equals(getElementBy())) {
				if (!browser.contains(Browsers.IE.getName())) {
					js.append("var b = document.evaluate(\"" + getElement()
							+ "\", document, null, XPathResult.ANY_TYPE, null).iterateNext();");
				} else {
					throw new ActionException(this, "Action error: name \""	+ getName() + "\" number \"" + getNumber() 
							+ "\" line \""	+ getLine() + "\" column \"" + getColumn() + "\" xpath is not supported in scrollToBottom action in IE");
				}
			} else if (ElementBy.CSSSELECTOR.getName().equals(getElementBy())) {
				js.append("var b = document.querySelector(\"" + getElement() + "\");");
			} else {
				throw new ActionException(this, "Action error: name \"" + getName() + "\" number \"" + getNumber()
						+ "\" line \"" + getLine() + "\" column \"" + getColumn() + "\" Element by error");
			}
			
			String getScrollHeightScript = js.toString()+ "var o = new Array(); o.push(b.scrollHeight); return o;";
						
	        js.append("b.scrollTop = b.scrollTop + b.clientHeight;");
	        js.append("var tmp = b.scrollTop + b.clientHeight;");
	        js.append("var o = new Array(); o.push(tmp); return o;");
	        
	        int tries = 1;
	        String scrollTop = "0";
	        logger.log(Level.INFO, "Execute script : " + js.toString());
	        while (tries > 0) {
	        	try {				
					String scrollHeight = ((JavascriptExecutor)driver).executeScript(getScrollHeightScript).toString();			
					if (scrollTop.equals(scrollHeight)) {
						break;
					}
					Object o = ((JavascriptExecutor)driver).executeScript(js.toString());
					scrollTop = o.toString();
					Thread.sleep(this.getInterval());
					tries ++;
				} catch(Exception e) {
					throw new ActionException(this, "Action error: name \"" + getName() + "\" number \"" + getNumber() 
							+ "\" line \"" + getLine() + "\" column \"" + getColumn()
							+ "\" javascript execute error : " + e.getMessage() + ", javascript : " + js.toString());
					}		
	        }	        
	        logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() 
					+ "\" number \"" + getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:" 
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			if ( e instanceof ActionException) {
				throw (ActionException)e;
			} else {
				throw new ActionException(this, e.getMessage(), e);
			}
		}
	}
	
	public int getInterval() {
		return interval;
	}
}