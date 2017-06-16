package org.satix.constants;

public enum Browsers {
	FF {
		public String getName() {
			return "FF";
		}
		public int getValue() {
			return FF.ordinal();
		}
	}, 
	IE {
		public String getName() {
			return "IE";
		}
		public int getValue() {
			return IE.ordinal();
		}
	}, 
	CHROME {
		public String getName() {
			return "Chrome";
		}
		public int getValue() {
			return CHROME.ordinal();
		}
	},
	HTMLUNIT {
		public String getName() {
			return "HtmlUnit";
		}
		public int getValue() {
			return HTMLUNIT.ordinal();
		}
	},
	IPHONE {
		public String getName() {
			return "IPhone";
		}
		public int getValue() {
			return IPHONE.ordinal();
		}
	},	
	ANDROID {
		public String getName() {
			return "Android";
		}
		public int getValue() {
			return ANDROID.ordinal();
		}
	}, 
	SAFARI {
		public String getName() {
			return "Safari";
		}
		public int getValue() {
			return SAFARI.ordinal();
		}
	};

	abstract public int getValue();
	abstract public String getName();
}
