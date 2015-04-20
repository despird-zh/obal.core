package com.dcube.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.dcube.base.BaseTester;
import com.dcube.core.security.Principal;

public class PrincipalTest extends BaseTester{
	
	public void testPrincipal2Json(){
		
		Principal princ = new Principal("acnt1","name1","passwd1","sourc1");

		Map<String,Object> groups = new HashMap<String,Object>();
		groups.put("gk1","group1");
		groups.put("gk2","group2");
		//princ.setGroups(groups);
		
		Map<String,Object> roles = new HashMap<String,Object>();
		roles.put("rk1","role1");
		roles.put("rk2","role2");
		//princ.setRoles(roles);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String val = mapper.writeValueAsString(princ);
			System.out.println(val);
			Principal princ2 = mapper.readValue(val, Principal.class);
			System.out.println(princ2.getName());
			System.out.println(princ2.getAccount());
			System.out.println(princ2.getSource());
			System.out.println(princ2.getPassword());


			//Map<String,Object> grps = princ2.getGroups();
			//System.out.println("grps:"+grps);
			//Map<String,Object> rls = princ2.getRoles();
			//System.out.println("rls:"+rls);
			
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
	
	public void testPrincipalWrite(){
		
		
	}
	
	protected void setUp() throws Exception {  
	     initLog4j();  
	     super.setUp();  
	}  
	  
	protected void tearDown() throws Exception {  
	    
		super.tearDown();  
	}  
}
