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
package com.doccube.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.accessor.EntityAccessor;
import com.doccube.core.accessor.GenericAccessor;
import com.doccube.core.accessor.GenericContext;
import com.doccube.core.security.Principal;
import com.doccube.exception.EntityException;
import com.doccube.exception.MetaException;
import com.doccube.meta.BaseEntity;
import com.doccube.meta.EntityManager;

/**
 * AccessorFactory create service instances according to request entry name,
 * class etc.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * @see AccessorBuilder
 **/
public final class AccessorFactory {

	static Logger LOGGER = LoggerFactory.getLogger(AccessorFactory.class);
	/** AccessorBuilder cache */
	private static Map<String, AccessorBuilder> builderMap = new HashMap<String, AccessorBuilder>();

	/** singleton */
	private static AccessorFactory instance;

	/** default builder */
	private static String defaultBuilder = null;
	
	/**
	 * Automatically create the factory instance, and load the builder instance.
	 **/
	static{
		
		instance = new AccessorFactory();
		instance.loadAccessorBuilder();
	}
	
	/**
	 * Hide from explicit invoke
	 **/
	private AccessorFactory() {

		CoreConfig cc = CoreConfig.getInstance();
		String defaultName = cc.getString(CoreConstants.CONFIG_DFT_BUILDER,CoreConstants.BUILDER_HBASE);
		defaultBuilder = defaultName;
		LOGGER.info("default builder is {}", defaultName);
		
	}

	private void loadAccessorBuilder(){
		
        ServiceLoader<AccessorBuilder> svcloader = ServiceLoader
                .load(AccessorBuilder.class, ClassLoader.getSystemClassLoader());
        // ServiceConfigurationError may be throw here
        for (AccessorBuilder builder: svcloader) {
            String name = builder.getBuilderName();
            LOGGER.debug("Loaded AccessorBuilder[{}]",name);
            builderMap.put(name, builder);
        }
	}
	
	/**
	 * Singleton instance
	 * 
	 * @return AccessorFactory the singleton instance.
	 **/
//	public static AccessorFactory getInstance() {
//
//		if (instance == null)
//			instance = new AccessorFactory();
//
//		return instance;
//	}

	/**
	 * Set the default builder name
	 * 
	 * @param defaultBuilder
	 *            the default builder name
	 * 
	 **/
	public static void setDefaultBuilder(String dftBuilder) {

		defaultBuilder = dftBuilder;
	}

	/**
	 * Get the default builder name
	 * 
	 * @return String the default builder name
	 **/
	public static String getDefaultBuilder() {

		return defaultBuilder;
	}

	/**
	 * Add the AccessorBuilder to accessor factory.
	 * 
	 * @param accessorBuilder
	 *            the accessor builder instance
	 * 
	 **/
	public static void addAccessorBuilder(AccessorBuilder accessBuilder) {

		if (null == accessBuilder) {

			LOGGER.warn("The access builder is null.");
		}

		builderMap.put(accessBuilder.getBuilderName(), accessBuilder);
	}

	/**
	 * Get the AccessorBuilder instance
	 * 
	 * @param builderName
	 *            the builder name
	 * 
	 * @return AccessorBuilder the builder instance
	 * 
	 **/
	public static AccessorBuilder getAccessorBuilder(String builderName) {

		return builderMap.get(builderName);
	}

	/**
	 * Append service mapping to Factory
	 * 
	 * @param builderName
	 *            the name of builder
	 * @param resourcePath
	 *            the path of resource file, eg. com/tt/xx/mm.properties
	 * 
	 **/
	public static void appendMapping(String builderName, String resourcePath) {

//		if(!builderMap.containsKey(builderName)){
//			LOGGER.warn("builder:{} not exists",builderName);
//			return;
//		}
//		
//		LOGGER.debug("Load {}'s mapping resource:{}", new String[]{builderName,resourcePath});
//		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
//		if(is == null){
//			
//			LOGGER.error("Cannot load accessor mapping:{}",resourcePath);
//			return;
//		}
//		Properties prop = new Properties();
//		try {
//			
//			prop.load(is);
//			Map<String, String> entries = new HashMap<String, String>();
//
//			for (final String name : prop.stringPropertyNames())
//				entries.put(name, prop.getProperty(name));
//
//			appendMapping(builderName, entries);
//
//		} catch (IOException e) {
//
//			LOGGER.error("Error during EntryAdmin contrustor.", e);
//		}
	}

	/**
	 * Append service mapping to Factory
	 * 
	 * @param builderName
	 *            the name of builder
	 * @param mapping
	 *            the mapping of services
	 **/
	public static void appendMapping(String builderName, Map<String, String> mapping) {

		builderMap.get(builderName).appendAccessorMap(mapping);
	}

	/**
	 * Build entry service
	 * 
	 * @param principal
	 *            the principal
	 * @param entityName
	 *            the name of entity, eg. the map key of service class
	 **/
	public static <K> K buildEntityAccessor(Principal principal, String entityName)
			throws EntityException {

		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == defaultBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					defaultBuilder);
		}
		K accessor = dftBuilder.newEntityAccessor(principal, entityName );
		// set other property as per builder
		dftBuilder.assembly(principal, (EntityAccessor<?>) accessor);
		return accessor;
	}

	/**
	 * Build General service
	 * 
	 * @param principal
	 *            the principal
	 * @param accessorName
	 *            the name of entry, eg. the map key of service class
	 **/
	public static <K> K buildGenericAccessor(Principal principal, String accessorName)
			throws EntityException {
		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == defaultBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					defaultBuilder);
		}
		K accessor = dftBuilder.newGenericAccessor(principal, accessorName);
		dftBuilder.assembly(principal, (GenericAccessor) accessor);
		return accessor;
	}

	/**
	 * Build embed EntryAccessor instance, it is usually called in GeneralAccessor object.
	 * The principal will be retrieved from mockupAccessor.
	 * 
	 * @param mockupAccessor
	 *            the mock-up accessor instance
	 * @param entryName
	 *            the name of entry
	 **/
	public static <K> K buildEntityAccessor(IBaseAccessor mockupAccessor,
			String entityName) throws EntityException {

		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == dftBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					defaultBuilder);
		}

		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new EntityException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					defaultBuilder);
		}
		BaseEntity schema;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
		} catch (MetaException e) {
			
			throw new EntityException("Error when fetch schema object:entity-{}",e, entityName);
		}
		AccessorContext econtext = new AccessorContext(context.getPrincipal(),schema);
		context.copy(econtext);// copy principal and attached values
		econtext.setEmbed(true);
		K accessor = dftBuilder.newBaseAccessor(econtext, entityName, false);
		dftBuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}

	/**
	 * Build embed GeneralAccessor instance, it is usually called in GeneralAccessor object.
	 * The principal will be retrieved from mockupAccessor.
	 * 
	 * @param mockupAccessor
	 *            the mock-up accessor instance
	 * @param entryName
	 *            the name of entry
	 **/
	public static <K> K buildGenericAccessor(IBaseAccessor mockupAccessor,
			String accessorName) throws EntityException {

		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == dftBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					defaultBuilder);
		}
		// retrieve the principal from mock-up accessor
		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new EntityException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					defaultBuilder);
		}
		// new generic context
		GenericContext ncontext = new GenericContext(context.getPrincipal());
		context.copy(ncontext);
		ncontext.setEmbed(true);
		K accessor = dftBuilder.newBaseAccessor(ncontext, accessorName, true);
		dftBuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}

	/**
	 * Build entry service
	 * 
	 * @param builderName
	 *            the builder name
	 * @param principal
	 *            the principal
	 * @param entityName
	 *            the name of entity, eg. the map key of service class
	 **/
	public static <K> K buildEntityAccessor(String builderName, Principal principal,
			String entityName) throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		K accessor = accessorbuilder.newEntityAccessor(principal,entityName);
		accessorbuilder.assembly(principal, (EntityAccessor<?>) accessor);
		return accessor;
	}

	/**
	 * Build General service
	 * 
	 * @param builderName
	 *            the builder name
	 * @param principal
	 *            the principal
	 * @param accessorName
	 *            the name of entry, eg. the map key of service class
	 **/
	public static <K> K buildGenericAccessor(String builderName, Principal principal,
			String accessorName) throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		K accessor = accessorbuilder.newGenericAccessor(principal,accessorName);
		accessorbuilder.assembly(principal, (GenericAccessor) accessor);
		return accessor;
	}


	/**
	 * Build embed EntryAccessor instance, it is usually called in GeneralAccessor object.
	 * The principal will be retrieved from mockupAccessor.
	 * 
	 * @param builderName
	 *            the builder name
	 * @param mockupAccessor
	 *            the mock-up accessor instance
	 * @param entityName
	 *            the name of entity
	 **/
	public static <K> K buildEntityAccessor(String builderName,
			IBaseAccessor mockupAccessor, String entityName)
			throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}

		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new EntityException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					accessorbuilder.getBuilderName());
		}
		BaseEntity schema;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
		} catch (MetaException e) {
			
			throw new EntityException("Error when fetch schema object:entity-{}",e, entityName);
		}
		AccessorContext econtext = new AccessorContext(context.getPrincipal(),schema);
		context.copy(econtext);
		econtext.setEmbed(true);
		K accessor = accessorbuilder.newBaseAccessor(econtext, entityName, false);
		accessorbuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}

	/**
	 * Build embed GeneralAccessor instance, it is usually called in GeneralAccessor object.
	 * The principal will be retrieved from mockupAccessor.
	 * 
	 * @param builderName
	 *            the builder name
	 * @param mockupAccessor
	 *            the mock-up accessor instance
	 * @param entryName
	 *            the name of entry
	 **/
	public static <K> K buildGenericAccessor(String builderName,
			IBaseAccessor mockupAccessor, String accessorName)
			throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		
		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new EntityException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					accessorbuilder.getBuilderName());
		}
		// new generic context
		GenericContext ncontext = new GenericContext(context.getPrincipal());
		context.copy(ncontext);
		ncontext.setEmbed(true);
		K accessor = accessorbuilder.newBaseAccessor(ncontext, accessorName, true);
		accessorbuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}
	
	/**
	 * Register accessor mapping information.
	 * <p>This method will be used for accessor to register itself. </p> 
	 * 
	 * @param builderName the name of builder
	 * @param accessorName the name of accessor
	 * @param accessor the instance of accessor
	 * 
	 **/
	public static void registerAccessor(String builderName, IBaseAccessor accessor){
		
		Objects.requireNonNull(accessor);
		String accessorClass = accessor.getClass().getName();
		AccessorBuilder builder = AccessorFactory.getAccessorBuilder(builderName);
		builder.appendAccessorMap(accessor.getAccessorName(), accessorClass);
	}
}
