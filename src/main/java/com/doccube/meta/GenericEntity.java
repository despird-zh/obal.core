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
package com.doccube.meta;

import com.doccube.core.EntryKey;
import com.doccube.core.security.Principal;
import com.doccube.exception.MetaException;

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

	@Override
	public String getSchema(Principal principal, EntryKey key) throws MetaException{
		
		return super.getSchema();
	}

	@Override
	public EntryKey newKey(Principal principal, Object... parameter) throws MetaException {
		
		return newKey(principal);
	}

	@Override
	public EntryKey newKey(Principal principal) throws MetaException {
		
		String key = String.valueOf(System.currentTimeMillis());		
		return new EntryKey(getEntityMeta().getEntityName(),key);
	}
	
}
