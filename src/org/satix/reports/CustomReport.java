package org.satix.reports;

public class CustomReport extends GenericReport {
	public CustomReport(String suiteName) {
		super(suiteName);
	}
	
	public CustomReport(String suiteName, String uniqueName) {
		super(suiteName, uniqueName);
	}
	
	@Override
	public void print() throws Exception {
		super.print();
		
		//type your code here
	}
}
