package com.doccube.base;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.doccube.admin.EntityAdmin;
import com.doccube.admin.EntitySetup;
import com.doccube.core.AccessorFactory;
import com.doccube.util.AccessorDetector;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class LaunchTest extends BaseTester{
	
	static{
		debug("---==: initial log4j");
		initLog4j();
		setSwitch(0,true);// prepare
		setSwitch(1,false);// prepare
		setSwitch(2,false);// load meta
		setSwitch(3,false);// principal test

		setSwitch(999,false); // drop schema, clear
	}
	
    public void test000Initial() {     
    	if(!switchOn(0))
    		return;
    	
    	debug("---==: Test 000 initial ");
    	AccessorFactory.getDefaultBuilder();
		List<Class<?>> classes = AccessorDetector.getClassesForPackage("com.esotericsoftware.kryo");
		for(Class<?> clz : classes){
			
			System.out.println(clz.getName());
		}
    }
    
    public void test001Prepare() {     
    	if(!switchOn(1))
    		return;
    	
    	debug("---==: Test 001 schema create ");
    	EntitySetup ei = new EntitySetup();
		ei.setup();		
    }
 
    public void test002() {       
    	if(!switchOn(2))
    		return;
    	
        debug("---==: Test 002 schema load meta info ");
        EntityAdmin eadmin = EntityAdmin.getInstance();
        eadmin.loadEntityMeta();
    }
    
    public void test003() {       
    	if(!switchOn(3))
    		return;
    	
        debug("test003Third");
    }
    
    public void test999End() {       
    	if(!switchOn(999))
    		return;
    	
    	debug("---==: Test 999 schema drop");
    	EntitySetup ei = new EntitySetup();
    	ei.purge();
    }
}
