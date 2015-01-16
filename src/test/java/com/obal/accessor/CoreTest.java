package com.obal.accessor;

import com.obal.core.CoreManager;
import com.obal.test.BlankTester;

public class CoreTest extends BlankTester{

	public void testCore(){
		
		System.out.println("--------core test");
	}
		
	protected void setUp() throws Exception {  
		initLog4j();
		CoreManager.getInstance().initial();
		CoreManager.getInstance().start();
	    super.setUp();  
	}  
	  
	protected void tearDown() throws Exception {  
	    CoreManager.getInstance().stop();
		super.tearDown();  
	} 
}
