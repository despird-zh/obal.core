package com.obal.common;

import java.util.Collection;
import java.util.List;

import com.obal.core.EntryKey;
import com.obal.meta.EntityAttr;

public interface IEntryInfo {

	public EntityAttr getAttr(String attrname);
	
	public Collection<EntityAttr> getAttrs();
	
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
	
	public <K> K getAttrValue(String attrName, Class<K> type);
	
	public void setAttrValue(String attrName, Object value);
}
