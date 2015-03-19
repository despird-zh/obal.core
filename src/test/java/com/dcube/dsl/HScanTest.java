package com.dcube.dsl;

import com.dcube.base.BaseTester;
import com.dcube.dsl.HScan;

public class HScanTest extends BaseTester{
	
	public void testScan(){
		
		HScan<String> hscan = new HScan<String>("table1");
		hscan.filters("AND")
				.filter("a1").ne("v1")
				.filter("a2").eq("v2")
			.attribute("a1")
			.attribute("a2")
			.attribute("a3");
		
		String json = hscan.toJson();
		
		HScan<String> hscan1 = new HScan<String>("table1");
		hscan1.fromJson(json);
	}
}
