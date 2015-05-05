package com.dcube.base;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.dcube.admin.EntityAdmin;
import com.dcube.admin.EntitySetup;
import com.dcube.core.AccessorDetector;
import com.dcube.core.AccessorFactory;
import com.dcube.exception.BaseException;
import com.dcube.launcher.CoreFacade;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class CoreMainTest extends BaseTester{
	
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
    	
    	debug("---==: Test 000 initial ");
		
		try {
			CoreFacade.initial();
			CoreFacade.start();
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public void test001SetupTables() {     
    	if(!switchOn(1))
    		return;
    	
    	debug("---==: Test 001 schema create ");
    	//step1 detect all the accessor builder
    	AccessorFactory.loadAccessorBuilder();
    	//setp2 setup all the tables.
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
