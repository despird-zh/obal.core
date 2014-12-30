package com.obal.aop;

import com.obal.cache.aop.CacheableGet;
import com.obal.core.EntryKey;

public class CacheTestAccessor {
	
	DemoBean dp = new DemoBean("CTA String --s-s");
	
	//@Auditable(verb="putdemo", object = "", subject = "")
	public void doPutDemo1(String a1,String a2){
		
		System.out.println("--------calling doPutDemo1");
	}

	//@Auditable(verb="putdemo")
	public String doPutDemo2(String a1,String a2){
		
		System.out.println("--------calling doPutDemo2");
		return "===doPutDemo2 result===";
	}
	
	@CacheableGet(entrykey="key",entity="entityname")
	public String doGetDemo1(String key,String entityname){
				
		System.out.println("--------calling doGetDemo1");
		return "===doGetDemo1Value===";
	}
	
	public String doGetDemo2(EntryKey entrykey){
		
		
		System.out.println("--------calling doGetDemo1");
		return "===doGetDemo2Value===";
	}
	
	public void doDelDemo1(){
		
		System.out.println("--------calling doDelDemo1");
	}
	
	public DemoBean getdp(){
		
		return dp;
	}
}
