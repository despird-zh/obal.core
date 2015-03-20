package com.dcube.base;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base blank tester 
 **/
public class BaseTester extends TestCase{
	
	static boolean LOG4J_INIT = false;
	
	static boolean[] onoffswitch = new boolean[1000];
	
	static Logger LOGGER = LoggerFactory.getLogger(BaseTester.class);
	public static void setSwitch(int index ,boolean onoffFlag){
		
		onoffswitch[index] = onoffFlag;
	}
	
	public static Boolean switchOn(int index){
		
		return onoffswitch[index];
	}
	
	public static void initLog4j() {
		
		if(LOG4J_INIT) {
			debug("---==: LOG4J Ready ...");
			return;
		}
		
		Properties prop = new Properties();

		prop.setProperty("log4j.rootCategory", "ERROR, CONSOLE");
		prop.setProperty("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
		prop.setProperty("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
		prop.setProperty("log4j.appender.CONSOLE.layout.ConversionPattern", "%d{HH:mm:ss,SSS} [%t] %-5p %C{1} : %m%n");
		prop.setProperty("log4j.logger.com.dcube","DEBUG");
		PropertyConfigurator.configure(prop);
		
		LOG4J_INIT = true;
	}
	
	public static void debug(String msg, String ...parameters){
		
		LOGGER.debug(msg, parameters);
	}
}
