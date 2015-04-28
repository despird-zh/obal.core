package com.dcube.index;

import com.dcube.core.EntryKey;
import com.dcube.core.hbase.HWrapperUtils;
import com.dcube.meta.EntityAttr;

/**
 * IndexInfo holds the indexable attribute change for further operation on index table. 
 * 
 * @author despird
 * @version 0.1 2014-4-2
 **/
public class IndexInfo {

	/**
	 * Operation Mode 
	 **/
	public static enum IndexMode{
		Update, // update
		Remove; // remove
	}
	
	EntryKey entryKey ;
	EntityAttr attr;
	Object oldValue;
	Object newValue;
	IndexMode mode = IndexMode.Update;
	
	/**
	 * Build with key, attribute and old value, default for update mode.
	 * @param key 
	 * @param attr
	 * @param oldValue 
	 **/
	public IndexInfo(String key, EntityAttr attr, Object oldValue){
		this.attr = attr;
		this.entryKey = new EntryKey(attr.getEntityName(),key);
		this.oldValue = oldValue;
	}
	
	/**
	 * Build with key, attribute and old value
	 * @param key 
	 * @param attr
	 * @param oldValue 
	 **/
	public IndexInfo(IndexMode mode, String key, EntityAttr attr, Object oldValue){
		this.attr = attr;
		this.entryKey = new EntryKey(attr.getEntityName(),key);
		this.oldValue = oldValue;
		this.mode = mode;
	}
	
	/**
	 * Build with key, attribute, value and value flag,default for update mode.
	 * @param key 
	 * @param attr
	 * @param value 
	 * @param newFlag true:set value as new; false:set value as old
	 **/
	public IndexInfo(String key, EntityAttr attr, Object value, boolean newFlag){
		this.attr = attr;
		this.entryKey = new EntryKey(attr.getEntityName(),key);
		
		if(newFlag){
			this.newValue = value;
		}else{
			this.oldValue = value;
		}
	}
	
	/**
	 * Build with key, attribute and old value , new value,default for update mode.
	 * @param key 
	 * @param attr
	 * @param oldValue 
	 * @param newValue
	 **/
	public IndexInfo(String key, EntityAttr attr, Object oldValue, Object newValue){
		this.attr = attr;
		this.entryKey = new EntryKey(attr.getEntityName(),key);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/**
	 * Get the Index table row key of old value 
	 **/
	public EntryKey getOldIndexKey(){
		
		EntryKey rtv = HWrapperUtils.toIndexEntryKey(attr, oldValue);		
		return rtv;
	}
	
	/**
	 * Get the Index table row key of new value 
	 **/
	public EntryKey getNewIndexKey(){
		
		EntryKey rtv = HWrapperUtils.toIndexEntryKey(attr, newValue);		
		return rtv;
	}
	
	/**
	 * Get the entity attribute object 
	 **/
	public EntityAttr getEntityAttr(){
		
		return this.attr;
	}
	
	/**
	 * Get the entry key object 
	 **/
	public EntryKey getEntryKey(){
		return this.entryKey;
	}
	
	/**
	 * Get the old value 
	 **/
	public Object getOldValue(){
		return this.oldValue;
	}
	
	/**
	 * Get the new value 
	 **/
	public Object getNewValue(){
		
		return this.newValue;
	}
	
	/**
	 * Get the name of entity 
	 **/
	public String getEntityName(){
		
		return this.entryKey.getEntityName();
	}
	
	/**
	 * The index mode  
	 **/
	public IndexMode mode(){
		return this.mode;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(entryKey)
		.append("|").append(attr.getAttrName())
		.append("|oldv:").append(oldValue)
		.append("|newv:").append(newValue);
		
		return buf.toString();
	}
}
