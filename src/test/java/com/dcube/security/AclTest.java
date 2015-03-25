package com.dcube.security;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.dcube.base.BaseTester;
import com.dcube.core.CoreConstants;
import com.dcube.core.security.AclPrivilege;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;

public class AclTest extends BaseTester{

	
	protected void setUp() throws Exception {  
	     initLog4j();  
	     super.setUp();  
	}  
	  
	protected void tearDown() throws Exception {  
	    
		super.tearDown();  
	}  
}
