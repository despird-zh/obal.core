package com.doccube.exception;

import com.doccube.base.BlankTester;
import com.doccube.exception.EntityException;

public class ExceptionTest extends BlankTester {

	
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
