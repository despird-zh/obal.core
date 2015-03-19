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
package com.dcube.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityAccessor;
import com.dcube.core.accessor.GenericAccessor;
import com.dcube.core.accessor.GenericContext;
import com.dcube.core.security.Principal;
import com.dcube.exception.EntityException;
import com.dcube.exception.MetaException;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityManager;
import com.dcube.util.AccessorDetector;

/**
 * Base class of AccessorBuilder, provides common operation when create accessor instance
 * 
 * @author despird-zh
 * @version 0.1 2014-3-1
 * 
 **/
public abstract class AccessorBuilder {

	private Properties accessorProp = null;
	private String builderName = null;
	Logger LOGGER = LoggerFactory.getLogger(AccessorBuilder.class);

	/**
	 * Constructor
	 * @param builderName builder name eg. hbase, redis
	 * @param accessormap the accessor mapping 
	 **/
	protected AccessorBuilder(String builderName){
		
		this.builderName = builderName;
		accessorProp = new Properties();

	}

	/**
	 * Get the builder name
	 * @return String builder name 
	 **/
	protected String getBuilderName(){
		return builderName;
	}
	
	/**
	 * Get the IBaseAccessor implementation class object
	 * 
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
	 * detect the Accessors
	 **/
	protected void detectAccessors(String packagePath){
		
		Objects.requireNonNull(packagePath);
		LOGGER.debug("Detecting package - {}",packagePath);
		List<Class<?>> clazzList = AccessorDetector.getClassesForPackage(packagePath);
		String accessorName = null;
		String accessorClass = null;
		for(Class<?> clazz : clazzList){
			
			if(!IBaseAccessor.class.isAssignableFrom(clazz)){
				
				LOGGER.debug("Warning {} is not Accessor, ignore it.",clazz.getCanonicalName());
				continue;
			}
			try {	
				IBaseAccessor instance = (IBaseAccessor) clazz.newInstance();
				accessorName = instance.getAccessorName();
				accessorClass = clazz.getCanonicalName();
				accessorProp.put(accessorName, accessorClass);
				
				LOGGER.debug("Found {} - {}", accessorName,accessorClass);
			} catch (InstantiationException e) {
				
				LOGGER.error("Fail instantiate accessor:{}-{}",new String[]{accessorName,accessorClass},e);
			} catch (IllegalAccessException e) {
				
				LOGGER.error("Fail instantiate accessor:{}-{}",new String[]{accessorName,accessorClass},e);
			}
		}
	}
	
	/**
	 * Append accessor map to builder. 
	 **/
	protected void appendAccessorMap(Map<String,String> mapping){
		
		accessorProp.putAll(mapping);
	}
	
	/**
	 * Assembly the resource to IBaseAccessor instance
	 * @param principal the principal object
	 * @param accessor the IBaseAccessor object to be assembled
	 **/
	public abstract void assembly(Principal principal, IBaseAccessor accessor) throws EntityException;
	
	/**
	 * Assembly the resource to IBaseAccessor instance, the resources is copied from mockup accessor.
	 * 
	 * @param mockupAccessor the mock-up IBaseAccessor object
	 * @param accessors the IBaseAccessor objects to be assembled
	 **/
	public abstract void assembly(IBaseAccessor mockupAccessor, IBaseAccessor... accessors) throws EntityException;

	/**
	 * create new EntityAccessor instance. Accessor name and entity name is same.
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
	 * create new EntityAccessor instance.
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
		// prepare context object
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
		
		GenericContext context = new GenericContext(principal);
		
		return newBaseAccessor(context, accessorName,true);
	}

	/**
	 * Build the GenericAccessor instance, firstly check cache, not exist create new one.
	 * 
	 * @param context the AccessorContext object
	 * @param accessorName the name of accessor
	 * @param isGeneric the generic flag
	 * 
	 * @throws EntityException
	 **/
	@SuppressWarnings("unchecked")
	protected <K> K newBaseAccessor(GenericContext context, String accessorName, boolean isGeneric) throws EntityException{
		
		K result = null;
		try {

			Class<?> clazz = this.getAccessorClass(accessorName);
			
			if(!GenericAccessor.class.isAssignableFrom(clazz) && isGeneric)
				
				throw new EntityException("The {}-{} is not a GenericAccessor sub class.",accessorName, clazz.getName() );
			else if(!EntityAccessor.class.isAssignableFrom(clazz) && !isGeneric){
				
				throw new EntityException("The {}-{} is not a EntityAccessor sub class.",accessorName, clazz.getName() );
			}
			
			Constructor<K> constructor = null;
		
			if(isGeneric)
				constructor = (Constructor<K>)clazz.getConstructor(GenericContext.class);
			else
				constructor = (Constructor<K>)clazz.getConstructor(AccessorContext.class);
			
			result = constructor.newInstance(context);

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
	
	/**
	 * Append accessor mapping
	 * 
	 * @param accessorName the name of accessor
	 * @param accessorClass the class of accessor
	 * 
	 **/
	public void appendAccessorMap(String accessorName, String accessorClass){
		
		LOGGER.debug("Append [{}] entity mapping: {} -> {}" , 
				new String[]{this.builderName, accessorName, accessorClass});
		accessorProp.put(accessorName, accessorClass);
	}
}
