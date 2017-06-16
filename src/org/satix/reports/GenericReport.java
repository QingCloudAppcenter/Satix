package org.satix.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.satix.beans.TestCase;
import org.satix.beans.TestSuite;

public class GenericReport {
	private List<TestCase> testCaseList = new ArrayList<TestCase>();
	private List<TestSuite> testSuites;
	private String browser;
	private String platform;
	private int totalFailedTestCases;
	private int totalTestCases;
	private long totalElapsed;
	private String suiteName;
	private String fileName;

	public GenericReport(String suiteName, String fileName) {
		this.suiteName = suiteName;
		this.fileName = fileName;
	}
	
	public GenericReport(String suiteName) {
		this.suiteName = suiteName;
	}

	public GenericReport() {
	}

	public void addTestCase(TestCase tc) {
		testCaseList.add(tc);
	}

	public List<TestCase> getTestCaseList() {
		return testCaseList;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowser() {
		return browser;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPlatform() {
		return platform;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTotalFailedTestCases() {
		totalFailedTestCases = 0;
		for (TestCase tc : testCaseList) {
			if ("Failed".equals(tc.getStatus())) {
				totalFailedTestCases++;
			}
		}
		return totalFailedTestCases;
	}

	public int getTotalRunningLog(){
		int result = 0;
		for (TestCase tc : testCaseList) {
			result = result + tc.getRunningLog().size();
		}
		return result;
	}
	
	public int getTotalTestCases() {
		totalTestCases = testCaseList.size();
		return totalTestCases;
	}

	public void print() throws Exception {
		System.out.println("System info: " + getPlatform());
		System.out.println();
		System.out.println("Test Case Name" + "        " + "Test Status" + "        " + "Elapsed(ms)");
		for (TestCase tc : testCaseList) {
			System.out.print(tc.getTitle() + "        " + tc.getStatus() + "        " + tc.getElapsedTime());
		}
	}

	public long getTotalElapsed() {
		totalElapsed = 0;
		for (TestCase tc : testCaseList) {
			totalElapsed += tc.getElapsedTime();
		}
		return totalElapsed;
	}

	public List<TestSuite> getTestSuites() {
		if (testCaseList!=null && testCaseList.size()>0){
			testSuites = new ArrayList<TestSuite>();
		}
		TestSuite report = new TestSuite();
		String parentPath = "";
		for (TestCase tc : testCaseList) {
			if (parentPath.equals(new File(tc.getTestCaseFilePath()).getParentFile().getAbsolutePath())) {
				report.addTestCase(tc);
			} else {
				if (report != null) {
					testSuites.add(report);
				}
				parentPath = new File(tc.getTestCaseFilePath()).getParentFile().getAbsolutePath();
				report = new TestSuite();
				report.setSuiteName(new File(parentPath).getName());
				report.addTestCase(tc);
			}
		}
		if (report != null) {
			testSuites.add(report);
		}
		return testSuites;
	}
	
	public int getTotalTestSuites() {
		if (testSuites != null) {
			return testSuites.size();
		}
		return 0;
	}
	
	public void sendEmail() {
	}
}
