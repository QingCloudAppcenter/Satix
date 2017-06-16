package org.satix;

import java.io.File;

/**
 * This class is used to validate the arguments input by Satix users.
 * @author Ray
 *
 */
public class Arguments {
	
	private final String EXCEPTION = "The parameters specified are incorrect."+
			" Please type satix -h for details."; 
	private String configuration;
	private String testCasePath;
	
	public void validateArgs(String[] args) {
		if (args.length!=1 && args.length!=2 && args.length!=4) {
			throw new IllegalArgumentException(EXCEPTION);
		}      

        if (args.length == 1) { //satix -h|-help
        	 if (args[0].equals("-h") || args[0].equals("-help")) {
                 usage();
                 System.exit(0);
              } else {
            	  throw new IllegalArgumentException(EXCEPTION);  
              }
        }
        if (args.length == 2) { //satix -t testcase
        	if (args[0].equals("-t")) {
        		checkArg("-t", args[1], "The test case file or folder does not exist.");        		
        	} else {
        		throw new IllegalArgumentException(EXCEPTION);
        	}
        } else if (args.length == 4) { //satix -c configure -t testcase
        	if (args[0].equals("-c") && args[2].equals("-t")) {        		
        		checkArg("-c", args[1], "The configuation file does not exist.");
        		checkArg("-t", args[3], "The test case file or folder does not exist.");
        	} else if (args[0].equals("-t") && args[2].equals("-c")) {
        		checkArg("-t", args[1], "The test case file or folder does not exist.");
        		checkArg("-c", args[3], "The configuation file does not exist.");        		
        	} else {
        		throw new IllegalArgumentException(EXCEPTION);
        	}
        	
        }     
    }

	private void checkArg(String type, String arg, String msg) {
		File path = new File(arg);
		if (!path.exists()) {
			throw new IllegalArgumentException(msg);
		} else {
			if (type.equals("-t")) {
				setTestCasePath(arg);
			} else if (type.equals("-c")) {
				setConfiguration(arg);
			}
		}
	}
	
	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	public String getTestCasePath() {
		return testCasePath;
	}
	public void setTestCasePath(String testCasePath) {
		this.testCasePath = testCasePath;
	}
	
    public void usage() {
        System.out.println("Usage: satix.bat|sh [-c <global property file>] -t <test case file or folder>");
        System.out.println("   or: satix.bat|sh -h|-help                                      ");
        System.out.println("                                                                  ");
        System.out.println("  -c  load the global configuration file. It is optional          ");
        System.out.println("  -t  load the test cases to be executed                          ");
        System.out.println("  -h  this help                                                   ");
    }
}
