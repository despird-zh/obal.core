/*
 * Licensed to the G.Obal under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  G.Obal licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 */
package com.dcube.core.accessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dcube.core.IGenericEntry;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

/**
 * EntryInfo is the base class for all classes that be used to wrap the entry row
 * It only provide methods to access the entry key. User will extends it as need.
 * It not indicate the Traceable or AccessControllable feature.
 * 
 * @author despird
 * @version 0.1 2014-2-1 
 * 
 * @author despird
 * @version 0.2 2014-4-3 Remove EntityAttr reference in AttributeItem
 * 
 **/
public class GenericEntry implements IGenericEntry{

	private Map<String, AttributeItem> itemMap = null;
	
	public GenericEntry (){
		itemMap = new HashMap<String,AttributeItem> ();
	}
	
	/**
	 * Get the attribute object via entity name and attribute name
	 * 
	 * @param entityname the entity name
	 * @param attrname the attribute name 
	 * 
	 * @return EntityAttr 
	 **/
	public AttributeItem getAttrItem(String entityname, String attrname) {
		
		AttributeItem item = itemMap.get(entityname + EntityConstants.NAME_SEPARATOR + attrname);

		return item;
	}

	/**
	 * Get the attribute list 
	 * @return List<EntityAttr>
	 **/
	public List<AttributeItem> getAttrItemList() {
		
		List<AttributeItem> rtv = new ArrayList<AttributeItem>(itemMap.values());
		
		return rtv;
	}

	/**
	 * Get the changed AttributeItem list
	 **/
	public List<AttributeItem> getChangedAttrItemList() {
		
		List<AttributeItem> rtv = new ArrayList<AttributeItem>(itemMap.values());
		for(int i = rtv.size()-1; i >=0;i--){
			if(!rtv.get(i).isChanged())
				rtv.remove(i);
		}
		
		return rtv.size() == 0? null:rtv;
	}
	
	/**
	 * Get the value of attribute and return with casted object.
	 * 
	 * @param entityname the entity name
	 * @param attrname the attribute name
	 * @param targetType the type of target
	 * 
	 * @return K type to cast the value
	 **/
	@SuppressWarnings("unchecked")
	public <K> K getAttrValue(String entityname, String attrname, Class<K> targetType) {

		AttributeItem e = itemMap.get(entityname + EntityConstants.NAME_SEPARATOR + attrname);
		if(null == e)
			return null;
		Object value = e.value();
		if(null!=value && targetType.isAssignableFrom(value.getClass())){
			
			return (K) value;
		}
		return null;
	}

	/**
	 * Get the value of specified attribute
	 * @param entityname the entity name
	 * @param attrname the attribute name
	 * 
	 * @return Object the value object
	 **/
	public Object getAttrValue(String entityname, String attrname){
		
		AttributeItem e = itemMap.get(entityname + EntityConstants.NAME_SEPARATOR + attrname);
		if(null == e)
			return null;
		
		return e.value();
	}
	
	/**
	 * Set the attribute with value
	 * 
	 * @param attribute the attribute object
	 * @param value the value object
	 * 
	 **/
	public void setAttrValue(EntityAttr attribute, Object value) {
		
		AttributeItem item = itemMap.get(attribute.getFullName());
		
		if(item == null){
			item = new AttributeItem(attribute.getEntityName(),
					attribute.getAttrName(), 
					value);
			itemMap.put(item.getFullName(),item);
		}
		else
			item.setNewValue(value);
				
	}
	
	/**
	 * Set the attribute with value
	 * 
	 * @param entityname the entity name
	 * @param attrname the attribute name
	 * @param value the value object
	 * 
	 **/
	public void setAttrValue(String entityname, String attrname, Object value) {
		
		AttributeItem item = itemMap.get(entityname + EntityConstants.NAME_SEPARATOR + attrname);
		
		if(item == null){
			item = new AttributeItem(entityname,attrname, value);
			itemMap.put(item.getFullName(),item);
		}
		else
			item.setNewValue(value);
		
	}
	
	/**
	 * Get the item map
	 * @return Map<String, AttributeItem> the map of AttributeItem
	 **/
	protected Map<String, AttributeItem> getItemMap(){
		
		return this.itemMap;
	}

	/**
	 * Copy the original source object to current object.
	 * 
	 * @param originSource the source object 
	 **/
	public void copy(GenericEntry originSource) {
		
		Map<String, AttributeItem> itemMap = originSource.getItemMap();
		this.itemMap.clear();
		for(Map.Entry<String, AttributeItem> e: itemMap.entrySet()){
			
			AttributeItem newItem = (AttributeItem)e.getValue().clone();
			this.itemMap.put(e.getKey(), newItem);
		}
	}

}
