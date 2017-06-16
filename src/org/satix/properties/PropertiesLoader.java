package org.satix.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * A utility to load key-value from a properties file which is read into an
 * InputStream. The value could be a JSON object or a variable.
 * 
 * @author Ray
 *
 */
public class PropertiesLoader {
	/**
	 * Loads name/value pairs from the given input stream.
	 */
	public Map<String, Object> load(InputStream is) throws IOException {
		Map<String, Object> properties = new HashMap<String, Object>();
		Properties props = new Properties();
		//updated by Jo@20170524 to fix Chinese messy code when load data from configuration files. 
		InputStreamReader isr = new InputStreamReader(is, "UTF-8"); 
		props.load(isr);

		Enumeration<?> e = (Enumeration<?>) props.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = props.getProperty(key);
			if (key != null && value != null) {
				if (value.indexOf('[') == 0 && value.lastIndexOf(']') == (value.length() - 1)) {
					properties.put(key, JSONArray.parse(value));
				}
				if (value.indexOf('{') == 0 && value.lastIndexOf('}') == (value.length() - 1)) {
					properties.put(key, JSONObject.parse(value));
				} else {
					// expand the the value before storing it in the map.
					properties.put(key, expand(properties, value));
				}
			}
		}
		return properties;
	}

	/**
	 * This method checks if any previously defined properties are used when
	 * declaring new properties and if so the previous property declaration is
	 * expanded to the value. e.g. if you have dir=/opt/mydrive
	 * file=${dir}/myfile
	 * 
	 * The value of file that is saved is "/opt/mydrive/myfile" (instead of)
	 * "${dir}/myfile"
	 * 
	 * @param existingProperties
	 *            - the properties map that is referenced for any potential
	 *            expansions
	 * @param valueToExpand
	 *            - the value that may be expanded
	 * @return - the expanded value
	 */
	public String expand(Map<String, Object> existingProperties, String valueToExpand) {
		boolean finished = false;

		// Assume we are not done in order to get into the loop
		while (!finished) {
			// Assume we are finished
			finished = true;
			Set<Entry<String, Object>> entrySet = existingProperties.entrySet();
			Iterator<Entry<String, Object>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				String searchString = "${" + entry.getKey() + "}";
				if (valueToExpand.contains(searchString)) {
					// If we find something to expand then expand and assume to
					// iterate
					// again to verify we are finished.
					finished = false;
					valueToExpand = valueToExpand.replace(searchString, (String) (entry.getValue()));
				}
			}
		}

		return valueToExpand;
	}
}
