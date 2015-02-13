package com.doccube.admin;

import com.doccube.admin.EntitySetup;
import com.doccube.test.BlankTester;

public class AdminTest extends BlankTester{
	
	public void testInitializer(){
		
		EntitySetup ei = new EntitySetup();
		ei.setup();
		
	}

	 protected void setUp() throws Exception {  
	     initLog4j();  
	     super.setUp();  
	 }  
	  
	 protected void tearDown() throws Exception {  
	    
		 super.tearDown();  
	 }  
}
