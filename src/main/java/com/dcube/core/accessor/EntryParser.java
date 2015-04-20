package com.dcube.core.accessor;

import java.util.ArrayList;
import java.util.List;

import com.dcube.core.EntryKey;
import com.dcube.core.TraceInfo;
import com.dcube.core.IGenericEntry.AttributeItem;
import com.dcube.exception.MetaException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityManager;

/**
 * EntryParser provides the methods to read/write data of GenericEntry object.
 * It let subclass easily to interact with GenericEntry. 
 **/
public class EntryParser {
	
	/** the generic entry */
	protected GenericEntry rawEntry = null;
	
	/**
	 * Default constructor, here not initial the rawEntry
	 * because it could be EntityEntry or AccessControlEntry or TraceableEntry
	 **/
	public EntryParser(){}
	
	/**
	 * the default constructor, which take entry as data source 
	 **/
	public EntryParser(GenericEntry gEntry){
		
		this.rawEntry = gEntry;
	}
		
	/**
	 * Get the GenericEntry object
	 **/
	public GenericEntry getGenericEntry(){
		return this.rawEntry;
	}
	
	/**
	 * Get EntryKey from GenericEntry 
	 **/
	public EntryKey getEntryKey(){
		
		if(rawEntry instanceof EntityEntry)
			return ((EntityEntry)rawEntry).getEntryKey();
		else
			throw new UnsupportedOperationException("Only EntityEntry proveide EntryKey"); 
	}
	
	/**
	 * Set EntryKey to EntityEntry 
	 **/
	public void setEntryKey(EntryKey entryKey){
		
		if(rawEntry instanceof EntityEntry)
			((EntityEntry)rawEntry).setEntryKey(entryKey);
		else
			throw new UnsupportedOperationException("Only EntityEntry proveide EntryKey"); 
	}
	
	/**
	 * Get EntityAttr list, in fact it convert AttributeItem into EntityAttr 
	 **/
	public List<EntityAttr> getAttrList(){
		
		List<AttributeItem> itemlist = this.rawEntry.getAttrItemList();
		List<EntityAttr> rtv = new ArrayList<EntityAttr>();
		for(AttributeItem item: itemlist){
			EntityAttr attr;
			try {
				attr = EntityManager.getInstance().getEntityAttr(item.entity(), item.attribute());
				rtv.add(attr);
			} catch (MetaException e) {
				
				e.printStackTrace();
			}
		
		}
		return rtv;
	}
	
	
	/**
	 * Get EntityAttr list, in fact it convert AttributeItem into EntityAttr 
	 **/
	public List<EntityAttr> getChangedAttrList(){
		
		List<AttributeItem> itemlist = this.rawEntry.getAttrItemList();
		List<EntityAttr> rtv = new ArrayList<EntityAttr>();
		for(AttributeItem item: itemlist){
			if(!item.isChanged()) // unchanged ignore
				continue;
			
			EntityAttr attr;
			try {
				attr = EntityManager.getInstance().getEntityAttr(item.entity(), item.attribute());
				rtv.add(attr);
			} catch (MetaException e) {
				
				e.printStackTrace();
			}
		
		}
		return rtv;
	}
	
	/**
	 * Get attribute of Entity
	 **/
	public EntityAttr getAttr(String entityname, String attrname){
		
		AttributeItem item = rawEntry.getAttrItem(entityname ,attrname);
		EntityAttr attr = null;
		try {
			attr = EntityManager.getInstance().getEntityAttr(item.entity(), item.attribute());
		} catch (MetaException e) {
			
			e.printStackTrace();
		}
		return attr;
	}
	
	/**
	 * Set raw generic entry 
	 **/
	public void setGenericEntry(GenericEntry rawEntry){
		 this.rawEntry = rawEntry;
	}

	/**
	 * Get trace information object 
	 **/
	public TraceInfo getTraceInfo(){
		
		if(rawEntry instanceof TraceableEntry)
			return ((TraceableEntry)rawEntry).getTraceInfo();
		else
			throw new UnsupportedOperationException("Only EntityEntry proveide EntryKey"); 
		
	}
	
	/**
	 * Set trace information object 
	 **/
	public void setTraceInfo(TraceInfo traceInfo){
		
		if(rawEntry instanceof TraceableEntry)
			((TraceableEntry)rawEntry).setTraceInfo(traceInfo);
		else
			throw new UnsupportedOperationException("Only EntityEntry proveide EntryKey"); 
	}
}
