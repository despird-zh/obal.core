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

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.AccessorBuilder;
import com.obal.core.CoreConstants;
import com.obal.core.IBaseAccessor;
import com.obal.core.security.Principal;
import com.obal.core.security.PrincipalAware;
import com.obal.exception.EntityException;
/**
 * Hbase-wise implementation of AccessorBuilder.
 * All accessors access the hbase will be created by this class
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 * @see AccessorBuilder
 * @see HEntityAccessor
 * @see HGenericAccessor
 **/
public class HAccessorBuilder extends AccessorBuilder{

	static Logger LOGGER = LoggerFactory.getLogger(HAccessorBuilder.class);
	
	private Configuration config = null;
	
	/**
	 * Default Constructor 
	 **/
	public HAccessorBuilder() throws EntityException{
		
		this(CoreConstants.BUILDER_HBASE,"com/obal/core/AccessorMap.hbase.properties");
	}
	
	/**
	 * constructor 
	 * @param builderName 
	 * @param accessorMap 
	 **/
	public HAccessorBuilder(String builderName, String accessormap) throws EntityException{
		super(builderName,accessormap);
		
		config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.zookeeper.quorum", "192.168.1.133");
		config.set("hbase.master", "192.168.1.133:60010");
		File file = new File(".");
		try {

			String path = file.getCanonicalPath();
			LOGGER.debug("hadoop.home->{}",path);
			System.setProperty("hadoop.home.dir", path + "/target/classes");

		} catch (IOException e) {
			LOGGER.error("Error when create AccessorBuilder",e);
		}
	}

	@Override
	public void assembly(Principal principal,IBaseAccessor accessor) {
		HConnection connection = null;		
		HBaseAdmin hBaseAdmin = null;
		if(accessor instanceof HConnAware){
			try {
				connection = HConnectionManager.createConnection(config);
				((HConnAware) accessor).setConnection(connection);
			} catch (IOException e) {
				LOGGER.error("Error when assembly Accessor:set HConnection",e);
			}			
		}
		
		if(accessor instanceof HAdminAware){
			try {
				hBaseAdmin = new HBaseAdmin(config);
				((HAdminAware) accessor).setAdmin(hBaseAdmin);
				
			} catch (IOException e) {
				LOGGER.error("Error when assembly Accessor:set HBaseAdmin",e);
			}			
		}
		
		if(accessor instanceof PrincipalAware){
			((PrincipalAware) accessor).setPrincipal(principal);		
		}
	}

	@Override
	public void assembly(IBaseAccessor mockupAccessor,
			IBaseAccessor... accessors) throws EntityException {
		
		HConnection connection = null;		
		HBaseAdmin hBaseAdmin = null;
		Principal principal = null;
		for(IBaseAccessor accessor:accessors){
			
			if((mockupAccessor instanceof HConnAware) 
					&& (accessor instanceof HConnAware)){
				
				connection = ((HConnAware) mockupAccessor).getConnection();
				((HConnAware) accessor).setConnection(connection);		
			}
			
			if((mockupAccessor instanceof HConnAware) 
					&& (accessor instanceof HAdminAware)){

				hBaseAdmin = ((HAdminAware) accessor).getAdmin();
				((HAdminAware) accessor).setAdmin(hBaseAdmin);
		
			}
			
			if((mockupAccessor instanceof HConnAware) 
					&& (accessor instanceof PrincipalAware)){
				principal = ((PrincipalAware) accessor).getPrincipal();
				((PrincipalAware) accessor).setPrincipal(principal);		
			}
			// Set embed flag
			accessor.setEmbed(true);
		}
	}
	
	/**
	 * Get the configuration object 
	 * 
	 * @return Configuration the configuration of Hbase Connection
	 **/
	public Configuration getConfiguration(){
		
		return this.config;
	}
}
