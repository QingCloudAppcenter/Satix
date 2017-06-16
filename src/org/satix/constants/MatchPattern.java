package org.satix.constants;

public enum MatchPattern {
	GLOB {
		public String getName() {
			return "glob:";
		}
		public int getValue() {
			return GLOB.ordinal();
		}
	}, 
	REGEXP {
		public String getName() {
			return "regexp:";
		}
		public int getValue() {
			return REGEXP.ordinal();
		}
	}, 
	EXACT {
		public String getName() {
			return "exact:";
		}
		public int getValue() {
			return EXACT.ordinal();
		}
	};

	abstract public int getValue();
	abstract public String getName();
	
	public static void main(String[] args) {
		System.out.println(MatchPattern.GLOB.getName());
		System.out.println(MatchPattern.GLOB.getValue());
		System.out.println(MatchPattern.REGEXP.getName());
		System.out.println(MatchPattern.REGEXP.getValue());
		System.out.println(MatchPattern.EXACT.getName());
		System.out.println(MatchPattern.EXACT.getValue());
	}
}
