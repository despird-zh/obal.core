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
package com.obal.core.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HConnection;

import com.obal.core.accessor.GenericAccessor;

/**
 * Hbase General Accessor base class, it will hold HConnection object. 
 **/
public abstract class HGenericAccessor extends GenericAccessor implements HConnAware{

	private HConnection conn;	

	@Override
	public void setConnection(HConnection connection) {
		this.conn = connection;
	}

	@Override
	public HConnection getConnection() {
		
		return conn;
	}
	
	@Override
	public void release() {
		try {
			if (conn != null && !isEmbed())
				this.conn.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
