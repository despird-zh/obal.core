package com.doccube.base;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.doccube.admin.EntityAdmin;
import com.doccube.admin.EntitySetup;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class LaunchTest extends BlankTester{
	
	static{
		System.out.println("---==: initial log4j");
		initLog4j();
		setSwitch(1,false);// prepare
		setSwitch(2,false);// load meta
		setSwitch(3,false);// principal test

		setSwitch(999,false); // drop schema, clear
	}

    public void test003() {       
    	if(!switchOn(3))
    		return;
    	
        System.out.println("test003Third");
    }
    
    public void test001Prepare() {     
    	if(!switchOn(1))
    		return;
    	
    	System.out.println("---==: Test 001 schema create ");
    	EntitySetup ei = new EntitySetup();
		ei.setup();		
    }
 
    public void test002() {       
    	if(!switchOn(2))
    		return;
    	
        System.out.println("---==: Test 002 schema load meta info ");
        EntityAdmin eadmin = EntityAdmin.getInstance();
        eadmin.loadEntityMeta();
    }
      
    public void test999End() {       
    	if(!switchOn(999))
    		return;
    	
    	System.out.println("---==: Test 999 schema drop");
    	EntitySetup ei = new EntitySetup();
    	ei.purge();
    }
}
