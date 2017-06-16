package org.satix.utils;

import java.io.IOException;
import java.util.HashMap;

import org.satix.exceptions.JSONException;

import com.ibm.json.java.JSONObject;
import com.ibm.json.java.JSONArray;
public class JSONParser {
	/**
	 * Get specific value from a JSONObject stored in map.
	 * @param map
	 * map store (key,value) pairs, the value should be a JSONObject or JSONArray
	 * @param properties
	 * properties include 2 parts, key->navInfo
	 * key is used to locate the value in map, navInfo is used to locate the specific 
	 * value in the JSONObject or JSONArray
	 * e.g. key1->attr1.attr2, key2->[2].attr1
	 * @return
	 * @throws IOException 
	 * @author Yue Chen
	 */
	public static String getValue(HashMap<String, Object> map, String properties) throws JSONException {
		String[] props = properties.split("->");	//separate the properties to key and navInfo
		if(props.length!=2){	//if the split result is not correct
			throw new JSONException(" ${json:"+properties+"} is not a correct format to get a json value. \n"+
					"please reference the example: e.g. key1->attr1.attr2, key2->[2].attr1");
		}
		Object jsonRes = map.get(props[0]);
		if(jsonRes != null){
			jsonRes = _getJSONValue(jsonRes, props[1]);
		}
		return jsonRes == null? null : jsonRes.toString();
	}
	
	private static Object _getJSONValue(Object json, String navInfo) throws JSONException{
		
		if(json instanceof JSONArray) {
			int sepPos = 0;			//position to seperate navInfo ']'
			if(navInfo.indexOf('[')==0 && (sepPos = navInfo.indexOf(']'))>0){
				int arrayIndex = Integer.valueOf(navInfo.substring(1, sepPos));
				if(sepPos == navInfo.length()-1){
					return ((JSONArray)json).get(arrayIndex);
				}
				return _getJSONValue(((JSONArray)json).get(arrayIndex), navInfo.substring(sepPos+1, navInfo.length()));
			}else if(navInfo.equals("length") || navInfo.equals(".length")){
				return ""+((JSONArray)json).size();
			}else{
				throw new JSONException("Incorrect navInfo "+navInfo+" to get from JSONArray "+json);
			}
		}else if(json instanceof JSONObject) {
			int indexOfDot = navInfo.indexOf('.');
			int sepPos = 0;
			if(indexOfDot == 0){
				navInfo = navInfo.substring(1);
				indexOfDot = navInfo.indexOf('.');
			}
			int indexOfPar = navInfo.indexOf('[');
			
			if(indexOfPar > 0 && indexOfDot > 0){
				sepPos = Math.min(indexOfPar, indexOfDot);
			}else if(indexOfPar > 0){
				sepPos = indexOfPar;
			}else if(indexOfDot > 0){
				sepPos = indexOfDot;
			}else{
				return ((JSONObject)json).get(navInfo);
			}
			return _getJSONValue(((JSONObject)json).get(navInfo.substring(0, sepPos)),
					navInfo.substring(sepPos, navInfo.length()));
		}else {
			throw new JSONException("josn navInfo "+navInfo+" cannot nav in json "+json);
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException,IOException {
		// TODO Auto-generated method stub
		HashMap<String, Object> varsMap = new HashMap<String, Object>();
		varsMap.put("v1.a.b.c", JSONObject.parse("{\"abc\":123,\"def\":456}"));
		varsMap.put("v2", JSONArray.parse("[\"jkl\"]"));
		varsMap.put("v3", "abcdefg");
		varsMap.put("username.abc", JSONArray.parse("[{\"username\":\"abc\"}]"));
		varsMap.put("jsonV.c.b.c", JSONArray.parse("[{\"value\":1,\"prop\":[[\"abc\"],{\"a\":\"def\",\"b\":123,\"c\":456},[\"xyz\"]],\"type\":\"string\"},{\"value\":2,\"prop\":\"abc\",\"type\":\"enum\"},{\"value\":3,\"prop\":\"xyz\",\"type\":\"teger\"}]"));
		
		System.out.println(getValue(varsMap,"jsonV.c.b.c->[0].prop[1].b"));
		System.out.println(getValue(varsMap,"v1.a.b.c->def"));
		System.out.println(getValue(varsMap,"username.abc->[0].username"));
		System.out.println(getValue(varsMap,"jsonV.c.b.c->[0].prop.length"));
		System.out.println(getValue(varsMap,"v2->[0]"));
		//System.out.println(getValue(varsMap,"v3"));
	}

}
