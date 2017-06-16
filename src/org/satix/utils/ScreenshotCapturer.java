package org.satix.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.satix.constants.ReservedVariables;
import org.satix.properties.SystemConfigurations;


/**
 * a screenshot capturer for exception
 *
 */
public class ScreenshotCapturer {
	private static final String CLASSNAME = ScreenshotCapturer.class.getSimpleName();
	private static final Logger logger = Logger.getLogger(CLASSNAME);
	private static String caseName = "";
	
	public static void saveScreenShot(WebDriver driver, String actionName) throws IOException {
		if (driver instanceof HtmlUnitDriver) {
			return; // HtmlUnitDriver has no GUI, so no screenshot captured needed
		}
		
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
		Date date = new Date();
		
		StringBuilder filename = new StringBuilder(caseName+"-"+actionName);
		filename.append("-");
		filename.append(sdf.format(date));
		filename.append(".png");
		
		String screenshotPath = SystemConfigurations.getFolderPath(
				ReservedVariables.SCREENSHOTPATH.getName(), 
				"." + File.separator + "screenshots");
		File destFilePath=new File(screenshotPath + filename.toString().replace("\"", ""));
		String saveLevel = SystemConfigurations.getValue(
				ReservedVariables.SCREENSHOTLEVEL.getName(), "global");
		if ("testcase".equals(saveLevel)) {
			screenshotPath += File.separator + caseName;
		}
		else if ("action".equals(saveLevel)) {
			screenshotPath += File.separator + caseName + File.separator + actionName;
		}
		
		if (!(screenshotPath.endsWith("/")) && !(screenshotPath.endsWith("\\"))) {
			screenshotPath += File.separator;
		}
		
		FileUtils.copyFile(scrFile, destFilePath);		
	}
	
	public static void setName(String name) {
		caseName = name;
	}
	
	public static void main(String[] args) {
		ScreenshotCapturer.setName("catalog");
		WebDriver driver = new FirefoxDriver();
		driver.get("http://www.google.com");
		try {
			ScreenshotCapturer.saveScreenShot(driver,"ActionError");
			logger.log(Level.INFO, "Screeenshot captured");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
