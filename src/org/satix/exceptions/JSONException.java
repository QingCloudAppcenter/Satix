package org.satix.exceptions;

public class JSONException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public JSONException(){
		super();
	}
	
	public JSONException(String message){
		super("JSONException:" + message);
	}
	
	public JSONException(String message, Throwable cause){
		super("JSONException:" + message, cause);
	}
	
	public JSONException(Throwable cause){
		super(cause);
	}
}
