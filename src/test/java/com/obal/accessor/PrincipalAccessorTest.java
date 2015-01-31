package com.obal.accessor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.obal.core.CoreManager;
import com.obal.core.EntryFilter;
import com.obal.core.accessor.TraceableEntry;
import com.obal.core.security.Principal;
import com.obal.core.security.hbase.UserAccessor;
import com.obal.exception.AccessorException;
import com.obal.exception.BaseException;
import com.obal.exception.EntityException;
import com.obal.test.BlankTester;
import com.obal.util.AccessorUtils;

public class PrincipalAccessorTest extends BlankTester{

	public void testCore(){
		
		UserAccessor pa = null;
		Principal princ = new Principal("demo1","demouser1","demopwd","demosrc");
		//princ.setKey("101001");
		try {
			Map<String,Object> groups = new HashMap<String,Object>();
			groups.put("gk1","group1");
			groups.put("gk2","group2");
			princ.setGroups(groups);
			
			Map<String,Object> roles = new HashMap<String,Object>();
			roles.put("rk1","role1");
			roles.put("rk2","role2");
			princ.setRoles(roles);
			pa = AccessorUtils.getEntityAccessor(princ, "obal.user");
			
			pa.doPutEntry(pa.toEntryInfo.convert(princ));
			
			pa.doPutEntryAttr("101001", "i_name", "newdemoname");

			List<TraceableEntry> pl = pa.doScanEntry(null);
			
			TraceableEntry princ2 = pa.doGetEntry("101001");
			System.out.println("p-name:"+pa.toPrincipal.convert(princ2).getName());
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			AccessorUtils.releaseAccessor(pa);
		}
	}
		
	protected void setUp() throws Exception {  
		initLog4j();
		CoreManager.getInstance().initial();
		CoreManager.getInstance().start();
	    super.setUp();  
	}  
	  
	protected void tearDown() throws Exception {  
	    CoreManager.getInstance().stop();
		super.tearDown();  
	} 
}
