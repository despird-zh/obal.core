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
package com.doccube.accessor.hbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doccube.core.AccessorFactory;
import com.doccube.core.CoreConstants;
import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.accessor.EntryInfo;
import com.doccube.core.hbase.HEntityAccessor;
import com.doccube.core.hbase.HEntryWrapper;
import com.doccube.core.hbase.HRawWrapper;
import com.doccube.meta.EntityConstants;

public class AttrInfoAccessor extends HEntityAccessor<EntryInfo>{

	public static Logger LOGGER = LoggerFactory.getLogger(AttrInfoAccessor.class);
	
	static{
		AccessorFactory.registerAccessor(CoreConstants.BUILDER_HBASE, new AttrInfoAccessor(null));
	}
	
	public AttrInfoAccessor(AccessorContext context) {
		super(EntityConstants.ACCESSOR_ENTITY_ATTR,context);
		
	}

	@Override
	public HEntryWrapper<EntryInfo> getEntryWrapper() {
		
		HRawWrapper wrapper = new HRawWrapper();

		return wrapper;
	}
	
}
