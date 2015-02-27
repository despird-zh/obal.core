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
package com.doccube.core.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HConnection;

import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.accessor.GenericAccessor;
import com.doccube.core.accessor.GenericContext;

/**
 * GenericAccessor base class, it will hold HConnection object. 
 **/
public abstract class HGenericAccessor extends GenericAccessor implements HConnAware{

	private static final String LOCAL_CONNECT = "_CONNECTION";
	
	public HGenericAccessor(GenericContext context) {
		super(context);
	}

	@Override
	public void setConnection(HConnection connection) {
		
		getLocalVars().get().put(LOCAL_CONNECT, connection);
	}

	@Override
	public HConnection getConnection() {
		
		HConnection conn = (HConnection)getLocalVars().get().get(LOCAL_CONNECT);
		return conn;
	}
	
	@Override
	public void release() {
		try {
			HConnection conn = getConnection();
			if (conn != null && !isEmbed())
				conn.close();
			
			super.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
