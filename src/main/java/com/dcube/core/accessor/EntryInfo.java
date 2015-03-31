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
import com.dcube.core.IEntryInfo;
import com.dcube.core.accessor.GenericInfo.AttributeItem;
import com.dcube.meta.EntityAttr;

/**
 * EntryInfo is the base class for all classes that be used to wrap the entry row
 * It only provide methods to access the entry key. User will extends it as need.
 * It not indicate the Traceable or AccessControllable feature.
 * 
 * @author despird
 * @version 0.1 2014-2-1 
 * 
 * @see GenericInfo
 * @see AccessControlEntry
 * @see TraceableEntry
 * 
 **/
public class EntryInfo extends GenericInfo implements IEntryInfo{

	private EntryKey entryKey = null;
	
	/**
	 * Constructor with entity name and key 
	 **/
	public EntryInfo (String entityName,String key){
		
		super();
		entryKey = new EntryKey(entityName,key);
	}
	
	/**
	 * Constructor with EntryKey
	 **/
	public EntryInfo (EntryKey entryKey){
		
		super();
		this.setEntryKey(entryKey);
	}
	
	@Override
	public String getEntityName(){
		
		return getEntryKey().getEntityName();
	}
	
	@Override
	public EntryKey getEntryKey() {
		
		return this.entryKey;
	}

	@Override
	public void setEntryKey(EntryKey entryKey) {
		
		this.entryKey = entryKey;
	}

	@Override
	public EntityAttr getAttr(String attrname) {
		
		return getAttr(entryKey.getEntityName(), attrname);
	}

	@Override
	public <K> K getAttrValue(String attrname, Class<K> type) {
		
		return getAttrValue(entryKey.getEntityName(), attrname,type);
	}

	@Override
	public Object getAttrValue(String attrname) {
		
		return getAttrValue(entryKey.getEntityName(), attrname);
	}

	public void setAttrValue(String attrname, Object value) {
		
		super.setAttrValue(entryKey.getEntityName(), attrname, value);
	}

	@Override
	public Map<String, String> getAuditPredicates() {
		
		List<AttributeItem> changedItems = super.getChangedItems();
		Map<String, String> predicates = new HashMap<String, String>();
		for(AttributeItem item : changedItems){
			predicates.put(item.attrname, item.currentVal.toString());
		}
		return predicates;
	}	
	
}
