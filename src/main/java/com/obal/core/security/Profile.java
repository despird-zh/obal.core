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
package com.obal.core.security;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Profile store the setting of principal 
 *  
 **/
public class Profile {
	
	private Map<String, Object> settings = null;
	
	/**
	 * Default constructor 
	 **/
	public Profile(){
		
		settings = new HashMap<String, Object>();
	}

	/**
	 * Default constructor 
	 **/
	@JsonCreator
	public Profile(@JsonProperty("settings") Map<String, Object> setting){
		
		this.settings = setting;
	}

	/**
	 * Store key value pair
	 **/
	public void setSetting(String key, Object value){
		
		this.settings.put(key, value);
	}
	
	/**
	 * Get vlaue of setting by key
	 * @param key the key of setting 
	 **/
	@JsonProperty("settings")
	@SuppressWarnings("unchecked")
	public <K> K getSetting(String key){
		
		return (K)this.settings.get(key);
	}
	
	/**
	 * Get setting map  
	 **/
	public Map<String, Object> getSettings(){
		
		return this.settings;
	}
	
	/**
	 * Get setting map  
	 **/
	public void setSettings(Map<String, Object> settings){
		
		this.settings = settings;
	}
}
