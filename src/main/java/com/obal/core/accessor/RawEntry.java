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
package com.obal.core.accessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.obal.core.EntryInfo;
import com.obal.core.EntryKey;

/**
 * GeneralEntryInfo wrap the data without clear schema definition
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public class RawEntry extends EntryInfo{

	private static final long serialVersionUID = 1L;

	Map<String, Object> kvmap = null;
	
	/**
	 * constructor
	 * @param entityName the entry name
	 * @param key the entry key 
	 **/
	public RawEntry(String entityName, String key) {
		super(entityName, key);
	}

	/**
	 * constructor
	 * 
	 * @param key the entry key 
	 **/
	public RawEntry(EntryKey key) {
		super(key);
	}
	
	/**
	 * store key-value pair
	 * 
	 * @param key the entry attribute name
	 * @param value the entry attribute value
	 *  
	 **/
	public void put(String key,Object value){
		
		if(kvmap == null){
			
			kvmap = new HashMap<String,Object>();			
		}
		kvmap.put(key, value);
	}
	
	/**
	 * get value by key
	 * 
	 * @param key
	 * 
	 * @return object value
	 **/
	public Object get(String key){
		
		return kvmap == null? null:kvmap.get(key);
	}
	
	/**
	 * get key set
	 * 
	 * @return set of key 
	 **/
	public Set<String> getKeySet(){
		
		return kvmap.keySet();
	}
}
