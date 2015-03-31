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
package com.dcube.accessor.hbase;

import com.dcube.audit.AuditInfo;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.hbase.HEntryWrapper;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityConstants;

public class AuditEAccessor extends HEntityAccessor<EntityEntry>{

	public AuditEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_AUDIT);		
	}

	@Override
	public HEntryWrapper<EntityEntry> getEntryWrapper() {
		// TODO Auto-generated method stub
		return null;
	}

}
