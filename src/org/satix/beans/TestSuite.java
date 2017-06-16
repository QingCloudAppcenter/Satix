package org.satix.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of test cases
 * 
 * @author Ray
 *
 */
public class TestSuite {
	private String suiteName;
	private List<TestCase> testCaseList = new ArrayList<TestCase>();

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public void addTestCase(TestCase tc) {
		testCaseList.add(tc);
	}

	public List<TestCase> getTestCases() {
		return testCaseList;
	}

}
