package org.satix.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.satix.utils.SatixLogger;

/**
 * This class is to cache the system configurations such as browser, url, 
 * username, password, report, email, etc. 
 * 
 */
public class SystemConfigurations {
	private static final String CLASSNAME = SystemConfigurations.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private static Map<String, Object> configurations;

	private SystemConfigurations() {
	}

	public static Map<String, Object> getConfigurations() {
		if (null == configurations) {	
			PropertiesLoader props = new PropertiesLoader();
			try {
				File f = new File(System.getProperty("user.dir")+"/config", "satix.properties");
			 	configurations = props.load(new FileInputStream(f));   
				logger.log(Level.INFO, "Satix configuration properties[" + f.getAbsolutePath() + "] loaded.");
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "********" + e.getMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, "********" + e.getMessage());
			}			
		}

		return configurations;
	}

	public static String getValue(String key, String defaultValue){
		Object result = SystemConfigurations.getConfigurations().get(key);
		if(null==result || !(result instanceof String)){
			return defaultValue;
		}
		//updated by Jo@20170523 to trim the blank at the start and end
		//return (String)result;
		return ((String)result).trim();
	}
	
	public static String getFolderPath(String key, String defaultValue) {
		Object result = SystemConfigurations.getConfigurations().get(key);
		if (null==result || !(result instanceof String)) {
			File file = new File(defaultValue);
			if (!file.exists()) {
				file.mkdirs();
			}
			return file.getAbsolutePath() + File.separator;
		}
		if (((String)result).endsWith("/") || ((String)result).endsWith("\\")) {
			return (String)result;
		} else {
			return result + File.separator;
		}
	}
}
