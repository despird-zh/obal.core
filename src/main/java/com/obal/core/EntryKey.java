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
 * EntryKey wrap the key information 
 **/
public class EntryKey {

	private String key = null;
	private String entityName = null;
	
	/**
	 * constructor
	 * 
	 * @param entityName the name of entity
	 * @param key then entry key
	 **/
	public EntryKey(String entityName,String key){
		this.entityName = entityName;
		this.key = key;
	}
	
	public String getEntityName() {
		
		return entityName;
	}

	public void setEntityName(String entityName) {
		
		this.entityName = entityName;
	}
	
	public String getKey(){
		
		return this.key;
	}
	
	public void setKey(String key){
		
		this.key = key;
	}	
	
	public byte[] getKeyBytes(){
		
		if(this.key == null) return null;
		return getKey().getBytes();
	}

	public void setKeyBytes(byte[] key){
		
		setKey(new String(key));
	}
}
