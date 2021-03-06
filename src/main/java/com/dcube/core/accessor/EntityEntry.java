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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dcube.core.EntryKey;
import com.dcube.core.IEntityEntry;

/**
 * EntryInfo is the base class for all classes that be used to wrap the entry row
 * It only provide methods to access the entry key. User will extends it as need.
 * It not indicate the Traceable or AccessControllable feature.
 * 
 * @author despird
 * @version 0.1 2014-2-1 
 * 
 * @see GenericEntry
 * @see AccessControlEntry
 * @see TraceableEntry
 * 
 **/
public class EntityEntry extends GenericEntry implements IEntityEntry{

	private EntryKey entryKey = null;
	
	/**
	 * Default constructor
	 **/
	public EntityEntry (){
		
		super();
	}
	
	/**
	 * Constructor with entity name and key 
	 **/
	public EntityEntry (String entityName,String key){
		
		super();
		entryKey = new EntryKey(entityName,key);
	}
	
	/**
	 * Constructor with EntryKey
	 * @param entryKey the entry key
	 * @return EntityEntry
	 **/
	public EntityEntry (EntryKey entryKey){
		
		super();
		this.setEntryKey(entryKey);
	}
	
	/**
	 * Get the entity name
	 * 
	 * @return String the entity name 
	 **/
	@Override
	public String getEntityName(){
		
		return getEntryKey().getEntityName();
	}
	
	/**
	 * Get the entry key
	 * @return EntryKey 
	 **/
	@Override
	public EntryKey getEntryKey() {
		
		return this.entryKey;
	}

	/**
	 * Set the entry key
	 * @param entryKey the EntryKey object 
	 **/
	@Override
	public void setEntryKey(EntryKey entryKey) {
		
		this.entryKey = entryKey;
	}

	/**
	 * Get the entity attribute
	 * @param attrname the attribute name
	 * @return EntityAttr 
	 **/
	@Override
	public AttributeItem getAttrItem(String attrname) {
		
		return getAttrItem(entryKey.getEntityName(), attrname);
	}

	/**
	 * Get the attribute value via attribute name
	 * @param attrname the attribute name
	 * @param type the class of target type
	 * @return K value casted with specified type 
	 **/
	@Override
	public <K> K getAttrValue(String attrname, Class<K> type) {
		
		return getAttrValue(entryKey.getEntityName(), attrname,type);
	}

	/**
	 * Get attribute value
	 * @param attrname the attribute name
	 * @return Object the value object 
	 **/
	@Override
	public Object getAttrValue(String attrname) {
		
		return getAttrValue(entryKey.getEntityName(), attrname);
	}

	/**
	 * Set attribute value
	 * 
	 * @param attrname the attribute name
	 * @param value the value object
	 *  
	 **/
	@Override
	public void setAttrValue(String attrname, Object value) {
		
		super.setAttrValue(entryKey.getEntityName(), attrname, value);
	}

	/**
	 * Change attribute value
	 * 
	 * @param attrname the attribute name
	 * @param value the value object
	 *  
	 **/
	@Override
	public void changeAttrValue(String attrname, Object value) {
		
		super.changeAttrValue(entryKey.getEntityName(), attrname, value);
	}
	
	/**
	 * Get the Predicates of audit info
	 * @return Map<String, Object>
	 **/
	@Override
	public Map<String, Object> getAuditPredicates() {
		
		List<AttributeItem> items = super.getChangedAttrItemList();
		items = items == null? super.getAttrItemList():items;
		Map<String, Object> predicates = new HashMap<String, Object>();
		if(items == null)
			 return predicates;
		
		for(AttributeItem item : items){
			predicates.put(item.attribute(), item.value());
		}
		return predicates;
	}

	/**
	 * Copy the original source object to current object.
	 * 
	 * @param originSource the source object 
	 **/
	public void copy(EntityEntry originSource) {
		
		super.copy(originSource);
		EntryKey tkey = originSource.getEntryKey();
		this.entryKey = (EntryKey)tkey.clone();
	}	

}
