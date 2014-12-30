package com.obal.accessor;

import java.util.Properties;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.log4j.PropertyConfigurator;

import com.obal.core.hbase.HMapRedHttpScan;
import com.obal.test.BlankTester;

public class HMapRepScanTest extends BlankTester{

	public void test() {
    	
		Properties prop = new Properties();

		prop.setProperty("log4j.rootCategory", "ERROR, CONSOLE");
		prop.setProperty("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
		prop.setProperty("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
		prop.setProperty("log4j.appender.CONSOLE.layout.ConversionPattern", "%d{HH:mm:ss,SSS} [%t] %-5p %C{1} : %m%n");
		
		PropertyConfigurator.configure(prop);
    	String source = "obal.meta.attr";
    	Scan scan = new Scan();
    	scan.addFamily("c0".getBytes());
    	HMapRedHttpScan hmrScan = new HMapRedHttpScan(source, scan);
    	hmrScan.init();
    	try {
			hmrScan.mapredScan("tttt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 protected void setUp() throws Exception {  
		 initLog4j();
		//EntityAdmin eadmin = EntityAdmin.getInstance();
		//eadmin.loadEntityMeta();
	     super.setUp();  
	 }  
	  
	 protected void tearDown() throws Exception {  
	    
		 super.tearDown();  
	 } 
}
