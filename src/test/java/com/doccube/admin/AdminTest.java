package com.doccube.admin;

import org.junit.Test;

import com.doccube.admin.EntitySetup;
import com.doccube.base.BlankTester;

public class AdminTest extends BlankTester{
	
	public void setup(){
		
		EntitySetup ei = new EntitySetup();
		ei.setup();
		ei.purge();
	}

	 protected void setUp() throws Exception {  
	     initLog4j();  
	     super.setUp();  
	 }  
	  
	 protected void tearDown() throws Exception {  
	    
		 super.tearDown();  
	 }
}
