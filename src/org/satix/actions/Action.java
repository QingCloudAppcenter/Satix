package org.satix.actions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.satix.BrowserDriver;
import org.satix.Cache;
import org.satix.beans.TestCase;
import org.satix.constants.ElementBy;
import org.satix.constants.ReservedVariables;
import org.satix.exceptions.ActionException;
import org.satix.properties.SystemConfigurations;
import org.satix.properties.GlobalVariables;
import org.satix.utils.SatixLogger;
import org.satix.utils.StringParser;

/**
 * The class defines the common fields and methods of an action
 * 
 * 
 */
public abstract class Action {
	private static final String CLASSNAME = Action.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	protected static int MAX_TIMESPAN = 20;
	protected int MAX_TRIES = 1;
	protected static int WAIT_TIME = 1000;

	private int number; // action number in this test case
	private String line; // the line number that the action name resides
	private String column; // the column number that the action name resides
	private String name;
	private String element;
	private String elementBy;
	private String value;

	private boolean containsVariables;
	private TestCase testcase;
	private static int waitElementTime = MAX_TIMESPAN;
	private boolean ignore = false;
	
	static {
		String sWaitElementTime = SystemConfigurations.getValue(ReservedVariables.WAITELEMENTTIME.getName(), String.valueOf(MAX_TIMESPAN));
		try {
			waitElementTime = Integer.valueOf(sWaitElementTime);
		} catch (Exception e) {
			waitElementTime = MAX_TIMESPAN;
		}
	}

	public void init(TestCase testcase) throws ActionException {
		setTestcase(testcase);
	}

	public WebDriver getBrowserDriver() throws ActionException {
		boolean openBrowser = false;
		WebDriver driver = BrowserDriver.getBrowserDriver();
		if (null == driver) {
			openBrowser = true;
		}
		if (driver instanceof InternetExplorerDriver || driver instanceof FirefoxDriver || driver instanceof ChromeDriver) {
			if (null == ((RemoteWebDriver) driver).getSessionId()) {
				openBrowser = true;
			}
		}
		if (openBrowser) {
			synchronized (TestCase.class) {
				if (openBrowser) {
					String browser = GlobalVariables.getBrowserType();
					BrowserDriver.openBrowser(browser);
					logger.log(Level.INFO, "Open browser.");
					driver = BrowserDriver.getBrowserDriver();
				}
			}
		}
		return driver;
	}

	public void runWithCondition(WebDriver driver) throws ActionException{
		if(!ignore){
			run(driver);
		}
	}
	
	public abstract void run(WebDriver driver) throws ActionException;

	public WebElement WaitForWebElementToLoad(WebDriver driver, String element, String elementBy) throws ActionException {
		String waitingTime = SystemConfigurations.getValue(ReservedVariables.WAITINGONACTION.getName(), "0"); 
		int intWaitingTime = Integer.valueOf(waitingTime).intValue(); 
		if (intWaitingTime <= 0) {
			return WaitForWebElementToLoad(driver, element, elementBy, 1);
		} else {
			MAX_TRIES = (intWaitingTime * 1000) / WAIT_TIME;
			return WaitForWebElementToLoad(driver, element, elementBy, MAX_TRIES);
		}
	}	

	public WebElement WaitForWebElementToLoad(WebDriver driver, String element, String elementBy, int maxTries) throws ActionException {
		WebElement webElement = null;
		By by = null;
		int tries = 0;

		while (tries < maxTries) {
			try {
				if (ElementBy.ID.getName().equals(elementBy)) {
					by = By.id(element);
				} else if (ElementBy.NAME.getName().equals(elementBy)) {
					by = By.name(element);
				} else if (ElementBy.XPATH.getName().equals(elementBy)) {
					by = By.xpath(element);
				} else if (ElementBy.LINKTEXT.getName().equals(elementBy)) {
					System.out.println("element: " + element);
					by = By.linkText(element);
				} else if (ElementBy.PARTIALLINKTEXT.getName().equals(elementBy)) {
					by = By.partialLinkText(element);
				} else if (ElementBy.CLASSNAME.getName().equals(elementBy)) {
					by = By.className(element);
				} else if (ElementBy.CSSSELECTOR.getName().equals(elementBy)) {
					by = By.cssSelector(element);
				} else if (ElementBy.TAGNAME.getName().equals(elementBy)) {
					by = By.tagName(element);
				} else {
					throw new ActionException(this, "Action error: name \"" + getName() + "\" number \"" 
							+ getNumber() + "\" line \"" + getLine() + "\" column \"" + getColumn()
							+ "\" ElementBy " + elementBy + " is invalid.");
				}
				ExpectedCondition<WebElement> condition = ExpectedConditions.visibilityOfElementLocated(by);
				webElement = (new WebDriverWait(driver, waitElementTime)).until(condition);
				return webElement;
			} catch (Exception ex){				
				// in case element is hidden or disabled
				try {
					if (ElementBy.ID.getName().equals(elementBy)) {
						webElement = driver.findElement(By.id(element));
					} else if (ElementBy.NAME.getName().equals(elementBy)) {
						webElement = driver.findElement(By.name(element));
					} else if (ElementBy.XPATH.getName().equals(elementBy)) {
						webElement = driver.findElement(By.xpath(element));
					} else if (ElementBy.LINKTEXT.getName().equals(elementBy)) {
						webElement = driver.findElement(By.linkText(element));
					} else if (ElementBy.PARTIALLINKTEXT.getName().equals(elementBy)) {
						webElement = driver.findElement(By.partialLinkText(element));
					} else if (ElementBy.CLASSNAME.getName().equals(elementBy)) {
						webElement = driver.findElement(By.className(element));
					} else if (ElementBy.CSSSELECTOR.getName().equals(elementBy)) {
						webElement = driver.findElement(By.cssSelector(element));
					} else if (ElementBy.TAGNAME.getName().equals(elementBy)) {
						webElement = driver.findElement(By.tagName(element));
					} else {
						throw new ActionException(this, ex);
					}
					return webElement;
				} catch (Exception e) {
					if (tries == (maxTries - 1)) {
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
		}
		return webElement;
	}	

	public void updateVariableValues() throws ActionException {
		if (isContainsVariables()) {		
			Method[] methods = getClass().getMethods();

			// store the setter first for fast retrieve
			HashMap<String, Method> setters = new HashMap<String, Method>();

			for (Method aMethod : methods) {
				String methodName = aMethod.getName();
				if (methodName.startsWith("set")) {
					setters.put(methodName, aMethod);
				}
			}

			for (Method aMethod : methods) {
				String methodName = aMethod.getName();
				if (methodName.startsWith("get")) {
					try {
						Object obj = aMethod.invoke(this);
						if (obj instanceof String) {
							String value = (String) obj;
							if (StringParser.containsVars(value)) {
								String parsedValue = StringParser.parseString(value, Cache.getTestCaseCache());
								String setMethod = "set" + methodName.substring(3);
								Method aMthd = setters.get(setMethod);
								aMthd.invoke(this, parsedValue);
							}
						}
					} catch (Exception e) {
						throw new ActionException(this, e);
					}
				}
			}
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getElement() {
		return element;
	}

	public void setElementBy(String elementBy) {
		this.elementBy = elementBy;
	}

	public String getElementBy() {
		return elementBy;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getLine() {
		return line;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getColumn() {
		return column;
	}

	public void setContainsVariables(boolean containsVariables) {
		this.containsVariables = containsVariables;
	}

	public boolean isContainsVariables() {
		return containsVariables;
	}

	public TestCase getTestcase() {
		return testcase;
	}

	public void setTestcase(TestCase testcase) {
		this.testcase = testcase;
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}	
}
