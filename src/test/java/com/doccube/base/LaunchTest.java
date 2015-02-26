package com.doccube.base;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class LaunchTest extends BlankTester{
	
	public LaunchTest(){
		
		System.out.println("constructor");
	}
	
    @Test   
    public void test003Third() {       
       
        System.out.println("test003Third");
    }
   
    @Test   
    public void test001First() {       
       
        System.out.println("test001First");
    }

    @Test   
    public void test002Second() {       
       
        System.out.println("test002Second");
    } 
    
	protected void setUp() throws Exception {
		
		System.out.println("setUp");
		initLog4j();
		super.setUp();		
	}

	protected void tearDown() throws Exception {
		
		System.out.println("tearDown");
		super.tearDown();
		
	}
}
