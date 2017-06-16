package org.satix.utils;

import java.io.IOException;
import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import org.satix.exceptions.JSONException;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * A string parser utility
 * 
 */
public class StringParser {
	//private static final String CLASSNAME = StringParser.class.getSimpleName();
	//private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	/**
	 * Replace the variables in the input string with the values stored in the map.
	 * 
	 * @param s
	 *            input string
	 * @param varsMap
	 *            a map holding all pairs of variable-value
	 * @return a pure string with the variables replaced
	 * @throws IOException 
	 */
	public static String parseString(String s, HashMap<String, Object> varsMap) throws JSONException {
		if (!s.contains("${") || !s.contains("}") || varsMap == null) {
			return s; // no variable, return itself
		}
		
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		int i = 0;
		int k = 0;
		int m = 0;
		
		while (i < len) {
			if (!"$".equals(s.substring(i, i + 1))) {
				k = i;
				while (i < len && !"$".equals(s.substring(i, i + 1))) {
					i++;
				}
				sb.append(s.substring(k, i));
				continue;
			} else {
				if ("{".equals(s.substring(i + 1, i + 2))) {
					int j = i + 2;
					while (j < len && !"$".equals(s.substring(j, j + 1)) && !"}".equals(s.substring(j, j + 1))) {
						j++;
					}

					if ("$".equals(s.substring(j, j + 1))) {
						j = j - 1;

						if ("}".equals(s.substring(j, j + 1))) {							
							String varValue = null;
							if (s.length()> (i+7) && s.substring(i+2, i+7).equals("json:")) {
								varValue = JSONParser.getValue(varsMap, s.substring(i+7, j));
							} else {
								varValue = varsMap.get(s.substring(i+2, j)).toString();
							}
							if (null == varValue) {
								sb.append("${" + s.substring(i + 2, j) + "}"); // this is a runtime variable.
							} else {
								sb.append(varValue);
							}
						} else
							sb.append(s.substring(i, j + 1));
					} else if ("}".equals(s.substring(j, j + 1))) {						
						String varValue = null;
						if(s.length()> (i+7) && s.substring(i+2, i+7).equals("json:")){
							varValue = JSONParser.getValue(varsMap, s.substring(i+7, j));
						}else{
							varValue = (varsMap.get(s.substring(i+2, j)) != null)?varsMap.get(s.substring(i+2, j)).toString():null;
						}
						if (null == varValue) {
							sb.append("${" + s.substring(i + 2, j) + "}"); //a runtime variable.
						} else {
							sb.append(varValue);
						}
					}
					
					i = j + 1;
				} else {
					m = i;
					while (i + 1 < len && !"$".equals(s.substring(i + 1, i + 2))) {
						i++;
					}
					i++;
					sb.append(s.substring(m, i));
					continue;
				}
				continue;
			}
		}

		return sb.toString();
	}

	/**
	 * Check if the input string has variable or not. A variable is enclosed by ${ and }
	 * 
	 * @param str
	 * @return
	 */
	public static boolean containsVars(String str) {
		boolean ret = false;

		int index1 = str.indexOf("${");
		int index2 = str.indexOf("}");
		if (index1>=0 && index2>index1) {
			ret = true;
		}

		return ret;
	}

	public static void main(String[] args) throws IOException,JSONException  {
		HashMap<String, Object> varsMap = new HashMap<String, Object>();
		varsMap.put("v1", "def");
		varsMap.put("v3.a.b.c", JSONObject.parse("{\"abc\":123,\"def\":true}"));
		varsMap.put("v1.a.b.c", JSONObject.parse("{\"abc\":123,\"def\":456}"));
		varsMap.put("v2", JSONArray.parse("[\"jkl\"]"));
		varsMap.put("v3", "abcdefg");
		varsMap.put("username.abc", JSONArray.parse("[{\"username\":\"abc\"}]"));
		varsMap.put("jsonV.c.b.c", JSONArray.parse("[{\"value\":1,\"prop\":[[\"abc\"],{\"a\":\"def\",\"b\":123,\"c\":456},[\"xyz\"]],\"type\":\"string\"},{\"value\":2,\"prop\":\"abc\",\"type\":\"enum\"},{\"value\":3,\"prop\":\"xyz\",\"type\":\"teger\"}]"));
		
		String rtn1 = parseString("abc${v1}ghi${v2}", varsMap);
		String rtn2 = parseString("${bb${v1}${json:v2->[0]}${v1}", varsMap);
		String rtn3 = parseString("${v1}$${v2}${v1}", varsMap);
		String rtn4 = parseString("$ab$${json:v2->[0]}${v1}", varsMap);
		String rtn5 = parseString("aaaa$aaa", varsMap);
		String rtn6 = parseString("${v1${v1}}$${json:v2->[0]}${v1}", varsMap);
		String rtn7 = parseString("${v1}ghi${json:v2->[0]}abc", varsMap);
		String rtn8 = parseString("${json:v3.a.b.c->abc}ghi${json:v3.a.b.c->def}abc", varsMap);
		String rtn9 = parseString("${json:jsonV.c.b.c->length}ghi${json:jsonV.c.b.c->[0].prop[0][0]}abc", varsMap);

		System.out.println("rtn1: " + rtn1);
		System.out.println("rtn2: " + rtn2);
		System.out.println("rtn3: " + rtn3);
		System.out.println("rtn4: " + rtn4);
		System.out.println("rtn5: " + rtn5);
		System.out.println("rtn6: " + rtn6);
		System.out.println("rtn7: " + rtn7);
		System.out.println("rtn8: " + rtn8);
		System.out.println("rtn9: " + rtn9);
	}
}
