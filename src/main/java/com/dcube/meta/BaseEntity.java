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

package com.dcube.meta;

import com.dcube.core.EntryKey;
import com.dcube.core.security.Principal;
import com.dcube.exception.MetaException;

/**
 * BaseEntity is responsible for generate Entry Key and decide the actual schema name before persistence or scan
 * <p>BaseEntity is managed by EntityManager</p>
 * 
 * 
 * @author despird-zh
 * @since 0.1
 * @see EntityManager
 **/
public abstract class BaseEntity{

	protected EntityMeta meta = null;
	
	/**
	 * Constructor 
	 * @param EntityMeta The Entity MetaData 
	 **/
	public BaseEntity(EntityMeta meta){
		
		this.meta = meta;
	}
	
	/**
	 * Get list of all schemas for entity. the schema list is retrieved from EntityMeta
	 * 
	 **/
	public String getSchema()throws MetaException{
		
		if(null == this.meta) {
			throw new MetaException("Cann't get schema list because of null meta.");
		}else{
			
			return this.meta.getSchema();
		}
	}
	
	/**
	 * Get the entity schema by entry key
	 * @param entryKey the entry key
	 * @return String the schema name of entity 
	 **/
	public String getSchema(String entryKey)throws MetaException{
		
		return getSchema(null, entryKey);
	}
	
	/**
	 * Get the schema name  
	 **/
	public String getSchema(Principal principal,String entryKey)throws MetaException{
		
		EntryKey key = new EntryKey(meta.getEntityName(), entryKey);
		return getSchema(principal, key);
	}
	
	/**
	 * Get the schema name, it indict the physical location to store entry data, eg. the table name
	 * @return String the schema name
	 **/
	public abstract String getSchema(Principal principal,EntryKey entryKey)throws MetaException;
	
	/**
	 * Generate new key with parameters
	 * @return EntryKey the schema name
	 **/
	public abstract EntryKey newEntryKey(Principal principal,Object... parameter)throws MetaException;
	
	/**
	 * Generate new key
	 * @return EntryKey the schema name
	 **/
	public abstract EntryKey newEntryKey(Principal principal)throws MetaException;
	
	/**
	 * Get the schema name in byte[]
	 * @param principal the principal
	 * @param entryKey the key of entry 
	 * 
	 * @see getSchema 
	 **/
	public byte[] getSchemaBytes(Principal principal,EntryKey entryKey)throws MetaException{
		
		String schema = getSchema(principal,entryKey);
		return schema == null? null: schema.getBytes();
	}
	
	/**
	 * Get Index schema name 
	 * @param principal the principal
	 * @param entryKey the key of entry 
	 * @param String the coupled index schema name
	 **/
	public String getIndexSchema(Principal principal,EntryKey entryKey)throws MetaException{
		String schema = getSchema(principal,entryKey);
		return schema + EntityConstants.ENTITY_INDEX_POSTFIX;
	}
	
	/**
	 * Get entry meta object
	 * @return EntryMeta the entry meta object
	 **/
	public EntityMeta getEntityMeta(){
		
		return this.meta;
	}
	
	/**
	 * Set EntityMeta to entity
	 * @param meta the entity meta 
	 **/
	public void setEntityMeta(EntityMeta meta){
		
		this.meta = meta;
	}
	
	/**
	 * Get the entry name
	 * @return String entry name  
	 **/
	public String getEntityName(){
		
		return meta.getEntityName();
	}

	/**
	 * Convert string key to EntryKey 
	 * @param entryKey key string value
	 * @return EntryKey
	 **/
	public EntryKey getEntryKey(String entryKey){
		
		return new EntryKey(meta.getEntityName(), entryKey);
	}
	
	/**
	 * Get the index key prefix 
	 * @param entrykey the calculation parameter
	 * @param attr the calculation parameter 
	 **/
	public String getIndexKeyPrefix(EntryKey entrykey, EntityAttr attr){
		return "";
	}
}
