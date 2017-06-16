package org.satix.properties;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.satix.utils.SatixLogger;


/**
 * This class loads the file ActionMapping.properties and the extension file 
 * ActionMapping_ext.properties, and stores the relationship of action name 
 * used by XML-based test case and action java class provided by system 
 * (system action) or end users (custom action)
 * 
 * @author Ray(Xiaosi) Zhou
 * 
 */
public class ActionClassMapping {
	private static final String CLASSNAME = ActionClassMapping.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private static Map<String, Object> mapping;

	private ActionClassMapping() {
	}

	public static Map<String, Object> getMapping() {
		if (null == mapping) {			
			PropertiesLoader props = new PropertiesLoader();
			ActionClassMapping instance = new ActionClassMapping();
			try {
				mapping = props.load(instance.getClass().getResourceAsStream("/mapping/ActionMapping.properties"));
				mapping.putAll(props.load(instance.getClass().getResourceAsStream("/mapping/ActionMapping_ext.properties")));				
			    logger.log(Level.INFO, "Action mappings loaded.");
			} catch (Exception e) {	
				logger.log(Level.SEVERE, "Action mappings loaded failed.");
				e.printStackTrace();				
			}
		}		
		return mapping;
	}
}
