package org.satix.actions;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;
/**
 * 
 * @author huojiao
 *
 */
public class SwitchToNewWindow extends Action {
	private static final String CLASSNAME = SwitchToNewWindow.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			switchToNewWindow(driver);
			logger.log(Level.INFO,
					getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() + "\" line \""
							+ getLine() + "\" passed, elapsed time:" + (System.currentTimeMillis() - start_time)
							+ "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, e);
		}
	}

	public void switchToNewWindow(WebDriver driver) {
		String currentWindow = driver.getWindowHandle();
		// 得到所有窗口的句柄
		Set<String> handles = driver.getWindowHandles();

		// 排除当前窗口的句柄，则剩下是新窗口
		Iterator<String> it = handles.iterator();
		while (it.hasNext()) {
			if (currentWindow == it.next())
				continue;
			driver.switchTo().window(it.next());
		}

	}

}
