package com.doccube.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doccube.accessor.IAdminAccessor;
import com.doccube.accessor.IMetaGenericAccessor;
import com.doccube.accessor.TestAccessor;
import com.doccube.admin.EntityAdmin;
import com.doccube.base.BlankTester;
import com.doccube.core.EntryKey;
import com.doccube.core.accessor.EntryInfo;
import com.doccube.core.security.Principal;
import com.doccube.exception.AccessorException;
import com.doccube.exception.EntityException;
import com.doccube.meta.EntityAttr;
import com.doccube.meta.EntityConstants;
import com.doccube.meta.EntityMeta;
import com.doccube.meta.GenericEntity;
import com.doccube.meta.EntityAttr.AttrMode;
import com.doccube.meta.EntityAttr.AttrType;
import com.doccube.util.Accessors;

public class RedisTester extends BlankTester{

	public void testMain(){
		
		//self.createTestSchema();
		EntryKey key = this.doNewEntry();
		//EntryKey key = new EntryKey("obal.test","1416631407433");
		
		this.doUpdateAttr(key);
		this.doGet(key);
		this.doDelete(key);
	

	}
	

	private EntryInfo doGet(EntryKey key){
		
		TestAccessor ta = null;
		EntryInfo rentry = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try{
			ta = Accessors.getEntityAccessor("redis",princ, "obal.test");
			rentry = ta.doGetEntry(key.getKey());
			
		}catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("----end get entry");
			Accessors.closeAccessor(ta);
		}
		
		return rentry;
	}
	
	private void doDelete(EntryKey key){
		System.out.println("----start delete new entry");
		TestAccessor ta = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try{
			ta = Accessors.getEntityAccessor("redis",princ, "obal.test");
			ta.doDelEntry(key.getKey());
			
		}catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("----end delete new entry");
			Accessors.closeAccessor(ta);
		}
	}
	
	private void doUpdateAttr(EntryKey key){
		System.out.println("----start update attr");
		TestAccessor ta = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try{
			ta = Accessors.getEntityAccessor("redis",princ, "obal.test");
			
			ta.doPutEntryAttr(key.getKey(), "i_int", 2211);
			ta.doPutEntryAttr(key.getKey(), "i_double", 23.2222);
			ta.doPutEntryAttr(key.getKey(), "i_long", 23888888L);
			ta.doPutEntryAttr(key.getKey(), "i_date", new Date(System.currentTimeMillis()));
			ta.doPutEntryAttr(key.getKey(), "i_string", "SSSTUPDATE");

			List<String> strlist = new ArrayList<String>();
			strlist.add("item20");
			strlist.add("item21");
			strlist.add("item22");
			ta.doPutEntryAttr(key.getKey(), "i_list_str", strlist);
			
			List<Integer> intlist = new ArrayList<Integer>();
			intlist.add(2010);
			intlist.add(2011);
			intlist.add(2012);
			intlist.add(2014);
			ta.doPutEntryAttr(key.getKey(), "i_list_int", intlist);

			List<Date> dtlist = new ArrayList<Date>();
			dtlist.add(new Date());
			dtlist.add(new Date());
			dtlist.add(new Date());
			dtlist.add(new Date());
			ta.doPutEntryAttr(key.getKey(), "i_list_dt", dtlist);
			
			Map<String, String> strmap = new HashMap<String, String>();
			strmap.put("sk1", "str val 21");
			strmap.put("sk2", "str val 22");
			strmap.put("sk3", "str val 23");
			ta.doPutEntryAttr(key.getKey(), "i_map_str", strmap);
			
			Map<String, Integer> intmap = new HashMap<String, Integer>();
			intmap.put("ik1", 221);
			intmap.put("ik2", 222);
			intmap.put("ik3", 223);
			ta.doPutEntryAttr(key.getKey(), "i_map_int", intmap);
			
			Map<String, Date> dtmap = new HashMap<String, Date>();
			dtmap.put("dk1", new Date());
			dtmap.put("dk2", new Date());
			dtmap.put("dk3", new Date());
			ta.doPutEntryAttr(key.getKey(), "i_map_dt", dtmap);
			
		}catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("----end update attr");
			Accessors.closeAccessor(ta);
		}
	}
	
	private EntryKey doNewEntry(){
		
		System.out.println("----start create new entry");
		TestAccessor ta = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try {
			ta = Accessors.getEntityAccessor("redis",princ, "obal.test");
			EntryKey key = ta.newKey();
			EntryInfo re = new EntryInfo(key);
			re.setAttrValue("i_int", 1211);
			re.setAttrValue("i_double", 13.111);
			re.setAttrValue("i_long", 123456788888L);
			re.setAttrValue("i_date", new Date());
			re.setAttrValue("i_string", "demo string SSSSXXXX AAAALAAAA");
			
			List<String> strlist = new ArrayList<String>();
			strlist.add("item0");
			strlist.add("item1");
			strlist.add("item2");
			strlist.add("item3");
			re.setAttrValue("i_list_str", strlist);
			
			List<Integer> intlist = new ArrayList<Integer>();
			intlist.add(1010);
			intlist.add(1011);
			intlist.add(1012);
			intlist.add(1014);
			re.setAttrValue("i_list_int", intlist);

			List<Date> dtlist = new ArrayList<Date>();
			dtlist.add(new Date());
			dtlist.add(new Date());
			dtlist.add(new Date());
			dtlist.add(new Date());
			re.setAttrValue("i_list_dt", dtlist);
			
			Map<String, String> strmap = new HashMap<String, String>();
			strmap.put("sk1", "str val 1");
			strmap.put("sk2", "str val 2");
			strmap.put("sk3", "str val 3");
			re.setAttrValue("i_map_str", strmap);
			
			Map<String, Integer> intmap = new HashMap<String, Integer>();
			intmap.put("ik1", 121);
			intmap.put("ik2", 122);
			intmap.put("ik3", 123);
			re.setAttrValue("i_map_int", intmap);
			
			Map<String, Date> dtmap = new HashMap<String, Date>();
			dtmap.put("dk1", new Date());
			dtmap.put("dk2", new Date());
			dtmap.put("dk3", new Date());
			re.setAttrValue("i_map_dt", dtmap);
			
			return ta.doPutEntry(re);
			
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("----end create new entry");
			Accessors.closeAccessor(ta);
		}
		
		return null;
	}
	
	private void createTestSchema(){
		
		EntityAdmin ea = EntityAdmin.getInstance();
		Principal princ = new Principal("useracc","demouser","pwd");
		
		IAdminAccessor aa = ea.getAdminAccessor(princ);
		IMetaGenericAccessor imeta = null;
		try {
			
			EntityMeta meta = new EntityMeta("obal.test");
			meta.setSchemaClass(GenericEntity.class.getName());
			meta.setDescription("user schema descriptionxxx");
			
			EntityAttr attr = null;
			
			attr = new EntityAttr("i_int",AttrType.INTEGER,"c0","int-val");
			meta.addAttr(attr);
			attr = new EntityAttr("i_double",AttrType.DOUBLE,"c1","double-val");
			meta.addAttr(attr);
			attr = new EntityAttr("i_long",AttrType.LONG,"c1","long-val");
			meta.addAttr(attr);
			attr = new EntityAttr("i_date",AttrType.DATE,"c2","date-val");
			meta.addAttr(attr);
			attr = new EntityAttr("i_string",AttrType.STRING,"c2","str-val");
			meta.addAttr(attr);
			attr = new EntityAttr("i_list_str",AttrMode.LIST,AttrType.STRING,"c3","list-str");
			meta.addAttr(attr);
			
			attr = new EntityAttr("i_list_int",AttrMode.LIST,AttrType.INTEGER,"c3","list-int");
			meta.addAttr(attr);

			attr = new EntityAttr("i_list_dt",AttrMode.LIST,AttrType.DATE,"c3","list-dt");
			meta.addAttr(attr);
			
			attr = new EntityAttr("i_map_str",AttrMode.MAP,AttrType.STRING,"c4","map-str");
			meta.addAttr(attr);
			
			attr = new EntityAttr("i_map_int",AttrMode.MAP,AttrType.INTEGER,"c4","map-int");
			meta.addAttr(attr);

			attr = new EntityAttr("i_map_dt",AttrMode.MAP,AttrType.DATE,"c4","map-dt");
			meta.addAttr(attr);
			
			aa.createSchema("obal.test",meta.getAllAttrs());
			
			imeta = Accessors.getGenericAccessor(princ, EntityConstants.ACCESSOR_GENERIC_META);

			imeta.putEntityMeta(meta);
						
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{

			Accessors.closeAccessor(imeta,aa);
		}
	}
	
	 protected void setUp() throws Exception {  
		 initLog4j();
		EntityAdmin eadmin = EntityAdmin.getInstance();
		eadmin.loadEntityMeta();
	     super.setUp();  
	 }  
	  
	 protected void tearDown() throws Exception {  
	    
		 super.tearDown();  
	 } 
}
