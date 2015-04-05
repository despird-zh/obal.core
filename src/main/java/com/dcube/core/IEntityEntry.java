package com.dcube.core;

import java.util.Map;

import com.dcube.meta.EntityAttr;

/**
 * IEntryInfo provide methods to access value of row data of table.
 * <p>EntryInfo means a row data of table, usually it not wrap data cross multiple tables.</p>
 **/
public interface IEntityEntry extends IGenericEntry{

	/**
	 * Get the entry key
	 * 
	 * @return EntryKey the entry key
	 **/
	public EntryKey getEntryKey();
	
	/**
	 * Set entry key to Entry object 
	 **/
	public void setEntryKey(EntryKey entryKey);

	/**
	 * Get EntityName
	 * 
	 **/
	public String getEntityName();
	
	/**
	 * Get attribute 
	 **/
	public EntityAttr getAttr(String attrname);
	
	/**
	 * Get attribute value 
	 **/
	public <K> K getAttrValue(String attrname, Class<K> type);
	
	/**
	 * Get attribute value 
	 **/
	public Object getAttrValue(String attrname);
	
	/**
	 * Set the attribute value 
	 **/
	public void setAttrValue(String attrname, Object value);
	
	/**
	 * Get the audit verb Predicates 
	 **/
	public Map<String, Object> getAuditPredicates();
		
}
