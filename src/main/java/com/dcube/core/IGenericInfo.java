package com.dcube.core;

import java.util.List;

import com.dcube.meta.EntityAttr;

/**
 * IGenericInfo is the basic interface for row level data wrapping.
 * 
 * @author despird-zh
 * @version 0.1 2014-3-1
 * 
 **/
public interface IGenericInfo {

	/**
	 * Get the attribute of specified attribute
	 **/
	public EntityAttr getAttr(String entityname,String attrname);

	/**
	 * Get the attribute List, if every attributeItem not set entityattr,
	 * it will return null. 
	 **/
	public List<EntityAttr> getAttrs();
		
	/**
	 * Get the value of attribute as specified type
	 * @param entityname
	 * @param attrname 
	 **/
	public <K> K getAttrValue(String entityname,String attrname, Class<K> type);
	
	/**
	 * Get the value of attribute as Object
	 * @param entityname
	 * @param attrname 
	 **/
	public Object getAttrValue(String entityname,String attrname);
	
	/**
	 * Set attribute value
	 **/
	public void setAttrValue(EntityAttr attribute, Object value);
}
