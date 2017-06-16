package org.satix.actions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.satix.Cache;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action is used to store an attribute value or a specific property value
 * of an attribute from a web element. For example, we can get the whole value
 * of the attribute ¡°style¡± from the following web element, ¡°width: 200px; 
 * height: 50px; border: 1px solid black;¡±. We also can get a specific property
 * value of the attribute ¡°style¡± such as the width ¨C 200px. We even can get 
 * the numeric part of the property, 200, which makes more sense for later 
 * processing such as using compare action to validate the width is correct or not.
 * 		<div style="width: 200px; height: 50px; border: 1px solid black;" 
 * 				id="testStoreElementActionId" name="testStoreElementActionName">
 *
 */
public class StoreElement extends Action {
	private static final String CLASSNAME = StoreElement.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private String attribute;
	private String variable;
	private String type;

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			WebElement webElement = WaitForWebElementToLoad(driver, getElement(), getElementBy());
			String value = null;
			if ("text".equals(attribute)) {
				value = webElement.getText();
			} else if ("tag".equals(attribute)) {
				value = webElement.getTagName();
			} else {
				value = webElement.getAttribute(getAttributeName(attribute));
				value = getAttributeValue(attribute, value);
			}
			if (type != null && "int".equalsIgnoreCase(type)) {
				value = String.valueOf(parseLong(value));
			}
			if (type != null && "float".equalsIgnoreCase(type)) {
				value = String.valueOf(doubleValue(value));
			}
			
			Cache.putTestCaseVar(getVariable(), value);
			
			logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() 
					+ "\" line \"" + getLine() + "\" passed, elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, e);
		}
	}

	public String getAttributeName(String attributeName){
		if (attributeName.indexOf(".") == -1) {
			if (attributeName.indexOf("[") == -1) {
				return attributeName;
			} else {
				return attributeName.substring(0, attributeName.indexOf("["));
			}
		} else {
			return attributeName.substring(0, attributeName.indexOf("."));
		}
	}
	
	public String getAttributeValue(String attributeName, String attrValue) {
		String subName = null;
		int index = -1;
		
		if (attributeName.indexOf(".") == -1) {
			if (attributeName.indexOf("[") == -1) {
				return attrValue;
			} else {
				index = Integer.parseInt(attributeName.substring(attributeName.indexOf("["), attributeName.indexOf("]")));
				String[] ss = attrValue.split(" ");
				return ss[index];
			}
		} else {
			if (attributeName.indexOf("[") == -1) {
				subName = attributeName.substring(attributeName.indexOf(".") + 1);
				String[] ss = attrValue.split(";");
				for(String s : ss){
					if(s.substring(0, s.indexOf(":")).trim().equals(subName)){
						return s.substring(s.indexOf(":") + 1).trim();
					}
				}
			} else {
				subName = attributeName.substring(attributeName.indexOf(".") + 1, attributeName.indexOf("["));
				index = Integer.parseInt(attributeName.substring(attributeName.indexOf("[") + 1, attributeName.indexOf("]")));
				String[] ss = attrValue.split(";");
				for (String s : ss) {
					if(s.substring(0, s.indexOf(":")).trim().equals(subName)){
						String[] sss = s.substring(s.indexOf(":") + 1).split(" ");
						return sss[index].trim();
					}
				}
			}
		}
		return null;
	}

	public String doubleValue(String s) throws NumberFormatException {
		if (s != null && s.length() > 0) {
			int position = 0;
			s = s.replaceAll(",", "");
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (i == 0) {
					if (c == '-' || c == '.' || Character.isDigit(c)) {
						position = 1;
						continue;
					} else {
						throw new NumberFormatException();
					}
				}
				if (!(Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
					position = i;
					break;
				} else {
					position = i + 1;
				}
			}
			s = s.substring(0, position);
			
			StringBuffer template = new StringBuffer();
			for(int j=0; j<s.length(); j++){
				if(Character.isDigit(s.charAt(j))){
					template.append("#");
				}else{
					template.append(s.charAt(j));
				}
			}
			return new DecimalFormat(template.toString()).format(Double.parseDouble(s.substring(0, position)));
		} else {
			throw new NumberFormatException();
		}
	}

	public String doubleValue2(String s) throws NumberFormatException{
		if (s != null && s.length() > 0) {
			int position = 0;
			s = s.replaceAll(",", "");
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (i == 0) {
					if (c == '-' || c == '.' || Character.isDigit(c)) {
						position = 1;
						continue;
					} else {
						throw new NumberFormatException();
					}
				}
				if (!(Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
					position = i;
					break;
				} else {
					position = i + 1;
				}
			}
			return s.substring(0, position);
		} else {
			throw new NumberFormatException("Value can not be parsed into float!");
		}
	}
	
	public long parseLong(String s) throws NumberFormatException {
		if (s != null && s.length() > 0) {
			int position = 0;
			s = s.replaceAll(",", "");
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (i == 0) {
					if (c == '-' || Character.isDigit(c)) {
						position = 1;
						continue;
					} else {
						throw new NumberFormatException();
					}
				}
				if (!(Character.isDigit(s.charAt(i)))) {
					position = i;
					break;
				} else {
					position = i + 1;
				}
			}
			return Long.parseLong(s.substring(0, position));
		} else {
			throw new NumberFormatException();
		}
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getVariable() {
		return variable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
