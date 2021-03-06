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
package com.dcube.accessor;

import java.util.List;

import com.dcube.core.IBaseAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityAttr;

/**
 * Interface for administration, it extends from IBaseAccessor, means it's a GeneralAccessor.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public interface IAdminGAccessor extends IBaseAccessor{
	
	/**
	 * create schema 
	 * @param schemaName the schema name
	 * @param attrs the list of entry attribute objects
	 **/
	public void createSchema(String schemaName, List<EntityAttr> attrs) throws AccessorException;

	/**
	 * Create the index schema for specified schema 
	 **/
	public void createIndexSchema(String schemaName,List<EntityAttr> attrs) throws AccessorException;
	
	/**
	 * create schema 
	 * @param schemaName the schema name
	 * @param attrs the list of entry attribute objects
	 * @param accessControllable access controllable flag
	 **/
	public void createSchema(String schemaName, List<EntityAttr> attrs, boolean accessControllable) throws AccessorException;
	
	/**
	 * update schema 
	 * @param schemaName the schema name
	 * @param attrs the list of entry attribute objects
	 **/
	public void updateSchema(String schemaName, List<EntityAttr> attrs) throws AccessorException;	

	/**
	 * drop schema 
	 * @param schemaName the schema name
	 **/
	public void dropSchema(String schemaName) throws AccessorException;	

}
