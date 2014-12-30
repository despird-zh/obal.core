package com.obal.aop;

import com.obal.test.BlankTester;

public class CacheTest extends BlankTester{
	
	public void testCache(){
		
		CacheTestAccessor cta = new CacheTestAccessor();
		
		cta.doPutDemo1("s1111","t1111");
		String n = cta.doPutDemo2("s2222","t222");
		System.out.println(n);
		String m = cta.doGetDemo1("nn","mm");
		System.out.println(m);
		cta.doDelDemo1();
	}
	
	 protected void setUp() throws Exception {  
	     initLog4j();  
	     super.setUp();  
	 }  
	  
	 protected void tearDown() throws Exception {  
	    
		 super.tearDown();  
	 } 
}
