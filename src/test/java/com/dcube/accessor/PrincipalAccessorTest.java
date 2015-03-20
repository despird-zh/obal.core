package com.dcube.accessor;

import java.util.HashMap;
import java.util.Map;

import com.dcube.accessor.hbase.UserInfoEAccessor;
import com.dcube.base.BaseTester;
import com.dcube.core.CoreLauncher;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.security.Principal;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.util.Accessors;

public class PrincipalAccessorTest extends BaseTester{

	public void testCore(){
		
		UserInfoEAccessor pa = null;
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
			pa = Accessors.getEntityAccessor(princ, EntityConstants.ACCESSOR_ENTITY_USER);
			// get converter
			IEntryConverter<TraceableEntry,Principal > converter = pa.getEntryConverter(Principal.class);
			pa.doPutEntry(converter.toSource(princ));
			
			pa.doPutEntryAttr("101001", "i_name", "newdemoname");			
			EntryCollection<TraceableEntry> pl = pa.doScanEntry(null);
			
			TraceableEntry princ2 = pa.doGetEntry("101001");
			System.out.println("p-name:"+converter.toTarget(princ2).getName());
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			Accessors.closeAccessor(pa);
		}
	}
		
	protected void setUp() throws Exception {  
		initLog4j();
		CoreLauncher.initial();
		CoreLauncher.start();
	    super.setUp();  
	}  
	  
	protected void tearDown() throws Exception {  
	    CoreLauncher.stop();
		super.tearDown();  
	} 
}
