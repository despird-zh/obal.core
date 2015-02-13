package com.doccube.core;

import java.util.Collection;

import com.doccube.meta.EntityAttr;

public interface IGenericInfo {

	public EntityAttr getAttr(String attrname);
	
	public Collection<EntityAttr> getAttrs();
		
	public <K> K getAttrValue(String attrName, Class<K> type);
	
	public Object getAttrValue(String attrName);
	
	public void setAttrValue(String attrName, Object value);
}
