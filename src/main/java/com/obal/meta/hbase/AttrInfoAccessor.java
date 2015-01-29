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
package com.obal.meta.hbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.accessor.AccessorContext;
import com.obal.core.accessor.EntryInfo;
import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.hbase.HEntityAccessor;
import com.obal.core.hbase.HRawWrapper;
import com.obal.meta.BaseEntity;

public class AttrInfoAccessor extends HEntityAccessor<EntryInfo>{

	public static Logger LOGGER = LoggerFactory.getLogger(AttrInfoAccessor.class);
	
	public AttrInfoAccessor(AccessorContext context) {
		super(context);
	}

	@Override
	public HEntryWrapper<EntryInfo> getEntryWrapper() {
		
		HRawWrapper wrapper = new HRawWrapper();

		return wrapper;
	}
	
}
