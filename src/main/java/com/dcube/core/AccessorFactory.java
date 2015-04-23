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
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityAccessor;
import com.dcube.core.accessor.GenericAccessor;
import com.dcube.core.accessor.GenericContext;
import com.dcube.core.accessor.IndexAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.exception.MetaException;
import com.dcube.launcher.LifecycleHooker;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityManager;

/**
 * AccessorFactory create service instances according to request entry name,
 * class etc.
 * 
 * @author despird
 * @version 0.1 2014-3-1 initial
 * @version 0.2 2014-4-2 add index accessor support.
 * 
 * @see AccessorBuilder
 **/
public final class AccessorFactory {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AccessorFactory.class);
	/** AccessorBuilder cache */
	private static Map<String, AccessorBuilder> builderMap = new HashMap<String, AccessorBuilder>();

	/** singleton */
	private static AccessorFactory instance;

	/** default builder */
	private static String defaultBuilder = null;
	
	/** cache builder */
	private static String cacheBuilder = null;
	
	/** LifecycleHooker to interact with CoreFacade and listen the event */
	private static LifecycleHooker hooker;
		
	/**
	 * Hide from explicit invoke
	 **/
	private AccessorFactory() {

		defaultBuilder = CoreConfigs.getString(CoreConstants.CONFIG_DFT_BUILDER,CoreConstants.BUILDER_HBASE);
		cacheBuilder = CoreConfigs.getString(CoreConstants.CONFIG_CACHE_BUILDER,CoreConstants.BUILDER_REDIS);
		LOGGER.info("default builder is {}", defaultBuilder);

	}
	
	/**
	 * Load the AccessorBuilder and load all the IBaseAccessor classes under specified package.
	 * 
	 **/
	public static void loadAccessorBuilder(){
		
		if (instance == null)
			instance = new AccessorFactory();
		
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
	 * Initial instance
	 * 
	 * @return AccessorFactory the singleton instance.
	 **/
	public static LifecycleHooker getHooker() {

		if (instance == null)
			instance = new AccessorFactory();
		hooker = new LifecycleHooker("AccessorFactory", 999){

			@Override
			public void initial() {
				sendFeedback(false, "AccessorFactory load all accessor builders.");
				loadAccessorBuilder();
			}

			@Override
			public void startup() {
				sendFeedback(false, "AccessorFactory startup nothing done.");
			}

			@Override
			public void shutdown() {
				sendFeedback(false, "AccessorFactory shutdown do nothing.");			
			}
			
		};
		return hooker;
	}

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
	 * Add the AccessorBuilder to AccessorBuilder factory.
	 * 
	 * @param accessorBuilder
	 *            the AccessorBuilder instance
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
	 * Build entry service
	 * 
	 * @param principal
	 *            the principal
	 * @param entityName
	 *            the name of entity, eg. the map key of service class
	 **/
	public static <K> K buildEntityAccessor(Principal principal, String entityName)
			throws AccessorException {

		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == defaultBuilder) {

			throw new AccessorException(
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
			throws AccessorException {
		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == defaultBuilder) {

			throw new AccessorException(
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
	 *            the mock-up IBaseAccessor instance
	 * @param entityName
	 *            the name of entry
	 **/
	public static <K> K buildEntityAccessor(IBaseAccessor mockupAccessor,
			String entityName) throws AccessorException {

		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == dftBuilder) {

			throw new AccessorException(
					"The Default AccessorBuilder instance:{} not existed.",
					defaultBuilder);
		}

		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new AccessorException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					defaultBuilder);
		}
		BaseEntity schema;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when fetch schema object:entity-{}",e, entityName);
		}
		AccessorContext newcontext = new AccessorContext(context,schema);
		K accessor = dftBuilder.newBaseAccessor(newcontext, schema.getEntityMeta().getAccessorName(), false);
		dftBuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}

	/**
	 * Build embed GeneralAccessor instance, it is usually called in GeneralAccessor object.
	 * The principal will be retrieved from mockupAccessor.
	 * 
	 * @param mockupAccessor
	 *            the mock-up IBaseAccessor instance
	 * @param entryName
	 *            the name of entry
	 **/
	public static <K> K buildGenericAccessor(IBaseAccessor mockupAccessor,
			String accessorName) throws AccessorException {

		AccessorBuilder dftBuilder = builderMap.get(defaultBuilder);
		if (null == dftBuilder) {

			throw new AccessorException(
					"The Default AccessorBuilder instance:{} not existed.",
					defaultBuilder);
		}
		// retrieve the principal from mock-up accessor
		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new AccessorException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					defaultBuilder);
		}
		// new generic context
		GenericContext newcontext = new GenericContext(context);
		K accessor = dftBuilder.newBaseAccessor(newcontext, accessorName, true);
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
			String entityName) throws AccessorException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new AccessorException(
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
			String accessorName) throws AccessorException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new AccessorException(
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
			throws AccessorException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new AccessorException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}

		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new AccessorException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					accessorbuilder.getBuilderName());
		}
		BaseEntity schema;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when fetch schema object:entity-{}",e, entityName);
		}
		AccessorContext newcontext = new AccessorContext(context,schema);
		K accessor = accessorbuilder.newBaseAccessor(newcontext, schema.getEntityMeta().getAccessorName(), false);
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
	 *            the mock-up IBaseAccessor instance
	 * @param entryName
	 *            the name of entry
	 **/
	public static <K> K buildGenericAccessor(String builderName,
			IBaseAccessor mockupAccessor, String accessorName)
			throws AccessorException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new AccessorException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		
		GenericContext context = mockupAccessor.getContext();
		if(context == null){
			throw new AccessorException(
					"The Mockup Accessor[from {}]'s AccessorContext not existed.",
					accessorbuilder.getBuilderName());
		}
		// new generic context
		GenericContext newcontext = new GenericContext(context);
		K accessor = accessorbuilder.newBaseAccessor(newcontext, accessorName, true);
		accessorbuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}
	
	/**
	 * Register IBaseAccessor mapping information.
	 * <p>This method will be used for IBaseAccessor to register itself. </p> 
	 * 
	 * @param builderName the name of builder
	 * @param accessorName the name of IBaseAccessor
	 * @param accessor the instance of IBaseAccessor
	 * 
	 **/
	public static void registerAccessor(String builderName, IBaseAccessor accessor){
		
		Objects.requireNonNull(accessor);

		AccessorBuilder builder = AccessorFactory.getAccessorBuilder(builderName);
		builder.appendAccessor(accessor.getAccessorName(), accessor.getClass());

	}
	
	/**
	 * Build Cache IEntityAccessor with specified context.
	 * 
	 * @param principal the principal object 
	 * @param entityName the entity name
	 **/
	public static <K extends IEntityEntry> IEntityAccessor<K> buildCacheAccessor(Principal principal, String entityName)throws AccessorException {
		
		AccessorBuilder accessorbuilder = builderMap.get(cacheBuilder);
		if (null == accessorbuilder) {

			throw new AccessorException(
					"The cache AccessorBuilder instance:{} not existed.", cacheBuilder);
		}

		BaseEntity schema;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when fetching entity[{}] schema.", e, entityName);
		}
		// prepare context object
		AccessorContext context = new AccessorContext(principal,schema);		
		// new generic context
		IEntityAccessor<K> accessor = accessorbuilder.newBaseAccessor(context, CoreConstants.CACHE_ACCESSOR, false);
		// assembly the accessor
		accessorbuilder.assembly(principal, (IBaseAccessor) accessor);
		
		return accessor;
	}
	
	/**
	 * Build IndexAccessor 
	 * @param principal
	 * @param entityName
	 **/
	public static IndexAccessor buildIndexAccessor(Principal principal, String entityName)throws AccessorException {
		
		AccessorBuilder accessorbuilder = builderMap.get(cacheBuilder);
		if (null == accessorbuilder) {

			throw new AccessorException(
					"The cache AccessorBuilder instance:{} not existed.", cacheBuilder);
		}
		BaseEntity schema = null;
		try {
			schema = EntityManager.getInstance().getEntitySchema(entityName);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when fetching entity[{}] schema.", e, entityName);
		}
		// prepare context object
		AccessorContext context = new AccessorContext(principal,schema);		
		// new generic context
		IndexAccessor accessor = accessorbuilder.newIndexAccessor(context);
		// assembly the accessor
		accessorbuilder.assembly(principal, (IBaseAccessor) accessor);
		
		return accessor;
		
	}
	
	/**
	 * Build IndexAccessor 
	 * @param mockupAccessor
	 **/
	public static IndexAccessor buildIndexAccessor(IEntityAccessor<?> mockupAccessor)throws AccessorException {
		
		AccessorBuilder accessorbuilder = builderMap.get(cacheBuilder);
		if (null == accessorbuilder) {

			throw new AccessorException(
					"The cache AccessorBuilder instance:{} not existed.", cacheBuilder);
		}
		BaseEntity schema = mockupAccessor.getEntitySchema();		
		// prepare context object
		AccessorContext context = new AccessorContext(mockupAccessor.getContext(),schema);		
		// new generic context
		IndexAccessor accessor = accessorbuilder.newIndexAccessor(context);
		// assembly the accessor
		accessorbuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		
		return accessor;
		
	}
}
