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
package com.dcube.core.hbase;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.AccessorBuilder;
import com.dcube.core.CoreConfigs;
import com.dcube.core.CoreConstants;
import com.dcube.core.IBaseAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.exception.EntityException;
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
	public HAccessorBuilder() throws AccessorException{
		
		super(CoreConstants.BUILDER_HBASE);
		initial(); // initialize hbase access 
		loadAccessors(); // load accessors
	}
	
	/**
	 * Initial necessary resources for data accessing.
	 *  
	 * @param builderName 
	 * @param accessorMap 
	 **/
	private void initial() throws AccessorException{

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
			throw new AccessorException("Fail initial builder:{}",e, CoreConstants.BUILDER_HBASE);
		}		
		
	}

	/**
	 * Load Accessor classes 
	 *  
	 **/
	private void loadAccessors(){
		
		// detect the accessor classes under package
		String packagename = CoreConfigs.getString(CoreConstants.CONFIG_ACCESSOR_PACKAGE + CoreConstants.BUILDER_HBASE,"com.dcube.accessor.hbase");
		if(packagename.contains(",")){
			
			String[] packages = packagename.split(",");
			for(String pkg: packages){
				// detect package accessor classes
				detectAccessors(pkg);
			}
		}else{
			// detect package accessor classes
			detectAccessors(packagename);
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

	}

	@Override
	public void assembly(IBaseAccessor mockupAccessor,
			IBaseAccessor... accessors) throws EntityException {
		
		HConnection connection = null;		
		HBaseAdmin hBaseAdmin = null;
		for(IBaseAccessor accessor:accessors){
			
			if((mockupAccessor instanceof HConnAware) 
					&& (accessor instanceof HConnAware)){
				
				connection = ((HConnAware) mockupAccessor).getConnection();
				((HConnAware) accessor).setConnection(connection);		
			}
			
			if((mockupAccessor instanceof HAdminAware) 
					&& (accessor instanceof HAdminAware)){

				hBaseAdmin = ((HAdminAware) accessor).getAdmin();
				((HAdminAware) accessor).setAdmin(hBaseAdmin);
		
			}

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
