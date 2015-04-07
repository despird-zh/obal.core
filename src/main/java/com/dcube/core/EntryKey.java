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
package com.dcube.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * EntryKey wrap the key information, it holds the entity name and key data.
 * 
 * @author despird
 * @version 0.1 2014-3-4
 **/
public class EntryKey implements Cloneable{

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
	
	/**
	 * Get entity name 
	 **/
	public String getEntityName() {
		
		return entityName;
	}

	/**
	 * Set the entity name
	 **/
	public void setEntityName(String entityName) {
		
		this.entityName = entityName;
	}
	
	/**
	 * Get the key
	 **/
	public String getKey(){
		
		return this.key;
	}
	
	/**
	 * Set the key 
	 **/
	public void setKey(String key){
		
		this.key = key;
	}	
	
	/**
	 * Get the key bytes 
	 **/
	public byte[] getKeyBytes(){
		
		if(this.key == null) return null;
		return getKey().getBytes();
	}

	/**
	 * Set the key bytes 
	 **/
	public void setKeyBytes(byte[] key){
		
		setKey(new String(key));
	}
	
	@Override
	public String toString(){
		
		return entityName + CoreConstants.VALUE_SEPARATOR + key;
	}
	
	@Override
    public Object clone() {
		
		EntryKey newOne = new EntryKey(entityName,key);
		return newOne;
	}
	
	@Override
	public int hashCode() {
		
		return new HashCodeBuilder(17, 37)
			.append(this.entityName)
			.append(this.key).toHashCode();
			
	}
}
