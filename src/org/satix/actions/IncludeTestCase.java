package org.satix.actions;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.BrowserDriver;
import org.satix.TestCaseLoader;
import org.satix.beans.TestCase;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;

/**
 * This action is used to nest another test case in the current test case.
 *
 */
public class IncludeTestCase extends Action {
	private static final String CLASSNAME = IncludeTestCase.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private String path;
	private TestCase tc;

	@Override
	public void init(TestCase parentTc) throws ActionException {
		File file = new File(path);
		if (!file.exists()) {
			file = new File(parentTc.getTestCaseFilePath().substring(0, 
					parentTc.getTestCaseFilePath().lastIndexOf(System.getProperty("file.separator")) + 1) + path);
			if (!file.exists()) {
				logger.log(Level.SEVERE, "Can not find include test case file \"" + file.getAbsolutePath() + "\" in test case \"" 
						+ parentTc.getTestCaseFilePath() + "\".");
				System.exit(-1);
			}
		}
		logger.log(Level.INFO, "Include test case path: " + file.getAbsolutePath());
		TestCaseLoader tcLoader = new TestCaseLoader();
		tcLoader.setConfigurations(parentTc.getConfigurations());
		tc = tcLoader.loadTestCase(file.getAbsolutePath());
		tc.setParentTestCase(parentTc);
	}

	@Override
	public WebDriver getBrowserDriver() throws ActionException {
		WebDriver driver = BrowserDriver.getBrowserDriver();
		return driver;
	}

	@Override
	public void run(WebDriver driver) throws ActionException {
		tc.run();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
