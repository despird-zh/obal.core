package com.doccube.accessor;

import com.doccube.core.CoreManager;
import com.doccube.test.BlankTester;

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
