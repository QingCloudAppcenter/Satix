package org.satix;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
//import org.openqa.selenium.iphone.IPhoneDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.satix.constants.Browsers;
import org.satix.constants.ReservedVariables;
import org.satix.exceptions.ActionException;
import org.satix.properties.SystemConfigurations;
import org.satix.utils.SatixLogger;


/**
 * This class stores the browser driver instance
 * 
 * 
 */
public class BrowserDriver {
	private static final String CLASSNAME = BrowserDriver.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private static WebDriver driver;
	private static String platformInfo;

	private BrowserDriver() {
	}

	public static void openBrowser(String browser) throws ActionException {
		if (null == browser) {
			driver = null;
		} else {
			synchronized (BrowserDriver.class) {
				try {
					//Testers may need to differentiate the test cases for different IE versions, such as IE7, IE8,...
					if (browser.startsWith(Browsers.IE.getName())) { 
						
						//added by Jo@20170518 
						//to adapt to new version of Selenium
						String usrdir=System.getProperty("user.dir"); 
						String pathInConfig=SystemConfigurations.getValue(ReservedVariables.WEBDRIVERPATH.getName(), null);
						System.setProperty("webdriver.ie.driver",usrdir+pathInConfig );  
						 
						  DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
				          caps.setCapability("ignoreZoomSetting", true);
				          driver = new InternetExplorerDriver(caps);
						
//						 driver = new InternetExplorerDriver();
						
					} else if (Browsers.CHROME.getName().equals(browser)) {  
						//added by Jo@20170518 
						//to adapt to new version of Selenium
						String usrdir=System.getProperty("user.dir"); 
						String pathInConfig=SystemConfigurations.getValue(ReservedVariables.WEBDRIVERPATH.getName(), null);
					 	System.setProperty("webdriver.chrome.driver",usrdir+pathInConfig );  
						driver = new ChromeDriver();
						
					} else if (Browsers.FF.getName().equals(browser)) {
						
						//added by Jo@20170531 
						//to adapt to new version of Selenium
						String usrdir=System.getProperty("user.dir"); 
						String pathInConfig=SystemConfigurations.getValue(ReservedVariables.WEBDRIVERPATH.getName(), null);
					 	System.setProperty("webdriver.gecko.driver",usrdir+pathInConfig ); 
						
						
						String binaryPath = SystemConfigurations.getValue(ReservedVariables.FIREFOXBINARYPATH.getName(), null);
						String profilePath = SystemConfigurations.getValue(ReservedVariables.FIREFOXPROFILEPATH.getName(), null);
						if (null == binaryPath) {
							if (null == profilePath) {
								FirefoxProfile profile = new FirefoxProfile();
								profile.setEnableNativeEvents(true); 
								
								driver = new FirefoxDriver();
							} else {
								File file = new File(profilePath);
								if (file.exists()) {
									FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
									profile.setEnableNativeEvents(true);
									driver = new FirefoxDriver(profile);
								} else {
									logger.log(Level.INFO, "Firefox profile path is invalid, so open firefox in safe mode.");
									FirefoxProfile profile = new FirefoxProfile();
									profile.setEnableNativeEvents(true);
									driver = new FirefoxDriver();
								}
							}
						} else {
							FirefoxBinary binary = new FirefoxBinary(new File(binaryPath));
							FirefoxProfile profile = null;
							if(profilePath != null){
								profile = new FirefoxProfile(new File(profilePath));
							}
							driver = new FirefoxDriver(binary, profile);
						}
					} else if (Browsers.HTMLUNIT.getName().equals(browser)) {
						driver = new HtmlUnitDriver();
					} else if (Browsers.SAFARI.getName().equals(browser)) {
						driver = new SafariDriver();
					} /*else if (Browsers.IPHONE.getName().equals(browser)) {
						driver = new IPhoneDriver();
						// ((HtmlUnitDriver )driver).setJavascriptEnabled(false);
					} else if(Browsers.ANDROID.getName().equals(browser)){
						DesiredCapabilities caps = DesiredCapabilities.android();
						caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
						driver = new AndroidDriver(caps);
					} */ else { // use firefox by default
						 
//					    System.getProperty("Webdriver.gecko.driver", "C:/Workspace/tools/geckodriver-v0.16.1-win64/geckodriver.exe");
//						System.setProperty("webdriver.firefox.bin", "C:/Program Files (x86)/Mozilla Firefox/firefox.exe");  
						
						//added by Jo@20170531 
						//to adapt to new version of Selenium
						String usrdir=System.getProperty("user.dir"); 
						String pathInConfig=SystemConfigurations.getValue(ReservedVariables.WEBDRIVERPATH.getName(), null);
					 	System.setProperty("webdriver.gecko.driver",usrdir+pathInConfig ); 
						 
						
						driver = new FirefoxDriver();
					}
					platformInfo = ((JavascriptExecutor) driver).executeScript("var useragent = navigator.userAgent; return useragent;").toString();
					logger.log(Level.INFO, "Platform Information: " + platformInfo);
				} catch (Exception e) { 
					logger.log( Level.SEVERE,"Get errors: " +e.getLocalizedMessage());
					throw new ActionException("Satix can not create browser driver and can not open browser", e);
				}
			}
		}
	}

	public static WebDriver getBrowserDriver() {
		return driver;
	}

	public static String getPlatformInfo() {
		return platformInfo;
	}

	public static void quit() {
		if (driver != null) {
			driver.quit();
		}
	}
}