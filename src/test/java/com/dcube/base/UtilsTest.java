package com.dcube.base;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.dcube.core.AccessorFactory;
import com.dcube.util.AccessorDetector;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class UtilsTest extends BaseTester{
	
	static{
		debug("---==: initial log4j");
		initLog4j();
		setSwitch(0,false);// prepare
		setSwitch(1,true);// prepare
		setSwitch(2,false);// load meta
		setSwitch(3,false);// principal test

		setSwitch(999,false); // drop schema, clear
	}
	
    public void test000Initial() {     
    	if(!switchOn(0))
    		return;
    	
    	debug("---==: Test 000 AccessorDetector ");
		List<Class<?>> classes = AccessorDetector.getClassesForPackage("com.esotericsoftware.kryo");
		for(Class<?> clz : classes){
			
			System.out.println(clz.getName());
		}
    }
    
    public void test001Initial() {     
    	if(!switchOn(1))
    		return;
    	
    	String t = "group:000101";
    	
    	String[] ps = StringUtils.split(t, ":");
    	for(int i =0; i< ps.length; i++){
    		System.out.println("-->"+ps[i]);
    	}
    }

}
