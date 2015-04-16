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
 * @see RawEntry
 * @see RawAccessControlEntry
 * @see RawTraceableEntry
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
	public EntityAttr getAttr(String entityname, String attrname) {
		
		AttributeItem item = itemMap.get(entityname + EntityConstants.NAME_SEPARATOR + attrname);
		return item == null? null : item.attribute;
	}

	/**
	 * Get the attribute list 
	 * @return List<EntityAttr>
	 **/
	public List<EntityAttr> getAttrs() {
		
		List<EntityAttr> rtv = new ArrayList<EntityAttr>();
		
		for(Map.Entry<String, AttributeItem> e:itemMap.entrySet()){
			if(e.getValue() != null)
				rtv.add(e.getValue().attribute);
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
		Object value = e.currentVal;
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
		
		return e.currentVal;
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
			item = new AttributeItem(attribute, value);
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
	 * Get the AttributeItem object
	 * @param entityname the entity name
	 * @param attrname the attribute name
	 * @return  AttributeItem 
	 **/
	protected AttributeItem getAttributeItem(String entityname, String attrname){
		
		return itemMap.get(entityname + EntityConstants.NAME_SEPARATOR + attrname);
	}
	
	/**
	 * Get the changed item list
	 * @return  List<AttributeItem>
	 **/
	public List<AttributeItem> getChangedItems(){
		
		List<AttributeItem> rtv = new ArrayList<AttributeItem>();
		
		for(Map.Entry<String, AttributeItem> e:itemMap.entrySet()){
			if(e.getValue() != null && e.getValue().isChanged())
				rtv.add(e.getValue());
		}
		
		return rtv;
	}
	
	/**
	 * Inner class to wrap value and attribute 
	 **/
	public static class AttributeItem implements Cloneable{
		
		public AttributeItem(String entityname, String attrname, Object value){
			
			this.entityname = entityname;
			this.attrname = attrname;
			this.currentVal = value;
			
		}
		
		public AttributeItem(EntityAttr attribute, Object value){
			
			this.attribute = attribute;
			this.entityname = attribute.getEntityName();
			this.attrname = attribute.getAttrName();
			this.currentVal = value;
		}
		
		public String getFullName(){
			
			return this.entityname + EntityConstants.NAME_SEPARATOR + this.attrname;
		}
		
		public EntityAttr attribute = null;
		public String entityname = null;
		public String attrname = null;
		public Object currentVal = null;
		private Object originVal = null;
		private boolean changed = false;
		
		public void setNewValue(Object newVal){
			if(!changed){
				this.originVal = this.currentVal;// save original value
				changed = true;
			}
			this.currentVal = newVal;
		}
		
		public boolean isChanged(){
			return changed;
		}
		
		public Object getOriginalValue(){
			return this.originVal;
		}
		
		@Override
	    public Object clone() {
	       
			AttributeItem rtv = new AttributeItem(this.entityname,
					this.attrname,
					this.currentVal);
			
			rtv.attribute = this.attribute;
			rtv.changed = this.changed;
			rtv.originVal = this.originVal;
			
			return rtv;
	    }
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
