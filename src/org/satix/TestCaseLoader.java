/*
 ******************************************************************************************
 * Author: Xiaosi Zhou
 * Version: 0.1
 * Date: 03/14/2011
 *******************************************************************************************
 * 
 */

package org.satix;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.satix.actions.Action;
import org.satix.beans.TestCase;
import org.satix.exceptions.ActionException;
import org.satix.exceptions.JSONException;
import org.satix.properties.ActionClassMapping;
import org.satix.utils.SatixLogger;
import org.satix.utils.StringParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * This class loads a test case from XML file into memory.
 * 
 */
public class TestCaseLoader extends DefaultHandler {
	private static final String CLASSNAME = TestCaseLoader.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private Locator locator;

	private StringBuffer textBuffer;

	private int actionNumber = 1; // starting from 1

	private boolean bActionElementStart;
	private TestCase testCase;
	private HashMap<String, String> actionInfo;
	private Map<String, Object> actionMapping;
	private HashMap<String, Object> configurations;
	
	public TestCase loadTestCase(String filenames) throws ActionException {
		testCase = new TestCase();
		testCase.setId(filenames + new Date());
		testCase.setTestCaseFilePath(filenames);
		if (configurations != null) {
			testCase.setConfigurations(configurations);
		}
		try {
			actionMapping = ActionClassMapping.getMapping();
		} catch (Exception ex) {
			throw new ActionException("Get action mapping error!", ex);
		}
		if (null == actionMapping) {
			logger.log(Level.SEVERE, "can't load action mappings.");
			System.exit(-1);
		}

		// Parse the XML file
		XMLReader xr;
		try {
			xr = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			throw new ActionException("Create xml reader error!", e);
		}
		xr.setContentHandler(this);
		xr.setErrorHandler(this);

		FileReader r;
		try {
			r = new FileReader(filenames);
			xr.parse(new InputSource(r));
			r.close();
		} catch (FileNotFoundException e) {
			throw new ActionException("File not found error!", e);
		} catch (IOException e) {
			throw new ActionException("File IO error!", e);
		} catch (SAXException e) {
			throw new ActionException("SAX error!", e);
		}
		
		return testCase;
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String namespaceURI, String localName, 
			String qName, Attributes attrs) {
		String eleName = localName; // element name
		if ("".equals(eleName)) {
			eleName = qName;
		}

		if (eleName.equals(TestCase.ELEMENT_ACTION)) {// Element "action"
			bActionElementStart = true;
			if (actionInfo != null) {
				actionInfo.clear();
			} else {
				actionInfo = new HashMap<String, String>();
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) {
		String eleName = localName; // element name
		if ("".equals(eleName)) {
			eleName = qName;
		}

		String s = " " + textBuffer;
		s = s.trim();
		textBuffer = null;
		
		if (eleName.equals(TestCase.ELEMENT_TITLE)) { //title
			testCase.setTitle(s);
		} else if (eleName.equals(TestCase.ELEMENT_DESCRIPTION)) { //description
			testCase.setDescription(s);
		} else if (eleName.equals(TestCase.ELEMENT_STOP_IF_FAILED)) { //stopIfFailed
			testCase.setStopIfFailed(s);
		}else if (eleName.equals(TestCase.ELEMENT_REPEAT)) { //repeat
			testCase.setRepeat(s);
		} else if (eleName.equals(TestCase.ELEMENT_CONFIG_FILE)) { //config file
			testCase.setConfigFile(s);
		} else if (eleName.equals(TestCase.ELEMENT_ACTION)) { //action
			bActionElementStart = false; // end of the action
			String actionName = (String) actionInfo.get("name");
			try {
				if (null == actionMapping) {
					logger.log(Level.SEVERE, "******** action mapping is empty.");
					System.exit(-1);
				}
				Object actionClassName = actionMapping.get(actionName);
				if (null==actionClassName || !(actionClassName instanceof String)) {
					logger.log(Level.SEVERE, "******** action class '" + actionName + 
							"' doesn't exist. Please check if the action name in the test case '" + 
							testCase.getTitle() + "' is correct or not. ");
					System.exit(-1);
				}
				Class<?> aClass = Class.forName((String) actionMapping.get(actionName));
				Object anObject = aClass.newInstance();
				// assign the methods here
				Method[] methods = aClass.getMethods();
				for (Method aMethod : methods) {
					String methodName = aMethod.getName();
					if (methodName.startsWith("set")) {
						String parameter = methodName.substring(3);
						parameter = parameter.substring(0, 1).toLowerCase() + parameter.substring(1);
						String value = (String) actionInfo.get(parameter);
						if (null == value)
							continue;
						try {
							aMethod.invoke(anObject, value);
							try {
								Method original = aClass.getMethod(methodName + "Original", new Class[] { String.class });
								original.invoke(anObject, value);
							} catch (Exception e) {
							}
						} catch (Exception e) {
							logger.log(Level.SEVERE, "********" + e.getMessage());
							System.exit(-1);
						}
					}
				}

				Action anAction = (Action) anObject;
				anAction.setNumber(actionNumber++);
				if ("true".equals(actionInfo.get("containsVars"))) {
					anAction.setContainsVariables(true);
				}
				anAction.setLine(actionInfo.get("line"));
				anAction.setColumn(actionInfo.get("column"));
				anAction.init(testCase); //we need to remember what test case this action belongs to.

				testCase.addAction(anAction);
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, "********" + e.getMessage());
			} catch (InstantiationException e) {
				logger.log(Level.SEVERE, "********" + e.getMessage());
			} catch (ClassNotFoundException e) {
				logger.log(Level.SEVERE, "******** action class '" + e.getMessage() 
						+ "' doesn't exist. Please check the file ActionMapping.properties "
						+ "to see if the action class is specified correctly or not.");
				System.exit(-1);
			} catch (ActionException e) {
				logger.log(Level.SEVERE, "********" + e.getMessage());
			}
		} else {
			try {
				if (bActionElementStart) {
					if (testCase.getConfigurations() != null) {
						s = StringParser.parseString(s, testCase.getConfigurations());
					}
					if (StringParser.containsVars(s)) {
						actionInfo.put("containsVars", "true");
					}
					actionInfo.put(eleName, s);
					if ("name".equals(eleName)) {
						actionInfo.put("line", String.valueOf(locator.getLineNumber()));
						actionInfo.put("column", String.valueOf(locator.getColumnNumber()));
					}
				}
			} catch (JSONException e) {
				logger.log(Level.SEVERE, "******** " + e.getMessage());
				System.exit(-1);
			}
		}
	}

	public void characters(char[] buf, int offset, int len) throws SAXException {
		String str = new String(buf, offset, len);
		if (null == textBuffer) {
			textBuffer = new StringBuffer(str);
		} else {
			textBuffer.append(str);
		}
	}

	public void ignorableWhitespace(char[] buf, int offset, int len) throws SAXException {
		// Ignore it
	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public HashMap<String, Object> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(HashMap<String, Object> configurations) {
		this.configurations = configurations;
	}
}
