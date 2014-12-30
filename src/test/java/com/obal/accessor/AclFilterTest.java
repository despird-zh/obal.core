package com.obal.accessor;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.exceptions.DeserializationException;

import com.obal.core.CoreManager;
import com.obal.core.hbase.HAclFilter;
import com.obal.core.security.Principal;
import com.obal.test.BlankTester;

public class AclFilterTest extends BlankTester{
	
	public void testSerialize(){
		Principal princ = new Principal("demo1","demouser1","demopwd","demosrc");
		princ.setKey("101001");

		Map<String, Object> groups = new HashMap<String, Object>();
		groups.put("gk1", "group1");
		groups.put("gk2", "group2");
		princ.setGroups(groups);

		Map<String, Object> roles = new HashMap<String, Object>();
		roles.put("rk1", "role1");
		roles.put("rk2", "role2");
		princ.setRoles(roles);
		
		HAclFilter afilter = new HAclFilter("c0".getBytes(),"acl".getBytes());
		afilter.setPrincipal(princ);
		
		byte[] aclbytes = afilter.toByteArray();
		
		try {
			HAclFilter afilter2 = HAclFilter.parseFrom(aclbytes);
		} catch (DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("--------Serialize test");
	}
	
	
	protected void setUp() throws Exception {  
		initLog4j();

	    super.setUp();  
	}  
	  
	protected void tearDown() throws Exception {  

		super.tearDown();  
	} 
}
