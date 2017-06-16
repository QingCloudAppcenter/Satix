package org.satix.constants;

public enum ReservedVariables {
	REPORT {
		public String getName() {
			return "_report";
		}
		public int getValue() {
			return REPORT.ordinal();
		}
	}, 
	REPORTTITLE {
		public String getName() {
			return "_reportTitle";
		}
		public int getValue() {
			return REPORTTITLE.ordinal();
		}
	}, 
	SENDEMAIL {
		public String getName() {
			return "_sendEmail";
		}
		public int getValue() {
			return SENDEMAIL.ordinal();
		}
	}, 
	SMTPSERVER {
		public String getName() {
			return "_SMTPServer";
		}
		public int getValue() {
			return SMTPSERVER.ordinal();
		}
	},
	FROM {
		public String getName() {
			return "_from";
		}
		public int getValue() {
			return FROM.ordinal();
		}
	},
	PASSWORD {
		public String getName() {
			return "_password";
		}
		public int getValue() {
			return PASSWORD.ordinal();
		}
	},
	TO {
		public String getName() {
			return "_to";
		}
		public int getValue() {
			return TO.ordinal();
		}
	},
	CC {
		public String getName() {
			return "_cc";
		}
		public int getValue() {
			return CC.ordinal();
		}
	},
	BCC {
		public String getName() {
			return "_bcc";
		}
		public int getValue() {
			return BCC.ordinal();
		}
	},
	SUBJECT {
		public String getName() {
			return "_subject";
		}
		public int getValue() {
			return SUBJECT.ordinal();
		}
	},
	BROWSER {
		public String getName() {
			return "_browser";
		}
		public int getValue() {
			return BROWSER.ordinal();
		}
	},
	FIREFOXBINARYPATH {
		public String getName() {
			return "_firefoxBinaryPath";
		}
		public int getValue() {
			return FIREFOXBINARYPATH.ordinal();
		}
	},
	FIREFOXPROFILEPATH {
		public String getName() {
			return "_firefoxProfilePath";
		}
		public int getValue() {
			return FIREFOXPROFILEPATH.ordinal();
		}
	},
	DEBUG {
		public String getName() {
			return "_debug";
		}
		public int getValue() {
			return DEBUG.ordinal();
		}
	},
	REPORTPATH {
		public String getName() {
			return "_reportPath";
		}
		public int getValue() {
			return REPORTPATH.ordinal();
		}
	},
	SCREENSHOTPATH {
		public String getName() {
			return "_screenshotPath";
		}
		public int getValue() {
			return SCREENSHOTPATH.ordinal();
		}
	},
	SCREENSHOTLEVEL {
		public String getName() {
			return "_screenshotLevel";
		}
		public int getValue() {
			return SCREENSHOTLEVEL.ordinal();
		}
	},
	LOGOUTPATH {
		public String getName() {
			return "_logOutPath";
		}
		public int getValue() {
			return LOGOUTPATH.ordinal();
		}
	},
	LOGERRPATH {
		public String getName() {
			return "_logErrPath";
		}
		public int getValue() {
			return LOGERRPATH.ordinal();
		}
	},
	/**
		when error happens, close the browser.
	*/
	CLOSEBROWSERONEXCEPTION {
		public String getName() {
			return "_CloseBrowserOnException";
		}
		public int getValue() {
			return CLOSEBROWSERONEXCEPTION.ordinal();
		}
	},
	OPENBROWSERFOREACHTESTCASE{
		public String getName() {
			return "_OpenBrowserForEachTestCase";
		}
		public int getValue() {
			return OPENBROWSERFOREACHTESTCASE.ordinal();
		}
	},
	PRINTDETAILERROR{
		public String getName() {
			return "_printDetailError";
		}
		public int getValue() {
			return PRINTDETAILERROR.ordinal();
		}
	},
	WAITELEMENTTIME{
		public String getName() {
			return "_waitElementTime";
		}
		public int getValue() {
			return WAITELEMENTTIME.ordinal();
		}
	},
	STOPIFFAILED{
		public String getName() {
			return "_stopIfFailed";
		}
		public int getValue() {
			return STOPIFFAILED.ordinal();
		}
	},
	WAITINGONACTION{
		public String getName() {
			return "_waitingOnAction";
		}
		public int getValue() {
			return WAITINGONACTION.ordinal();
		}
	},
	WEBDRIVERPATH{
		public String getName() {
			return "_WebdriverPath";
		}
		public int getValue() {
			return WEBDRIVERPATH.ordinal();
		}
	},
	TESTCASEFAILEDIFCOMPARERETURNFALSE{
		public String getName() {
			return "_TestCaseFailedIfCompareReturnFalse";
		}
		public int getValue() {
			return TESTCASEFAILEDIFCOMPARERETURNFALSE.ordinal();
		}
	};
	
	abstract public int getValue();
	abstract public String getName();
}
