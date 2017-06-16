package org.satix.assertions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.satix.constants.MatchPattern;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action is used to check if the loaded page has the specified title or not. 
 *
 */
public class AssertTitle extends Assert {
	private static final String CLASSNAME = AssertTitle.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			WaitForWebElementToLoad(driver, getValue(), null);
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() 
				+ "\" number \"" + getNumber() + "\" line \"" + getLine() 
				+ "\" passed, elapsed time:" + (System.currentTimeMillis() - start_time) + "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, e);
		}
	}

	@Override
	public WebElement WaitForWebElementToLoad(WebDriver driver, String element, String elementBy) throws ActionException {

		if (null == element) {
			throw new ActionException(this);
		}

		WebElement webElement = null;
		int tries = 0;

		int matchingPattern = -1; // -1 is normal

		if (element.startsWith(MatchPattern.REGEXP.getName())) {
			element = element.substring(MatchPattern.REGEXP.getName().length());
			if (null == element) {
				throw new ActionException(this);
			}
			element = element.trim();
			matchingPattern = MatchPattern.REGEXP.getValue();
		} else if (element.startsWith(MatchPattern.GLOB.getName())) {
			element = element.substring(MatchPattern.GLOB.getName().length());
			if (null == element) {
				throw new ActionException(this);
			}
			element = element.trim();
			matchingPattern = MatchPattern.GLOB.getValue();
		} else if (element.startsWith(MatchPattern.EXACT.getName())) {
			element = element.substring(MatchPattern.EXACT.getName().length());
			if (null == element) {
				throw new ActionException(this);
			}
			element = element.trim();
			matchingPattern = MatchPattern.EXACT.getValue();
		}

		while (tries < MAX_TRIES) {
			String title = driver.getTitle();
			if (null == title)
				continue;

			switch (matchingPattern) {
			case -1: // contain
				if (title.contains(element))
					return webElement;
				break;
			case 0: // glob
				String str = convertGlobToRegEx(element);
				if (Pattern.compile(str).matcher(title).find())
				//if (title.matches(element))
					return webElement;
				break;
			case 1: // regular express
				if (Pattern.compile(element).matcher(title).find()) {
					return webElement;
				}
				break;
			case 2: // exact
				if (title.equals(element))
					return webElement;
				break;
			}

			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				throw new ActionException(this, e);
			}
			tries++;
		}

		if (tries >= MAX_TRIES) {
			throw new ActionException(this);
		}

		return webElement;
	}
	
	 private String convertGlobToRegEx(String str) {
	    int strLen = str.length();
	    StringBuilder sb = new StringBuilder(strLen);
	    if (str.startsWith("*")) { //remove the * at the begining since they are useless
	        str = str.substring(1);
	        strLen--;
	    }
	    if (str.endsWith("*")) {//remove the * at the end since they are useless
	        str = str.substring(0, strLen-1);
	        strLen--;
	    }
	    boolean escaping = false;
	    int inCurlies = 0;
	    for (char aChar : str.toCharArray()) {
	        switch (aChar) {
	        case '*':
	            if (escaping) {
	                sb.append("\\*");
	            }
	            else {
	                sb.append(".*");
	            }
	            escaping = false;
	            break;
	        case '?':
	            if (escaping) {
	                sb.append("\\?");
	            }
	            else {
	                sb.append('.');
	            }
	            escaping = false;
	            break;
	        case '.':
	        case '(':
	        case ')':
	        case '+':
	        case '|':
	        case '^':
	        case '$':
	        case '@':
	        case '%':
	            sb.append('\\');
	            sb.append(aChar);
	            escaping = false;
	            break;
	        case '\\':
	            if (escaping) {
	                sb.append("\\\\");
	                escaping = false;
	            }
	            else {
	                escaping = true;
	            }
	            break;
	        case '{':
	            if (escaping) {
	                sb.append("\\{");
	            }
	            else {
	                sb.append('(');
	                inCurlies++;
	            }
	            escaping = false;
	            break;
	        case '}':
	            if (inCurlies > 0 && !escaping) {
	                sb.append(')');
	                inCurlies--;
	            }
	            else if (escaping) {
	                sb.append("\\}");
	            }
	            else {
	                sb.append("}");
	            }
	            escaping = false;
	            break;
	        case ',':
	            if (inCurlies > 0 && !escaping) {
	                sb.append('|');
	            }
	            else if (escaping) {
	                sb.append("\\,");
	            }
	            else {
	                sb.append(",");
	            }
	            break;
	        default:
	            escaping = false;
	            sb.append(aChar);
	        }
	    }
	    return sb.toString();
	}
}
