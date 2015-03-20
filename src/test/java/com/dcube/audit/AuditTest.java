package com.dcube.audit;

import com.dcube.audit.AuditInfo;
import com.dcube.base.BaseTester;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.util.AuditUtils;

public class AuditTest extends BaseTester{

	public void testAudit(){
		EventDispatcher ed = EventDispatcher.getInstance();
		ed.start();
		AuditUtils.regAuditHooker();
		
		for(int i = 1; i<100; i++){
			
			AuditInfo evt = new AuditInfo("key-"+i);
			AuditUtils.doAudit(evt);
		}
		
		try {
			Thread.currentThread().sleep(600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("-------End---------");
			ed.shutdown();
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
