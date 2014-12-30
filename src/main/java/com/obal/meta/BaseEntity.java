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

package com.obal.meta;

import java.util.List;

import com.obal.core.EntryKey;
import com.obal.core.security.Principal;
import com.obal.core.security.PrincipalAware;
import com.obal.exception.MetaException;

/**
 * EntrySchema is resonsible for generate Entry Key and decide the actual schema name before persistence or scan
 * <p>Because EntrySchema implements PrincipalAware, it can hold principal to enable some principal sensitive operation
 * eg. @link #getSchema . The principal is store in ThreadLocal, ie. it is thread safe</p>
 * <p>EntrySchema is managed by EntrySchemaManager</p>
 * 
 * @author G.Obal
 * @since 0.1
 * @see com.obal.core.security.PrincipalAware
 * @see EntityManager
 **/
public abstract class BaseEntity implements PrincipalAware{
	
	/** threadlocal */
	private ThreadLocal<Principal> localPrincipal = new ThreadLocal<Principal>();
	
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
	public List<String> getSchemas()throws MetaException{
		
		if(null == this.meta) {
			throw new MetaException("Cann't get schema list because of null meta.");
		}else{
			
			return this.meta.getSchemas();
		}
	}
	
	/**
	 * Get the schema name  
	 **/
	public String getSchema(String entryKey){
		
		EntryKey key = new EntryKey(this.meta.getEntityName(), entryKey);
		return getSchema(key);
	}
	
	/**
	 * Get the schema name, it indict the physical location to store entry data, eg. the table name
	 * @return String the schema name
	 **/
	public abstract String getSchema(EntryKey entryKey);
	
	/**
	 * Generate new key with parameters
	 * @return EntryKey the schema name
	 **/
	public abstract EntryKey newKey(Object... parameter)throws MetaException;
	
	/**
	 * Generate new key
	 * @return EntryKey the schema name
	 **/
	public abstract EntryKey newKey()throws MetaException;
	
	/**
	 * Get the schema name in byte[]
	 * @see getSchema 
	 **/
	public byte[] getSchemaBytes(EntryKey entryKey){
		
		return getSchema(entryKey) == null? null: getSchema(entryKey).getBytes();
	}
	
	/**
	 * Get entry meta object
	 * @return EntryMeta the entry meta object
	 **/
	public EntityMeta getEntityMeta(){
		
		return this.meta;
	}
	
	/**
	 * Get the entry name
	 * @return String entry name  
	 **/
	public String getEntityName(){
		
		return this.meta.getEntityName();
	}
	
	@Override
	public void setPrincipal(Principal principal){
		
		this.localPrincipal.set(principal);
	}
	
	@Override
	public Principal getPrincipal(){
		
		return this.localPrincipal.get();
	}
	
	@Override
	public void clearPrincipal(){
		
		this.localPrincipal.remove();
	}
}
