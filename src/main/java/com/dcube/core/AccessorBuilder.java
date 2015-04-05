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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityAccessor;
import com.dcube.core.accessor.GenericAccessor;
import com.dcube.core.accessor.GenericContext;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.exception.EntityException;
import com.dcube.exception.MetaException;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityManager;

/**
 * Base class of AccessorBuilder, provides common operation when create accessor instance
 * 
 * @author despird-zh
 * @version 0.1 2014-3-1
 * 
 **/
public abstract class AccessorBuilder {

	private Map<String, Class<? extends IBaseAccessor>> accessorMap = null;
	private String builderName = null;
	Logger LOGGER = LoggerFactory.getLogger(AccessorBuilder.class);

	/**
	 * Constructor
	 * @param builderName builder name eg. hbase, redis
	 * @param accessormap the accessor mapping 
	 **/
	protected AccessorBuilder(String builderName){
		
		this.builderName = builderName;
		accessorMap = new HashMap<String, Class<? extends IBaseAccessor>>();

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
	protected Class<IBaseAccessor> getAccessorClass(String accessor) throws AccessorException{
		
		Class<IBaseAccessor> entryAccessorClazz = (Class<IBaseAccessor>)accessorMap.get(accessor);
		if(entryAccessorClazz == null) {
			
			throw new AccessorException("Accessor :{} is not found in Accessor Mapping!", accessor);
		}
		
		return entryAccessorClazz;
	}
	
	/**
	 * Detect the Accessors under specified package path
	 * @param packagePath
	 **/
	protected void detectAccessors(String packagePath) throws AccessorException{
		
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
				@SuppressWarnings("unchecked")
				Class<? extends IBaseAccessor> clazzTemp = (Class<? extends IBaseAccessor>)clazz;
				accessorMap.put(accessorName, clazzTemp);
				
				LOGGER.debug("Found {} - {}", accessorName,accessorClass);
			} catch (InstantiationException |IllegalAccessException e) {
				
				LOGGER.error("Fail instantiate accessor:{}-{}",new String[]{accessorName,accessorClass},e);
				throw new AccessorException("Fail instantiate accessor:{}-{}", e, new String[]{accessorName,accessorClass});
				
			} 
		}
	}
	
	/**
	 * Assembly the resource to IBaseAccessor instance
	 * @param principal the principal object
	 * @param accessor the IBaseAccessor object to be assembled
	 **/
	public abstract void assembly(Principal principal, IBaseAccessor accessor) throws AccessorException;
	
	/**
	 * Assembly the resource to IBaseAccessor instance, the resources is copied from mockup accessor.
	 * 
	 * @param mockupAccessor the mock-up IBaseAccessor object
	 * @param accessors the IBaseAccessor objects to be assembled
	 **/
	public abstract void assembly(IBaseAccessor mockupAccessor, IBaseAccessor... accessors) throws AccessorException;

	/**
	 * create new EntityAccessor instance. 
	 * 
	 * @param principal
	 * @param entityName  
	 * 
	 * @return K the EntityAccessor instance
	 **/
	protected <K> K newEntityAccessor(Principal principal,String entityName) throws AccessorException{
		String accessorName = null;
		BaseEntity schema;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
			accessorName = schema.getEntityMeta().getAccessorName();
		} catch (MetaException e) {
			
			throw new AccessorException("Error when fetching entity[{}] schema.", e, entityName);
		}
		// prepare context object
		AccessorContext context = new AccessorContext(principal,schema);
				
		return newBaseAccessor(context, accessorName,false);
		
	}	
	
	/**
	 * create new GeneralAccessor instance.
	 * 
	 * @param principal
	 * @param accessorName 
	 * 
	 * @return K the GenericAccessor instance
	 **/
	protected <K> K newGenericAccessor(Principal principal, String accessorName) throws AccessorException{
		
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
	protected <K> K newBaseAccessor(GenericContext context, String accessorName, boolean isGeneric) throws AccessorException{
		
		K result = null;
		try {

			Class<?> clazz = getAccessorClass(accessorName);
			
			if(!GenericAccessor.class.isAssignableFrom(clazz) && isGeneric)
				
				throw new AccessorException("The {}-{} is not a GenericAccessor sub class.",accessorName, clazz.getName() );
			else if(!EntityAccessor.class.isAssignableFrom(clazz) && !isGeneric){
				
				throw new AccessorException("The {}-{} is not a EntityAccessor sub class.",accessorName, clazz.getName() );
			}
	
			result = (K)clazz.newInstance();
			@SuppressWarnings("resource")
			IBaseAccessor baseAccessor = (IBaseAccessor)result;
			baseAccessor.setContext(context);

		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {

			throw new AccessorException("Fail build Accessor-{}",e, accessorName);
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
	@SuppressWarnings("unchecked")
	public void appendAccessor(String accessorName, String accessorClass) throws AccessorException{
		
		LOGGER.debug("Append [{}] entity mapping: {} -> {}" , 
				new String[]{this.builderName, accessorName, accessorClass});
		
		Class<IBaseAccessor> entryAccessorClazz = null;
		try {
			Class<?> rawClazz = this.getClass().getClassLoader().loadClass(accessorClass);
			if(IBaseAccessor.class.isAssignableFrom(rawClazz))
				entryAccessorClazz = (Class<IBaseAccessor>)rawClazz;
				
		} catch (ClassNotFoundException e) {
			
			throw new AccessorException("Class:{}-{} is not found!",e,accessorName,accessorClass);
		}
		accessorMap.put(accessorName, entryAccessorClazz);
	}
	
	/**
	 * Append the Accessor to map 
	 **/
	public void appendAccessor(String accessorName, Class<? extends IBaseAccessor> entryAccessorClazz){
		accessorMap.put(accessorName, entryAccessorClazz);
	}
	
	/**
	 * Build new cache accessor instance to access cache data, default is not supported. 
	 **/
	public <K extends IEntityEntry> IEntityAccessor<K> newCacheAccessor(AccessorContext context)throws AccessorException{
		
		throw new UnsupportedOperationException("Not support build Cache Accessor");
	}
}
