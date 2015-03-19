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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntryInfo;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.hbase.HEntryWrapper;
import com.dcube.core.hbase.HRawWrapper;
import com.dcube.meta.EntityConstants;

public class AttrInfoAccessor extends HEntityAccessor<EntryInfo>{

	public static Logger LOGGER = LoggerFactory.getLogger(AttrInfoAccessor.class);
	
	public AttrInfoAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_ATTR);		
	}

	@Override
	public HEntryWrapper<EntryInfo> getEntryWrapper() {
		
		HRawWrapper wrapper = new HRawWrapper();

		return wrapper;
	}
	
}
