package com.dcube.exception;

import com.dcube.base.BaseTester;
import com.dcube.exception.EntityException;

public class ExceptionTest extends BaseTester {

	
	public void testException(){
		
		try{
			
			throw new EntityException("ERR01",new Exception("sdf"),"[p1]","[p2]");		

		}catch(EntityException e){
			e.printStackTrace();
		}
		try{

		throw new EntityException("ERR02","[p1]","[p2]");		
		
		}catch(EntityException e){
			e.printStackTrace();
		}
	}

	 protected void setUp() throws Exception {  
	     initLog4j();  
	     super.setUp();  
	 }  
	  
	 protected void tearDown() throws Exception {  
	    
		 super.tearDown();  
	 }  
}
