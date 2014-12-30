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

import com.obal.core.EntryKey;
import com.obal.exception.MetaException;

public class GenericEntity extends BaseEntity{
		
	public GenericEntity(EntityMeta meta) {
		super(meta);
	}

	@Override
	public String getSchema(EntryKey key) {
		
		return getEntityMeta().getEntityName();
	}

	@Override
	public EntryKey newKey(Object... parameter) throws MetaException {
		
		return newKey();
	}

	@Override
	public EntryKey newKey() throws MetaException {
		
		String key = String.valueOf(System.currentTimeMillis());		
		return new EntryKey(getEntityMeta().getEntityName(),key);
	}
	
}
