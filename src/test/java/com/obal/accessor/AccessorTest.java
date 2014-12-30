package com.obal.accessor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.obal.admin.EntityAdmin;
import com.obal.admin.IAdminAccessor;
import com.obal.core.EntryKey;
import com.obal.core.accessor.RawEntry;
import com.obal.core.security.Principal;
import com.obal.exception.AccessorException;
import com.obal.exception.EntityException;
import com.obal.meta.AttrMode;
import com.obal.meta.AttrType;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityConstants;
import com.obal.meta.EntityManager;
import com.obal.meta.EntityMeta;
import com.obal.meta.GenericEntity;
import com.obal.meta.accessor.IMetaGenericAccessor;
import com.obal.test.BlankTester;
import com.obal.util.AccessorUtils;

public class AccessorTest extends BlankTester{

	public void testMain(){
		//createTestSchema();
		EntryKey key = this.doNewEntry();
		//EntryKey key = new EntryKey("obal.test","1416631407433");
		
		this.doUpdateAttr(key);
		this.doGet(key);
		this.doDelete(key);
		
	}
	
	private RawEntry doGet(EntryKey key){
		
		TestAccessor ta = null;
		RawEntry rentry = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try{
			ta = AccessorUtils.getEntityAccessor(princ, "obal.test");
			rentry = ta.doGetEntry(key.getKey());
			
		}catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("----end get entry");
			AccessorUtils.releaseAccessor(ta);
		}
		
		return rentry;
	}
	
	private void doDelete(EntryKey key){
		System.out.println("----start delete new entry");
		TestAccessor ta = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try{
			ta = AccessorUtils.getEntityAccessor(princ, "obal.test");
			ta.doDelEntry(key.getKey());
			
		}catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("----end delete new entry");
			AccessorUtils.releaseAccessor(ta);
		}
	}
	
	private void doUpdateAttr(EntryKey key){
		System.out.println("----start update attr");
		TestAccessor ta = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try{
			ta = AccessorUtils.getEntityAccessor(princ, "obal.test");
			
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
			AccessorUtils.releaseAccessor(ta);
		}
	}
	
	private EntryKey doNewEntry(){
		
		System.out.println("----start create new entry");
		TestAccessor ta = null;
		Principal princ = new Principal("useracc","demouser","pwd");
		try {
			ta = AccessorUtils.getEntityAccessor(princ, "obal.test");
			EntryKey key = ta.newKey();
			RawEntry re = new RawEntry(key);
			re.put("i_int", 1211);
			re.put("i_double", 13.111);
			re.put("i_long", 123456788888L);
			re.put("i_date", new Date());
			re.put("i_string", "demo string SSSSXXXX AAAALAAAA");
			
			List<String> strlist = new ArrayList<String>();
			strlist.add("item0");
			strlist.add("item1");
			strlist.add("item2");
			strlist.add("item3");
			re.put("i_list_str", strlist);
			
			List<Integer> intlist = new ArrayList<Integer>();
			intlist.add(1010);
			intlist.add(1011);
			intlist.add(1012);
			intlist.add(1014);
			re.put("i_list_int", intlist);

			List<Date> dtlist = new ArrayList<Date>();
			dtlist.add(new Date());
			dtlist.add(new Date());
			dtlist.add(new Date());
			dtlist.add(new Date());
			re.put("i_list_dt", dtlist);
			
			Map<String, String> strmap = new HashMap<String, String>();
			strmap.put("sk1", "str val 1");
			strmap.put("sk2", "str val 2");
			strmap.put("sk3", "str val 3");
			re.put("i_map_str", strmap);
			
			Map<String, Integer> intmap = new HashMap<String, Integer>();
			intmap.put("ik1", 121);
			intmap.put("ik2", 122);
			intmap.put("ik3", 123);
			re.put("i_map_int", intmap);
			
			Map<String, Date> dtmap = new HashMap<String, Date>();
			dtmap.put("dk1", new Date());
			dtmap.put("dk2", new Date());
			dtmap.put("dk3", new Date());
			re.put("i_map_dt", dtmap);
			
			return ta.doPutEntry(re);
			
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("----end create new entry");
			AccessorUtils.releaseAccessor(ta);
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
			
			imeta = AccessorUtils.getGenericAccessor(princ, EntityConstants.ENTITY_META_GENERIC);

			imeta.putEntityMeta(meta);
						
		} catch (AccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{

			AccessorUtils.releaseAccessor(imeta,aa);
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
