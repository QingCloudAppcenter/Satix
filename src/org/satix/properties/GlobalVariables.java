package org.satix.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.satix.constants.Browsers;
import org.satix.constants.ReservedVariables;
import org.satix.utils.SatixLogger;

/**
 * This class is to cache the custom global variables
 * The global variables are defined in the configuration file input 
 * by the end users through command line. That is the file global.properties
 * in the following command.
 * satix.bat|sh -c <global.properties> -t <testcase> 
 * 
 * @author Ray
 *
 */
public class GlobalVariables {
	private static final String CLASSNAME = GlobalVariables.class.getSimpleName();
	private static Logger logger = SatixLogger.getLogger(CLASSNAME);

	private static Map<String, Object> globalProperties;

	private GlobalVariables() {
	}

	public static Map<String, Object> init(String propertiesFilePath) {
		if (null == globalProperties) {
			PropertiesLoader props = new PropertiesLoader();
			try {
				File f = new File(System.getProperty("user.dir")+"/" + 
							propertiesFilePath).getAbsoluteFile();
				globalProperties = props.load(new FileInputStream(f));
				
			    logger.log(Level.INFO, "Global properties[" + f.getAbsolutePath() + "] loaded.");
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "********" + e.getMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, "********" + e.getMessage());
			}
		}
		return globalProperties;
	}

	public static Map<String, Object> getConfigurations() {
		return globalProperties;
	}

	public static String getBrowserType() {
		String result = (String) globalProperties.get(ReservedVariables.BROWSER.getName());
		if (null == result) { 
			//if user does not specify the custom configuration file, then load default browser which is FireFox
			result = SystemConfigurations.getValue(ReservedVariables.BROWSER.getName(), Browsers.FF.getName());
		}
		return result;
	}
}
