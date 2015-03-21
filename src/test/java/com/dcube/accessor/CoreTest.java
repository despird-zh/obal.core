package com.dcube.accessor;

import com.dcube.base.BaseTester;
import com.dcube.launcher.CoreLauncher;

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
