package org.satix.assertions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;


import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This class defines a action to verify the Alert works, use it like this:
 * <action>
 * 		<name>assertAlert</name>
 * 		<value>ExpectResult</value>
 * </action>
 * 
 * @author Huanguo Zhong
 *
 */

public class AssertAlert extends Assert {
	private static final String CLASSNAME = AssertAlert.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			textExist(driver, getValue(), null);
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" 
					+ getNumber() + "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e);
			}
			throw new ActionException(this, e);
		}
	}
	
	/**
     * This is identical to the selenium.getAlert() method for
     * WebDriverBackedSelenium, in that it gets the text of the alert message
     * and it clicks the OK button to make it go away, except this one works.
     * @return String with the text of the alert message in it.
     */
	public String getAlert(WebDriver driver) {
        Alert alert = driver.switchTo().alert();
        String str = alert.getText();

        alert.accept();
        return str;
    }   
	
	/**
     * This is identical to the selenium.isAlertPresent() method for
     * WebDriverBackedSelenium, in that it checks to see if an alert message
     * is present, except this one works.  It uses WebDriver methods to check to
     * see if a JavaScript alert() message is on the screen.
     * @return This will return true is an alert message is on the screen.
     */
    public boolean isAlertPresent(WebDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e){
            return false;
        }  
    }
    
    public void textExist(WebDriver driver, String element, String elementBy) throws ActionException {
		boolean exist = false;
		int tries = 0;
		
		if (isAlertPresent(driver)) {			
			while (tries < MAX_TRIES) {
				try {
					String source = getAlert(driver);		
					if (source != null && source.contains(element)) {
						exist = true;
						break;
					}
	
					try {
						Thread.sleep(WAIT_TIME);
					} catch (InterruptedException e) {
						throw new ActionException(this, e);
					}
					tries++;
				} catch (Exception e) {
					if (tries >= (MAX_TRIES - 1)) {
						throw new ActionException(this, e);
					}
					try {
						Thread.sleep(WAIT_TIME);
						tries++;
					} catch (InterruptedException e1) {
						throw new ActionException(this, "There is an error occured when waiting for find element.", e1);
					}
				}
			}
			if(!exist){
				throw new ActionException(this);
			}
			
		} else {
			logger.log(Level.INFO, "The alert window was not presented");
		}
	}
}
