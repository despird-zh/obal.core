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
package com.obal.core;

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
public abstract class EntryInfo extends EntryKey{
	
	/**
	 * Constructor with entity name and entry key 
	 * 
	 * @param entityName the entity name
	 * @param key the entry key
	 **/
	public EntryInfo(String entityName, String key){
		
		super(entityName, key);
	}

	/**
	 * Constructor with entry key 
	 * 
	 * @param key the entry key
	 **/
	public EntryInfo(EntryKey key){
		
		super(key.getEntityName(), key.getKey());
	}
	
	/**
	 * Get the entry key
	 * 
	 * @return EntryKey the entry key
	 **/
	public EntryKey getEntryKey(){
		
		return new EntryKey(getEntityName(), getKey());
	}
	
}
