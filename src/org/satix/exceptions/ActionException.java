package org.satix.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.satix.actions.Action;
import org.satix.beans.TestCase;
import org.satix.utils.SatixLogger;


public class ActionException extends Exception {
	private static final String CLASSNAME = ActionException.class.getSimpleName();
	private static Logger logger = SatixLogger.getLogger(CLASSNAME);

	private static final long serialVersionUID = 1L;
	private Action action;

	public ActionException(Action action) {
		this.action = action;
	}

	public ActionException(Action action, String message) {
		super(message);
		this.action = action;
	}

	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionException(Action action, Throwable cause) {
		super(null, cause);
		this.action = action;
	}

	public ActionException(Action action, String message, Throwable cause) {
		super(message, cause);
		this.action = action;
	}

	public String getErrorMsg() {
		StringBuilder sb = new StringBuilder();
		if (action != null) {
			TestCase tc = action.getTestcase();
			if (tc != null) {
				getTestCaseErrorMsg(tc, sb);
			}
			if (getMessage()!=null && !getMessage().trim().equals("")) {
				sb.append(getMessage());
			} else {
				sb.append("Action error: name \"" + action.getName() + "\" number \"" + action.getNumber() + "\" line \"" 
						+ action.getLine() + "\" column \"" + action.getColumn() + "\" Element \""
						+ (action.getElement()!=null ? action.getElement() : action.getValue()) + "\" not found.");
			}
			return sb.toString();
		}
		return getMessage();
	}

	private void getTestCaseErrorMsg(TestCase tc, StringBuilder sb) {
		if (tc != null) {
			if (tc.getParentTestCase() != null) {
				getTestCaseErrorMsg(tc.getParentTestCase(), sb);
			}
			sb.append("Error in Test Case \"" + tc.getTitle() + "\"(" + tc.getDescription() + ") --> ");
		}
	}
	
	public void printAllErrorMsg(Throwable e) {
		if (e.getMessage() != null) {
			logger.log(Level.SEVERE, e.getClass().getCanonicalName() + ": " + e.getMessage());
		}
		if (e.getStackTrace() != null) {
			for (StackTraceElement st : e.getStackTrace()) {
				logger.log(Level.SEVERE, "\tat " + st.toString());
			}
		}
		if (e.getCause() != null) {
			logger.log(Level.SEVERE, "Caused by: ");
			printAllErrorMsg(e.getCause());
		}
	}
}
