package org.satix.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.satix.actions.Action;
import org.satix.exceptions.ActionException;
import org.satix.properties.GlobalVariables;
import org.satix.properties.PropertiesLoader;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;


/**
 * A test case bean.
 * 
 */
public class TestCase {
	private static final String CLASSNAME = TestCase.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	public static final String ELEMENT_TITLE = "title";
	public static final String ELEMENT_DESCRIPTION = "description";
	public static final String ELEMENT_STOP_IF_FAILED = "stopIfFailed";
	public static final String ELEMENT_REPEAT = "repeat";
	public static final String ELEMENT_CONFIG_FILE = "configFile";
	public static final String ELEMENT_ACTION = "action";

	private List<Action> actionList = new ArrayList<Action>();
	private String title;
	private String description;
	private String browser;
	private String stopIfFailed;
	
	private String repeat;
	private String testCaseFilePath;
	private List<String> configFiles = new ArrayList<String>();
	private TestCase parentTestCase; //the parent of this test case
	private String[] dependingTCFileNames; //the list of file names of the test cases that this test case depends on

	private HashMap<String, Object> configurations;
	private long elapsedTime;
	private String status = "Not running";
	private String errorLog;
	private List<String> runningLog = new ArrayList<String>();
	private String suiteName;
	private String id;

	public TestCase(){
		if (null == configurations) {
			configurations = new HashMap<String, Object>();
		}
		if (GlobalVariables.getConfigurations() != null) {
			configurations.putAll(GlobalVariables.getConfigurations());
		}
	}

	public void run() throws ActionException {
		long start_time = System.currentTimeMillis();
		long end_time = 0;
		long elapsed_time = 0;

		ScreenshotCapturer.setName(getTitle());
		
		int repeat = 0;
		try {
			repeat = Integer.parseInt(getRepeat());
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "repeat parameter is not a number. Use 1 instead.");
			repeat = 1;
		}
		logger.log(Level.INFO, "Starting to run the test case \"" + getTitle() 
				+ "\", repeat " + repeat + " time(s).");

		int count = 0;
		while (count < repeat) {
			for (int i=0; i<actionList.size(); i++) {
				Action anAction = actionList.get(i);
				if (anAction != null) {
					try {
						anAction.updateVariableValues();
						anAction.runWithCondition(anAction.getBrowserDriver());
					} catch (ActionException e) {
						throw e;
					} catch (Exception e) {
						e.printStackTrace();
						throw new ActionException(anAction, e);
					} finally {
						end_time = System.currentTimeMillis();
						elapsed_time = end_time - start_time;
						setElapsedTime(elapsed_time);
					}
				}
			}
			count++;
		}
		if(status.equals("Running")){
			setStatus("Successful");
		}
	}

	public void setActionlist(List<Action> actionList) {
		this.actionList = actionList;
	}

	public List<Action> getActionList() {
		return actionList;
	}

	public void addAction(Action anAction) {
		actionList.add(anAction);
	}
	
	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowser() {
		return browser;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setStopIfFailed(String stopIfFailed) {
		this.stopIfFailed = stopIfFailed;
	}

	public String getStopIfFailed() {
		return stopIfFailed;
	}
	
	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public String getTestCaseFilePath() {
		return testCaseFilePath;
	}

	public void setTestCaseFilePath(String testCaseFilePath) {
		this.testCaseFilePath = testCaseFilePath;
	}

	public List<String> getConfigFiles() {
		return configFiles;
	}

	public void setConfigFile(String configFile) {
		configFiles.add(configFile);
		File file = new File(configFile);
		if (!file.exists()) {
			file = new File(testCaseFilePath.substring(0, testCaseFilePath.lastIndexOf(System.getProperty("file.separator")) + 1) + configFile);
			if (!file.exists()) {
				logger.log(Level.SEVERE, "Can not find mapping file \"" + configFile + "\" in test case \"" + testCaseFilePath + "\".");
				System.exit(-1);
			}
		}

		PropertiesLoader prop = new PropertiesLoader();
		try {
			if (configurations == null) {
				configurations = new HashMap<String, Object>();
			}
			configurations.putAll(prop.load(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Can not find mapping file \"" + file.getAbsolutePath() + "\" in test case \"" + testCaseFilePath + "\".");
			System.exit(-1);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IO error with mapping file \"" + file.getAbsolutePath() + "\" in test case \"" + testCaseFilePath + "\".");
			System.exit(-1);
		}
	}
	
	public HashMap<String, Object> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(HashMap<String, Object> configurations) {
		this.configurations = configurations;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public TestCase getParentTestCase() {
		return parentTestCase;
	}

	public void setParentTestCase(TestCase parentTestCase) {
		this.parentTestCase = parentTestCase;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public List<String> getRunningLog() {
		return runningLog;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public void appendErrorLog(){
		
	}
	
	public void setRunningLog(String runningLog) {
		this.runningLog.add(runningLog);
	}
	
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String[] getDependingTCFileNames() {
		return dependingTCFileNames;
	}

	public void setDependingTCFileNames(String[] dependingTCFileNames) {
		this.dependingTCFileNames = dependingTCFileNames;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (getParentTestCase() != null) {
			sb.append(getParentTestCase().toString());
		}
		sb.append("Test Case \"" + getTitle() + "\"(" + getDescription() + ") --> ");

		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TestCase) {
			if (getId() != null && ((TestCase) o).getId() != null && this.getId().equals(((TestCase) o).getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
}
