package org.satix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.satix.beans.TestCase;
import org.satix.constants.ReservedVariables;
import org.satix.exceptions.ActionException;
import org.satix.properties.SystemConfigurations;
import org.satix.properties.GlobalVariables;
import org.satix.reports.CSVReport;
import org.satix.reports.CustomReport;
import org.satix.reports.GenericReport;
import org.satix.reports.HtmlReport;
import org.satix.utils.SatixLogger;

/**
 * This is the main program of Satix. It loads test cases and run them one by one.
 * And generates report for summary result.
 * @author Ray
 *
 */
public class SatixRunner {
	private static final String CLASSNAME = SatixRunner.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private static List<GenericReport> reportTypes = new ArrayList<GenericReport>();

	public static void main(String[] args) {
		try {			
			SatixRunner runner = new SatixRunner();
			
			//validate arguments
			Arguments argument = new Arguments();
			argument.validateArgs(args);
			String testcasePath = argument.getTestCasePath();			
			String config = argument.getConfiguration();
			if (config != null) {
				GlobalVariables.init(config); //load global variables if specified
			}			
			
			//set the system logging files
			SatixLogger.init();

			// initiate report type
			runner.initReport(testcasePath);
			
			logger.log(Level.INFO, "Loading test cases from \"" + testcasePath + "\"");
			List<TestCase> tcList = runner.loadTestCases(testcasePath);
			for (GenericReport report : reportTypes) {
				for (TestCase tc : tcList) {
					report.addTestCase(tc);
				}
			}
			
			//start to run test cases
			runner.runTestCases(tcList);
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "********" + e.getMessage());
			System.exit(0);
		} finally {
			for (GenericReport report : reportTypes) {
				report.setPlatform(BrowserDriver.getPlatformInfo());
				try {
					report.print();
				} catch (Exception e) {
					logger.log(Level.SEVERE, "********" + e.getMessage());
				}
				if ("true".equalsIgnoreCase(SystemConfigurations.getValue(ReservedVariables.SENDEMAIL.getName(), "false"))) {
					try {
						report.sendEmail();
					} catch (Exception e) {
						logger.log(Level.SEVERE, "********" + e.getMessage());
					}
				}
			}
		}
		System.exit(0);
	}

	/**
	 * The test cases are organized in several ways. It could be one test case 
	 * ending with .xml, or it could be a test suite with/without index.txt
	 * The index file specifies what test cases to run. 
	 * More specifically,
	 * Case 1: /to/path/<testcase>.xml - run the specific test case
	 * Case 2: /to/path/<index>.txt - run the test cases specified in the file
	 * Case 3: /to/path/  - run the test cases under this folder either 
	 *         specified by the index.txt in the folder or all test cases under
	 *         the folder if no index.txt found over there.
	 * Please read user guide for details.
	 * 
	 * @param path Test case or folder path
	 * @return a list of test cases
	 * @throws Exception
	 */
	public List<TestCase> loadTestCases(String path) throws Exception {
		List<TestCase> tcList = null;
		File file = new File(path);

		if (file.isFile()) {
			if (path.endsWith(".xml")) { //a single test case
				logger.log(Level.INFO, "Load test case \"" + path + "\".");
				tcList = new ArrayList<TestCase>();
				TestCaseLoader tcLoader = new TestCaseLoader();
				tcList.add(tcLoader.loadTestCase(file.getAbsolutePath()));
			}
			if (path.endsWith(".txt")) { // index file to run a test suite
				logger.log(Level.INFO, "Load test cases specified by the index file \"" + path + "\".");
				tcList = loadTestCasesByIndex(new File(path));
			}
		}

		if (file.isDirectory()) { //test suite
			File indexFile = new File(file.getAbsolutePath() + File.separator + "index.txt"); //get the index file
			if (null==indexFile || !indexFile.exists()) { // no index file, then it loads test cases under the folder in order.
				tcList = new ArrayList<TestCase>();
				for (File item : file.listFiles()) {
					if (item.isFile() && item.getAbsolutePath().endsWith(".xml")) {
						logger.log(Level.INFO, "Load test case \"" + item.getAbsolutePath() + "\".");
						TestCaseLoader tcLoader = new TestCaseLoader();
						tcList.add(tcLoader.loadTestCase(item.getAbsolutePath()));
					} else if (item.isDirectory()) { //recursively load test cases from sub-folders
						tcList.addAll(loadTestCases(item.getAbsolutePath()));
					}
				}
			} else { //a test suite with index file
				logger.log(Level.INFO, "Load test cases specified by the index file \"" + indexFile.getAbsolutePath() + "\".");
				tcList = loadTestCasesByIndex(indexFile);
			}
		}

		return tcList;
	}
	
	/**
	 * Load test cases specified by an index file
	 * 
	 * @param indexFile Index file containing the test cases to be executed.
	 * @return  a list of test cases
	 * @throws Exception
	 */
	private List<TestCase> loadTestCasesByIndex(File indexFile) throws Exception {
		List<TestCase> tcList = new ArrayList<TestCase>();
		
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(indexFile);
			br = new BufferedReader(fr);
			String absolutePath = indexFile.getParentFile().getAbsolutePath() + File.separator;

			String line = null;
			while ((line=br.readLine()) != null) {
				line = line.trim();
				// skip the line that starts with a "#" or "//" or blank line
				if (line.startsWith("#") || line.startsWith("//") || line.equals("")) {
					continue;
				} else {
					// treat depend case
					String fileName = line;
					String[] depend = null;
					if (line.endsWith(">")) {
						fileName = line.substring(0, line.indexOf("<")).trim();
						depend = line.substring(line.indexOf("<") + 1, line.length() - 1).split(",");
						for (int i=0; i<depend.length; i++) {
							depend[i] = absolutePath + depend[i];
						}
					}
					if (null==fileName || fileName.equals("")) {
						throw new Exception("Index file \"" + indexFile.getAbsolutePath() + "\" is not valid, please check!");
					}
					File sub = new File(absolutePath + fileName);
					if (!sub.exists()) {
						throw new Exception("Path \"" + absolutePath + fileName + "\" does not exist, please check!");
					}
					if (sub.isFile() && fileName.endsWith(".xml")) {
						logger.log(Level.INFO, "Generate test case \"" + absolutePath + fileName + "\"");
						TestCaseLoader tcLoader = new TestCaseLoader();
						TestCase tc = tcLoader.loadTestCase(absolutePath + fileName);
						if (depend != null) {
							tc.setDependingTCFileNames(depend);
						}
						tcList.add(tc);
					} else if (sub.isDirectory()) {
						List<TestCase> list = loadTestCases(absolutePath + fileName);
						tcList.addAll(list);
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
		}
		
		return tcList;
	}

	/**
	 * Run test cases one by one of the list passed by the parameter
	 * 
	 * @param tcList a list of test cases
	 */
	private void runTestCases(List<TestCase> tcList) {
		for (int i=0; i<tcList.size(); i++) {
			TestCase tc = tcList.get(i);
			if (tc != null) {
				boolean run = true;
				if (tc.getDependingTCFileNames() != null) {
					for (int j=0; j<i; j++) {
						String path = tcList.get(j).getTestCaseFilePath();
						for (String s : tc.getDependingTCFileNames()) {
							if (s.equals(path) && tcList.get(j).getStatus()!=null 
									&& "Failed".equals(tcList.get(j).getStatus())) {
								run = false;
							}
						}
					}
				}
				if (run) {
					runTestCase(tc);
				} else {
					logger.log(Level.INFO, "Dependent test case failed, Test Case \"" + tc.getTitle() 
							+ "(" + tc.getDescription() + ")\" will not run.");
				}
			}
		}

		// output the statistical information of the automation result.		
		GenericReport report = reportTypes.get(0);

		logger.log(Level.INFO, "Elapsed time for all the test cases running: " + report.getTotalElapsed() 
				+ "ms. (" + (report.getTotalTestCases() - report.getTotalFailedTestCases())
				+ " cases passed, " + report.getTotalFailedTestCases() + " cases failed)");
	}

	/**
	 * Run a single test case
	 * 
	 * @param tc a test case
	 */
	private void runTestCase(TestCase tc) {
		long start_time = System.currentTimeMillis();
		boolean stopIfFailed = false;
		try { 
					
			if ("true".equalsIgnoreCase(SystemConfigurations.getValue(
					ReservedVariables.OPENBROWSERFOREACHTESTCASE.getName(), "false"))) {
				BrowserDriver.openBrowser(GlobalVariables.getBrowserType());
				logger.log(Level.INFO, "Open browser for test case " + tc.getTitle());
			}
			tc.setStatus("Running");
			if (reportTypes.size() > 0) {
				for (GenericReport report : reportTypes) {
					report.setPlatform(BrowserDriver.getPlatformInfo());				
					report.print();				
				}
			}
			tc.run();
			logger.log(Level.INFO, "Elapsed time for the test case[" + tc.getTitle() + "]: " 
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (ActionException e) {
			tc.setErrorLog(e.getErrorMsg());
			tc.setStatus("Failed");
			
			logger.log(Level.SEVERE, "********Errors:" + e.getErrorMsg()); 
			if(null!=e.getCause().getMessage()){
			logger.log(Level.SEVERE, "********Errors:" + e.getCause().getMessage());
			}
			
			if ("true".equalsIgnoreCase(SystemConfigurations.getValue(
					ReservedVariables.PRINTDETAILERROR.getName(), "false"))){
				e.printAllErrorMsg(e);
			}
			if ("true".equalsIgnoreCase(SystemConfigurations.getValue(
					ReservedVariables.CLOSEBROWSERONEXCEPTION.getName(), "false"))) {
				if (BrowserDriver.getBrowserDriver() != null) {
					BrowserDriver.getBrowserDriver().quit();
				}
			}
			if (tc.getStopIfFailed()!=null && "true".equalsIgnoreCase(tc.getStopIfFailed()) 
					&& "true".equalsIgnoreCase(SystemConfigurations.getValue(
							ReservedVariables.STOPIFFAILED.getName(), "true"))) {
				logger.log(Level.INFO, "Error in Test Case \"" + tc.getTitle() + "(" 
					+ tc.getDescription() + "), exit Satix since \"stopIfFailed\" of this test case is set to true.");
				stopIfFailed = true;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "********Errors:" + e);
		} finally {
			if (reportTypes.size() > 0) {
				for (GenericReport report : reportTypes) {
					report.setPlatform(BrowserDriver.getPlatformInfo());
					try {
						report.print();
					} catch (Exception e) {
						logger.log(Level.SEVERE, "********Errors:" + e.getMessage());
					}
				}
			}
			if (stopIfFailed) {
				if ("true".equalsIgnoreCase(SystemConfigurations.getValue(ReservedVariables.SENDEMAIL.getName(), "false"))
						&& reportTypes.size() > 0) {
					for (GenericReport report : reportTypes) {
						try {
							report.sendEmail();
						} catch (Exception e) {
							logger.log(Level.SEVERE, "*********Errors:" + e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * The function is to initiate reports. The filename of the reports depends on the test case. 
	 * If it is a test suite, the filename will be [test_suite_name|test_case_name]-yyyy-mm-dd
	 * hh_mm_ss.[html|csv|txt]
	 * 
	 * @param path The path of the test suite or the test case.
	 */
	private void initReport(String path) {
		// initiate report type
		String types = SystemConfigurations.getValue(ReservedVariables.REPORT.getName(), "console");
		GenericReport report = null;
		String suiteName = getSuiteName(path);
		if (types.contains("html")) {
			report = new HtmlReport(suiteName);
			reportTypes.add(report);
		}
		if (types.contains("csv")) {
			report = new CSVReport(suiteName);
			reportTypes.add(report);
		}
		if (types.contains("custom")) {
			report = new CustomReport(suiteName);
			reportTypes.add(report);
		}
		if (types.contains("console")) {
			report = new GenericReport(suiteName);
			reportTypes.add(report);
		}

		if (null == report) { // create a default report if not specified.
			report = new GenericReport(suiteName);
			reportTypes.add(report);
		}
	}

	private String getSuiteName(String path) {
		String suiteName = null;
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				if (file.isDirectory()) {
					suiteName = file.getName();
				} else {
					if (path.endsWith(".xml")) {
						suiteName = file.getName();
					} else {
						suiteName = file.getParentFile().getName();
					}
				}
			}
		}
		return suiteName;
	}
}
