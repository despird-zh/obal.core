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
package com.obal.admin.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.admin.IAdminAccessor;
import com.obal.core.hbase.HAdminAware;
import com.obal.core.hbase.HGenericAccessor;
import com.obal.exception.AccessorException;
import com.obal.meta.EntityAttr;

public class AdminAccessor extends HGenericAccessor implements IAdminAccessor,HAdminAware{

	Logger LOGGER = LoggerFactory.getLogger(AdminAccessor.class);
	private HBaseAdmin admin = null;
	
	@Override
	public void createSchema(String schemaName, List<EntityAttr> attrs) throws AccessorException {
		
		HBaseAdmin hadmin = this.getAdmin();
		try{
			if(hadmin.tableExists(schemaName)){
				LOGGER.error("Schema[{}] already existed, ignore further operation.",schemaName);
				return;
			}
			ArrayList<String> temp = new ArrayList<String>();
			@SuppressWarnings("deprecation")
			HTableDescriptor tableDescriptor = new HTableDescriptor(schemaName);  
	       
			for(EntityAttr attr:attrs){
				
				if (!temp.contains(attr.getColumn())) {

					tableDescriptor.addFamily(new HColumnDescriptor(attr.getColumn()));
					temp.add(attr.getColumn());
				}
				
			}
			
			hadmin.createTable(tableDescriptor);  
			
		}catch(IOException ioe){
			LOGGER.error("Error create schema:{}",ioe, schemaName);
			throw new AccessorException("Error create schema:{}", ioe, schemaName);
		}
		
	}

	@Override
	public void updateSchema(String schemaName, List<EntityAttr> attrs) throws AccessorException {
		HBaseAdmin hadmin = this.getAdmin();
		try{
			if(hadmin.tableExists(schemaName)){
				LOGGER.error("Schema[{}] already existed, ignore further operation.",schemaName);
				return;
			}
			// filter out the column names
			ArrayList<String> temp = new ArrayList<String>();
			for(EntityAttr attr:attrs){
				
				if (!temp.contains(attr.getColumn())) {

					temp.add(attr.getColumn());
				}				
			}
			
			// delete the existed column
			HTableDescriptor htd = hadmin.getTableDescriptor(schemaName.getBytes());
			HColumnDescriptor[] hcols = htd.getColumnFamilies();
			for(HColumnDescriptor hcol: hcols){
				if (!temp.contains(hcol.getNameAsString())) {

					hadmin.deleteColumn(schemaName, hcol.getNameAsString());
				}else{
					
					temp.remove(hcol.getNameAsString());
				}
			}
			// create newly add column
			for(String attrCol:temp){

				hadmin.addColumn(schemaName,new HColumnDescriptor(attrCol));
			}
			
		}catch(IOException ioe){
			LOGGER.error("Error create schema:{}",ioe, schemaName);
			throw new AccessorException("Error create schema:{}", ioe, schemaName);
		}
	}

	@Override
	public void dropSchema(String schemaName) throws AccessorException {
		HBaseAdmin hadmin = this.getAdmin();
		try{
			
			if(!hadmin.tableExists(schemaName)){
				LOGGER.error("Schema[{}] already existed, ignore further operation.",schemaName);
				return;
			}
			
			if(hadmin.isTableEnabled(schemaName))
				hadmin.disableTable(schemaName);
			
			hadmin.deleteTable(schemaName);
			
		}catch(IOException ioe){
			LOGGER.error("Error drop schema:{}", ioe, schemaName);
			throw new AccessorException("Error drop schema:{}", ioe, schemaName);
		}

	}

	@Override
	public void setAdmin(HBaseAdmin admin) {
		
		this.admin = admin;
	}

	@Override
	public HBaseAdmin getAdmin() {
		
		return this.admin;
	}
		
	@Override
	public void release() {
		// release super's HConnection
		super.release();
		
		if(this.admin != null){			
			try {
				this.admin.close();
				
			} catch (IOException e) {
				
				LOGGER.debug("Error when release admin resource.",e);
			}
		}
	}
}
