package com.dcube.accessor;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.dcube.accessor.hbase.GroupInfoEAccessor;
import com.dcube.accessor.hbase.PrincipalGAccessor;
import com.dcube.accessor.hbase.RoleInfoEAccessor;
import com.dcube.accessor.hbase.UserInfoEAccessor;
import com.dcube.base.BaseTester;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.security.Principal;
import com.dcube.core.security.UserGroup;
import com.dcube.core.security.UserRole;
import com.dcube.exception.BaseException;
import com.dcube.launcher.CoreFacade;
import com.dcube.meta.EntityConstants;
import com.dcube.util.AccessorUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class SecurityTest extends BaseTester{

	public void test000Initial() throws Exception{
		CoreFacade.initial();
		CoreFacade.start();
	}
	
	public static String groupname = "demogroup";
	public void Dtest001GroupCRUD()throws Exception{
		
		GroupInfoEAccessor ga = null;
		Principal princ = new Principal("admin","demouser1","adminpwd","demosrc");
		try{
			
			ga = AccessorUtils.getEntityAccessor(princ, EntityConstants.ENTITY_USER_GROUP);
			UserGroup ug = new UserGroup(groupname);
			IEntryConverter<TraceableEntry,UserGroup > converter = ga.getEntryConverter(UserGroup.class);
			TraceableEntry grpentry = converter.toSource(ug);
			EntryKey key = ga.newKey();
			grpentry.setEntryKey(key);
			ga.doPutEntry(grpentry, false);
			
			TraceableEntry grpentry2 = ga.doGetEntry(key.getKey());	
			UserGroup ug2 = converter.toTarget(grpentry2);
			
			Assert.assertEquals("New Group success", ug.name(), ug2.name());
		
		}finally{
			
			AccessorUtils.closeAccessor(ga);
		}
		
	}
	
	public static String rolename = "demorole";
	public void Dtest002RoleCRUD()throws Exception{
		
		RoleInfoEAccessor ra = null;
		Principal princ = new Principal("admin","demouser1","adminpwd","demosrc");
		try{
			
			ra = AccessorUtils.getEntityAccessor(princ, EntityConstants.ENTITY_USER_ROLE);
			UserRole ur = new UserRole(rolename);
			IEntryConverter<TraceableEntry,UserRole > converter = ra.getEntryConverter(UserRole.class);
			TraceableEntry grpentry = converter.toSource(ur);
			EntryKey key = ra.newKey();
			grpentry.setEntryKey(key);
			ra.doPutEntry(grpentry,false);
			
			TraceableEntry grpentry2 = ra.doGetEntry(key.getKey());	
			UserRole ur2 = converter.toTarget(grpentry2);
			
			Assert.assertEquals("New Group success", ur.name(), ur2.name());
		
		}finally{
			
			AccessorUtils.closeAccessor(ra);
		}
		
	}
	
	public void Dtest003CreatePrincipal()throws Exception{
		
		UserInfoEAccessor pa = null;
		Principal princ = new Principal("demo1","demouser1","demopwd","demosrc");
		//princ.setKey("101001");
		try {
			Set<String> groups = new HashSet<String>();
			groups.add("group1");
			groups.add("group2");
			princ.setGroups(groups);
			
			Set<String> roles = new HashSet<String>();
			roles.add("role1");
			roles.add("role2");
			princ.setRoles(roles);
			pa = AccessorUtils.getEntityAccessor(princ, EntityConstants.ENTITY_USER);
			EntryKey key = pa.newKey();
			// get converter
			IEntryConverter<TraceableEntry,Principal > converter = pa.getEntryConverter(Principal.class);
			TraceableEntry princ0 = converter.toSource(princ);
			princ0.setEntryKey(key);
			pa.doPutEntry(princ0,false);
			
			pa.doPutEntryAttr(key.getKey(), "i_name", "newdemoname");			
			EntryCollection<TraceableEntry> pl = pa.doScanEntry(null);
			pl.getAttrList();
			TraceableEntry princ2 = pa.doGetEntry(key.getKey());
			System.out.println("p-name:"+converter.toTarget(princ2).getName());
			
		} finally{
			
			AccessorUtils.closeAccessor(pa);
		}
	}

	public void test004GetPrincipal()throws Exception{
		
		ISecurityGAccessor pa = null;
		Principal princ = new Principal("demo1","demouser1","demopwd","demosrc");
		//princ.setKey("101001");
		try {
			
			pa = (ISecurityGAccessor)AccessorUtils.getGenericAccessor(princ, EntityConstants.ACCESSOR_GENERIC_SECURITY);
			Principal p1 = pa.getPrincipalByAccount("demo1");
			System.out.println(p1.getName());
		} finally{
			
			AccessorUtils.closeAccessor(pa);
		}
	}
	
	public void Dtest002UpdateAttr(){
		
		PrincipalGAccessor pa = null;
		UserInfoEAccessor uea = null;
		Principal princ = new Principal("demo1","demouser1","demopwd","demosrc");
		//princ.setKey("101001");
		try {
			pa = AccessorUtils.getGenericAccessor(princ, EntityConstants.ACCESSOR_GENERIC_USER);
			Principal princ2 = pa.getPrincipalByAccount("demo1");
			
			uea = AccessorFactory.buildEntityAccessor(princ, EntityConstants.ENTITY_USER);

			uea.doPutEntryAttr(princ2.getEntryKey().getKey(), "i_name", "newUserName");
			
			princ2 = pa.getPrincipalByAccount("demo1");
			
			Assert.assertEquals(princ2.getName(), "newUserName");
			
		} catch (BaseException e) {
			
			e.printStackTrace();
		}finally{
			
			AccessorUtils.closeAccessor(pa,uea);
		}
	}
	
	public void Dtest003Group(){
		
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
		
		  CoreFacade.stop();
	}
	
	protected void setUp() throws Exception {  
		initLog4j();
	}
	  
	protected void tearDown() throws Exception {  
	  
		super.tearDown();  
	} 
}
