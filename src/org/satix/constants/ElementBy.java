package org.satix.constants;

public enum ElementBy {
	ID {
		public String getName() {
			return "id";
		}
		public int getValue() {
			return ID.ordinal();
		}
	}, 
	NAME {
		public String getName() {
			return "name";
		}
		public int getValue() {
			return NAME.ordinal();
		}
	}, 
	XPATH {
		public String getName() {
			return "xpath";
		}
		public int getValue() {
			return XPATH.ordinal();
		}
	},
	LINKTEXT {
		public String getName() {
			return "linkText";
		}
		public int getValue() {
			return LINKTEXT.ordinal();
		}
	},
	PARTIALLINKTEXT {
		public String getName() {
			return "partialLinkText";
		}
		public int getValue() {
			return PARTIALLINKTEXT.ordinal();
		}
	},
	CLASSNAME {
		public String getName() {
			return "className";
		}
		public int getValue() {
			return CLASSNAME.ordinal();
		}
	},
	CSSSELECTOR {
		public String getName() {
			return "cssSelector";
		}
		public int getValue() {
			return CSSSELECTOR.ordinal();
		}
	},
	TAGNAME {
		public String getName() {
			return "tagName";
		}
		public int getValue() {
			return TAGNAME.ordinal();
		}
	};

	abstract public int getValue();
	abstract public String getName();
}
