package com.doccube.base;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.doccube.admin.EntityAdmin;
import com.doccube.admin.EntitySetup;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class LaunchTest extends BlankTester{
		
    @Test   
    public void T003() {       
       
        System.out.println("test003Third");
    }
    
    public void test001Prepare() {       
    	System.out.println("---==: Test schema create ");
    	EntitySetup ei = new EntitySetup();
		ei.setup();		
    }
 
    public void test002() {       
       
        System.out.println("---==: Test schema load meta info ");
        EntityAdmin eadmin = EntityAdmin.getInstance();
        eadmin.loadEntityMeta();
    }
      
    public void Itest999End() {       
       
    	System.out.println("---==: Test schema drop");
    	EntitySetup ei = new EntitySetup();
    	ei.purge();
    }
    
	protected void setUp() throws Exception {
		
		System.out.println("---==: Before launch initial log4j");
		initLog4j();
		super.setUp();		
	}

	protected void tearDown() throws Exception {
		
		System.out.println("---==: End launch test");
		super.tearDown();
		
	}
}
