package org.satix.actions;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.openqa.selenium.WebDriver;
import org.satix.exceptions.ActionException;
import org.satix.utils.SatixLogger;
import org.satix.utils.ScreenshotCapturer;

/**
 * This action is used to execute a group of actions within this action and 
 * the EndIf action
 *
 */
public class If extends Action {
	private static final String CLASSNAME = If.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);

	private String expression;
	private String expressionOriginal;

	@Override
	public void run(WebDriver driver) throws ActionException {
		try {
			long start_time = System.currentTimeMillis();
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");
			Boolean ret = null;
			String s = expressionOriginal;
			if (!expression.equals(expressionOriginal)) {
				s = expressionOriginal + " replaced by " + expression;
			}
			try {
				ret = (Boolean) engine.eval(expression);
			} catch (Exception e) {
				throw new ActionException(this, "Action \"" + getName()
						+ "\" line \"" + getLine() + "\", " + s
						+ " return ERROR", e);
			}
			if (!ret) {
				List<Action> actionList = getTestcase().getActionList();
				for (Action action : actionList) {
					if (action.getNumber() >= this.getNumber()) {
						if(action.getName().equals("endIf")){
							logger.log(Level.INFO, getTestcase() + "Action: name \""
									+ getName() + "\" number \"" + getNumber() + "\" line \""
									+ getLine() + "\" " + s + "return " + ret
									+ ", elapsed time:"
									+ (System.currentTimeMillis() - start_time) + "ms");
							return;
						}
						action.setIgnore(true);
					}
				}
			}
			logger.log(Level.INFO, getTestcase() + "Action: name \""
					+ getName() + "\" number \"" + getNumber() + "\" line \""
					+ getLine() + "\" " + s + "return " + ret + ", elapsed time:"
					+ (System.currentTimeMillis() - start_time) + "ms");
		} catch (Exception e) {
			try {
				ScreenshotCapturer.saveScreenShot(driver, getName());
			} catch (IOException e1) {
				throw new ActionException(this, "Save screenshot error!", e1);
			}
			throw new ActionException(this, "Action: name \"" + getName()
					+ "\" number \"" + getNumber() + "\" line \"" + getLine()
					+ "\"" + " " + e.getMessage(), e);
		}
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpressionOriginal() {
		return expressionOriginal;
	}

	public void setExpressionOriginal(String expressionOriginal) {
		this.expressionOriginal = expressionOriginal;
	}
}
