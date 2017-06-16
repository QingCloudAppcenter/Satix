package org.satix.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.openqa.selenium.WebDriver;
import org.satix.constants.ReservedVariables;
import org.satix.exceptions.ActionException;
import org.satix.properties.SystemConfigurations;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;


/**
 * This action is used to compare two values. Since we may have lots of logics 
 * such as <, >, >=, <=, ==, && and ||, moreover, there are more than two values
 * involved. In another word, it could be a very complicated expression. So we 
 * generalize this action to evaluate an expression.
 * Note: You MUST construct the expressing using Javascript syntax.
 * 
 */
public class Compare extends Action {
	private static final String CLASSNAME = Compare.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	private String expression;
	private String expressionOriginal;
	private String stopIfReturnFalse;
	
	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");
			Boolean ret = null;
			String s = expressionOriginal;
			if(!expression.equals(expressionOriginal)){
				s = expressionOriginal + " replaced by " + expression;
			}
			try {
				ret = (Boolean) engine.eval(expression);
			} catch (Exception e) {
				getTestcase().setRunningLog("Action \"" + getName() + "\" line \"" + getLine() + "\", " + s + " return ERROR");
				throw new ActionException(this, "Action \"" + getName() + "\" line \"" + getLine() + "\", " + s + " return ERROR", e);
			}
			if (ret) {
				getTestcase().setRunningLog("Action \"" + getName() + "\" line \"" + getLine() + "\", " + s + " return TRUE");
				logger.log(Level.INFO, getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() + "\" line \"" 
						+ getLine() + "\" " + s + " return TRUE, elapsed time:" + (System.currentTimeMillis() - start_time) + "ms");
			} else {
				if(SystemConfigurations.getValue(ReservedVariables.TESTCASEFAILEDIFCOMPARERETURNFALSE.getName(), "true").equalsIgnoreCase("true")){
					getTestcase().setRunningLog("Action \"" + getName() + "\" line \"" + getLine() + "\", " + s + " return FALSE");
					logger.log(Level.SEVERE, "********" + getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() 
							+ "\" line \"" + getLine() + "\" " + s + " return FALSE, elapsed time:"
							+ (System.currentTimeMillis() - start_time) + "ms");
					getTestcase().setStatus("Failed");
					getTestcase().setErrorLog("Action \"" + getName() + "\" line \"" + getLine() + "\", " + s + " return FALSE");
				}else{
					getTestcase().setRunningLog("Action \"" + getName() + "\" line \"" + getLine() + "\", " + s + " return FALSE");
					logger.log(Level.INFO,getTestcase() + "Action: name \"" + getName() + "\" number \"" + getNumber() + "\" line \"" 
							+ getLine() + "\" " + s + " return FALSE, elapsed time:" + (System.currentTimeMillis() - start_time) + "ms");
				}
				
			}
			if ("true".equalsIgnoreCase(stopIfReturnFalse)) {
				if (!ret) {
					throw new ActionException(this, "Action: name \"" + getName() + "\" number \"" + getNumber() + "\" line \"" + getLine() 
							+ "\" " + s + " return FALSE, elapsed time:" + (System.currentTimeMillis() - start_time) + "ms");
				}
			}
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, "Action: name \"" + getName() + "\" number \"" + getNumber() + "\" line \"" 
					+ getLine() + "\"" + " error", e);
		}
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setExpressionOriginal(String expressionOriginal) {
		this.expressionOriginal = expressionOriginal;
	}

	public String getStopIfReturnFalse() {
		return stopIfReturnFalse;
	}

	public void setStopIfReturnFalse(String stopIfReturnFalse) {
		this.stopIfReturnFalse = stopIfReturnFalse;
	}
}
