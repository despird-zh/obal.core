package com.doccube.accessor;

import com.doccube.base.BaseTester;
import com.doccube.core.CoreManager;

public class CoreTest extends BaseTester{

	public void testCore(){
		
		System.out.println("--------core test");
	}
		
	protected void setUp() throws Exception {  
		initLog4j();
		//CoreManager.getInstance().initial();
		//CoreManager.getInstance().start();
	    super.setUp();  
	}  
	  
	protected void tearDown() throws Exception {  
	    //CoreManager.getInstance().stop();
		super.tearDown();  
	} 
}
