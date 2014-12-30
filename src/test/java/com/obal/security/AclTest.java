package com.obal.security;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.obal.core.CoreConstants;
import com.obal.core.security.AclPrivilege;
import com.obal.core.security.EntryAce;
import com.obal.core.security.EntryAcl;
import com.obal.test.BlankTester;

public class AclTest extends BlankTester{

	public void testAce2Json(){
		ObjectMapper mapper = new ObjectMapper();
		EntryAce ea = new EntryAce(CoreConstants.ACE_TYPE_USER,"demouser",AclPrivilege.READ,"x1","s2","t3");
	
		try {
			String val = mapper.writeValueAsString(ea);
			System.out.println(val);
			
			EntryAce ea1 = mapper.readValue(val, EntryAce.class);
			System.out.println("------Read value:");
			System.out.println(ea1.type());
			System.out.println(ea1.name());
			System.out.println(ea1.privilege());
			System.out.println(ea1.permissions());
			
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testAcl2Json(){
		ObjectMapper mapper = new ObjectMapper();
		
		EntryAce ea = new EntryAce(CoreConstants.ACE_TYPE_USER,"demouser",AclPrivilege.READ,"x1","s2","t3");
		EntryAce ea1 = new EntryAce(CoreConstants.ACE_TYPE_ROLE,"drole1",AclPrivilege.EXECUTE,"1x1","s2","t3");
		EntryAce ea2 = new EntryAce(CoreConstants.ACE_TYPE_GROUP,"dgrp2",AclPrivilege.WRITE,"2x1","s2","t3");
		EntryAce ea3 = new EntryAce(CoreConstants.ACE_TYPE_USER,"duser3",AclPrivilege.BROWSE,"3x1","s2","t3");
		
		EntryAcl eal = new EntryAcl("acl1",ea,ea1,ea2,ea3);
		
		String val;
		try {
			System.out.println("------Java Object 2 Json:");
			val = mapper.writeValueAsString(eal);
			System.out.println(val);
			
			System.out.println("------Json String 2 Java Object:");
			EntryAcl eacl2 = mapper.readValue(val, EntryAcl.class);
			System.out.println(eacl2.name());
			List<EntryAce> aces = eacl2.allAces();
			for(EntryAce ace:aces){
				System.out.print("------ACE value:");
				System.out.print("/type:"+ace.type());
				System.out.print("/name"+ace.name());
				System.out.print("/priv"+ace.privilege());
				System.out.println("/perms"+ace.permissions());
			}
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
