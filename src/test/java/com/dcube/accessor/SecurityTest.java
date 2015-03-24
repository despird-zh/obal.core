package com.dcube.accessor;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.dcube.accessor.hbase.GroupInfoEAccessor;
import com.dcube.accessor.hbase.PrincipalGAccessor;
import com.dcube.accessor.hbase.UserInfoEAccessor;
import com.dcube.base.BaseTester;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.security.Principal;
import com.dcube.exception.BaseException;
import com.dcube.launcher.CoreLauncher;
import com.dcube.meta.EntityConstants;
import com.dcube.util.AccessorUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class SecurityTest extends BaseTester{

	public void test000Initial() throws Exception{
		CoreLauncher.initial();
		CoreLauncher.start();
	}
	
	public void Dtest001CreatePrincipal(){
		
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
			pa = AccessorUtils.getEntityAccessor(princ, EntityConstants.ENTITY_USER);
			EntryKey key = pa.newKey();
			// get converter
			IEntryConverter<TraceableEntry,Principal > converter = pa.getEntryConverter(Principal.class);
			TraceableEntry princ0 = converter.toSource(princ);
			princ0.setEntryKey(key);
			pa.doPutEntry(princ0);
			
			pa.doPutEntryAttr(key.getKey(), "i_name", "newdemoname");			
			EntryCollection<TraceableEntry> pl = pa.doScanEntry(null);
			pl.getAttrList();
			TraceableEntry princ2 = pa.doGetEntry(key.getKey());
			System.out.println("p-name:"+converter.toTarget(princ2).getName());
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			AccessorUtils.closeAccessor(pa);
		}
	}

	public void test002UpdateAttr(){
		
		PrincipalGAccessor pa = null;
		UserInfoEAccessor uea = null;
		Principal princ = new Principal("demo1","demouser1","demopwd","demosrc");
		//princ.setKey("101001");
		try {
			pa = AccessorUtils.getEntityAccessor(princ, EntityConstants.ACCESSOR_GENERIC_USER);
			Principal princ2 = pa.getPrincipalByAccount("demo1");
			uea = AccessorFactory.buildEntityAccessor(princ, EntityConstants.ENTITY_USER);

			uea.doPutEntryAttr(princ2.getId(), "i_name", "newUserName");
			
			princ2 = pa.getPrincipalByAccount("demo1");
			
			Assert.assertEquals(princ2.getName(), "newUserName");
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			AccessorUtils.closeAccessor(pa,uea);
		}
	}
	
	public void test003Group(){
		
		GroupInfoEAccessor pa = null;
		try {
			Principal princ2 = new Principal("demo2","demouser1","demopwd","demosrc");
			pa = AccessorFactory.buildEntityAccessor(princ2, EntityConstants.ACCESSOR_ENTITY_GROUP);
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			AccessorUtils.closeAccessor(pa);
		}
	}
	
	public void test999End() throws Exception{
		
		  CoreLauncher.stop();
	}
	
	protected void setUp() throws Exception {  
		initLog4j();
	}
	  
	protected void tearDown() throws Exception {  
	  
		super.tearDown();  
	} 
}
