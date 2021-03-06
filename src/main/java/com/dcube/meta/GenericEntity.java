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
 * GenericEntity implements the entity schema methods.
 * <p>If schema name or entity entry key is decided by principal, should implements new class 
 * base on BaseEntity, to customize the newKey and getSchema methods.<p>
 * 
 * @author despird-zh
 * @version 0.1 2014-2-1
 * @see EntityManager
 * @see BaseEntity
 **/
public class GenericEntity extends BaseEntity{
		
	public GenericEntity(EntityMeta meta) {
		super(meta);
	}

	/**
	 * Get the schema name 
	 * @param principal the schema calculation input
	 * @param key the schema calculation input 
	 **/
	@Override
	public String getSchema(Principal principal, EntryKey key) throws MetaException{
		
		return super.getSchema();
	}

	/**
	 * We new entry key by default, some the key calculation need principal
	 * @param principal the calculation input
	 * @param parameter the calculation input, used for override method.
	 **/
	@Override
	public EntryKey newEntryKey(Principal principal, Object... parameter) throws MetaException {
		
		return newEntryKey(principal);
	}

	/**
	 * We new entry key by default, some the key calculation need principal
	 * @param principal the calculation input
	 **/
	@Override
	public EntryKey newEntryKey(Principal principal) throws MetaException {
		
		String key = String.valueOf(System.currentTimeMillis());		
		return new EntryKey(getEntityMeta().getEntityName(),key);
	}
	
}
