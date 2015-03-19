package com.dcube.base;

import java.util.List;

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
    	
    	try {
			Class.forName("com.esotericsoftware.kryo.serializers.ClosureSerializer");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    }
    
    public static void main(String[] ars){
    	UtilsTest t = new UtilsTest();
    	t.test001Initial();
    }
}
