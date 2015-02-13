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
package com.doccube.audit.accessor;

import com.doccube.core.EntryKey;
import com.doccube.core.security.Principal;
import com.doccube.exception.MetaException;
import com.doccube.meta.BaseEntity;
import com.doccube.meta.EntityMeta;

public class AuditEntity extends BaseEntity{

	public AuditEntity(EntityMeta meta) {
		super(meta);
	}

	@Override
	public String getSchema(Principal principal, EntryKey key) {
		
		return "autit.info";
	}

	@Override
	public EntryKey newKey(Principal principal, Object... parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntryKey newKey(Principal principal) throws MetaException {
		// TODO Auto-generated method stub
		return null;
	}
}
