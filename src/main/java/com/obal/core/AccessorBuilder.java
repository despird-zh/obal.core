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
package com.obal.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.obal.core.accessor.AccessorContext;
import com.obal.core.accessor.EntityAccessor;
import com.obal.core.accessor.GenericAccessor;
import com.obal.core.security.Principal;
import com.obal.exception.EntityException;
import com.obal.exception.MetaException;
import com.obal.meta.BaseEntity;
import com.obal.meta.EntityManager;

/**
 * Base class of AccessorBuilder, provides common operation when create accessor instance
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public abstract class AccessorBuilder {

	private Properties accessorProp = null;
	private String builderName = null;
	
	/**
	 * Default Constructor 
	 **/
	protected AccessorBuilder() throws EntityException{}

	
	/**
	 * Constructor
	 * @param builderName builder name eg. hbase, redis
	 * @param accessormap the accessor mapping 
	 **/
	protected AccessorBuilder(String builderName, String accessormap)throws EntityException{
		
		this.builderName = builderName;
		
		InputStream is = AccessorBuilder.class.getClassLoader().getResourceAsStream(accessormap);
		accessorProp = new Properties();
		
		try {
			
			accessorProp.load(is);			
		} catch (IOException e) {
			
			throw new EntityException("Fail build Accessor builder:{}", e, builderName);
		}
	}

	/**
	 * get the builder name
	 * @return String builder name 
	 **/
	protected String getBuilderName(){
		return builderName;
	}
	
	/**
	 * get the accessor's class object
	 * @param accessor the accessor name
	 * @return Class object of accessor
	 **/
	@SuppressWarnings("unchecked")
	protected Class<IBaseAccessor> getAccessorClass(String accessor) throws EntityException{
		
		String accessorClass = (String) accessorProp.get(accessor);	
		Class<IBaseAccessor> entryAccessorClazz = null;
		try {
			Class<?> rawClazz = this.getClass().getClassLoader().loadClass(accessorClass);
			if(IBaseAccessor.class.isAssignableFrom(rawClazz))
				entryAccessorClazz = (Class<IBaseAccessor>)rawClazz;
				
		} catch (ClassNotFoundException e) {
			
			throw new EntityException("Class:{}-{} is not found!",e,accessor,accessorClass);
		}
		
		return entryAccessorClazz;
	}
		
	/**
	 * Append accessor map to builder. 
	 **/
	protected void appendAccessorMap(Map<String,String> mapping){
		
		accessorProp.putAll(mapping);
	}
	
	/**
	 * Assembly the resource to Accessor instance
	 * @param principal the principal object
	 * @param accessor the Accessor object to be assembled
	 **/
	public abstract void assembly(Principal principal, IBaseAccessor accessor) throws EntityException;
	
	/**
	 * Assembly the resource to Accessor instance, the resources is copied from mockup accessor.
	 * 
	 * @param mockupAccessor the mock-up Accessor object
	 * @param accessors the Accessor objects to be assembled
	 **/
	public abstract void assembly(IBaseAccessor mockupAccessor, IBaseAccessor... accessors) throws EntityException;

	/**
	 * create new EntryAccessor instance. accessor name and entity name is same.
	 * 
	 * @param principal
	 * @param entityName  
	 * 
	 * @return K the EntityAccessor instance
	 **/
	protected <K> K newEntityAccessor(Principal principal,String entityName) throws EntityException{
		
		return newEntityAccessor(principal,entityName,entityName );
	}	

	/**
	 * create new EntryAccessor instance.
	 * 
	 * @param principal
	 * @param accessorName 
	 * @param entityName
	 * 
	 * @return K the EntityAccessor instance
	 **/
	protected <K> K newEntityAccessor(Principal principal,String accessorName,String entityName) throws EntityException{
		
		BaseEntity schema;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
		} catch (MetaException e) {
			
			throw new EntityException("Error when fetch schema object:{}-{}",e,accessorName, entityName);
		}
		AccessorContext context = new AccessorContext(principal,schema);
		
		return newBaseAccessor(context, accessorName,false);
	}
	
	/**
	 * create new GeneralAccessor instance.
	 * 
	 * @param accessorName 
	 * 
	 * @return K the GenericAccessor instance
	 **/
	protected <K> K newGenericAccessor(Principal principal, String accessorName) throws EntityException{
		
		AccessorContext context = new AccessorContext(principal);
		
		return newBaseAccessor(context, accessorName,true);
	}

	@SuppressWarnings("unchecked")
	private <K> K newBaseAccessor(AccessorContext context, String accessorName, boolean isGeneric) throws EntityException{
		
		K result = null;
		
		Class<?> clazz = this.getAccessorClass(accessorName);
		
		if(!GenericAccessor.class.isAssignableFrom(clazz) && isGeneric)
			
			throw new EntityException("The {}-{} is not a GenericAccessor sub class.",accessorName, clazz.getName() );
		else if(!EntityAccessor.class.isAssignableFrom(clazz) && !isGeneric){
			
			throw new EntityException("The {}-{} is not a EntityAccessor sub class.",accessorName, clazz.getName() );
		}
		
		Constructor<K> constructor = null;
		try {
	
				constructor = (Constructor<K>)clazz.getConstructor(AccessorContext.class);
				result = constructor.newInstance(context);
			
		} catch (SecurityException e) {

			throw new EntityException("Fail build Accessor-{}",e, accessorName);
		} catch (IllegalArgumentException e) {

			throw new EntityException("Fail build Accessor-{}",e, accessorName);
		} catch (InstantiationException e) {

			throw new EntityException("Fail build Accessor-{}",e, accessorName);
		} catch (IllegalAccessException e) {

			throw new EntityException("Fail build Accessor-{}",e, accessorName);
		} catch (NoSuchMethodException e) {
			
			throw new EntityException("Fail build Accessor-{}",e, accessorName);
		} catch (InvocationTargetException e) {
			
			throw new EntityException("Fail build Accessor-{}",e, accessorName);
		} 
		
		return result;
	}
}
