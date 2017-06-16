package org.satix.reports;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.satix.beans.TestCase;
import org.satix.beans.TestSuite;
import org.satix.constants.ReservedVariables;
import org.satix.properties.SystemConfigurations;
import org.satix.utils.EmailHelper;
import org.satix.utils.SatixLogger;


public class CSVReport extends GenericReport {
	private static final String CLASSNAME = CSVReport.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private String reportPath;

	public CSVReport(String suiteName) {
		super(suiteName);
	}

	public CSVReport(String suiteName, String fileName) {
		super(suiteName, fileName);
	}

	@Override
	public void print() throws FileNotFoundException {	
		List<TestSuite> testSuites = getTestSuites();
		if (null==testSuites || testSuites.size()==0) {
			return; //no test cases executed
		}
		
		if (getFileName() == null) {
			setFileName(getSuiteName() + "-" + new SimpleDateFormat("yyyy-MM-dd HH_mm_ss").format(new Date()));
		}
		String fileName = getFileName() + ".csv";
		File folder = new File(SystemConfigurations.getFolderPath(
				ReservedVariables.REPORTPATH.getName(), "." + File.separator + "reports"));
		if (!folder.exists()) {
			folder.mkdir();
		}
		PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(
				SystemConfigurations.getFolderPath(
						ReservedVariables.REPORTPATH.getName(), "." + File.separator + "reports") + fileName)));
		ps.println("System info: " + getPlatform());
		ps.println("Browser info: " + getBrowser());
		ps.println();
		ps.println("Test Case Name" + ",\t\t\t" + "Test Status" + ",\t\t" + "Elapsed(ms)\n");
		for (TestSuite ts : testSuites) {
			List<TestCase> testCases = ts.getTestCases();
			for (TestCase tc : testCases) {
				ps.print(tc.getTitle());
				ps.print(",\t\t");
				if (tc.getTitle().length() < 7)
					ps.print("\t\t");
				else if (tc.getTitle().length()>=7 && tc.getTitle().length()<15)
					ps.print("\t");
				ps.print(tc.getStatus());
				ps.print(",\t\t");
				ps.print(tc.getElapsedTime());
				ps.print("\n");
			}
		}
		ps.close();
	}

	@Override
	public void sendEmail() {
		reportPath = SystemConfigurations.getFolderPath(ReservedVariables.REPORTPATH.getName(), 
				"." + File.separator + "reports") + getFileName() + ".csv";
		File file = new File(reportPath);
		try {
			FileInputStream in = new FileInputStream(file);
			byte[] text = new byte[in.available()];
			in.read(text);
			if (file.exists()) {
				EmailHelper eu = new EmailHelper();
				eu.setBody(new String(text));
				eu.send();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "********" + e.getMessage());
			e.printStackTrace();
		}
	}
}
