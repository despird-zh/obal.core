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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.accessor.EntityAccessor;
import com.obal.core.accessor.GenericAccessor;
import com.obal.core.security.Principal;
import com.obal.core.security.PrincipalAware;
import com.obal.exception.EntityException;

/**
 * AccessorFactory create service instances according to request entry name,
 * class etc.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * @see AccessorBuilder
 **/
public class AccessorFactory {

	Logger LOGGER = LoggerFactory.getLogger(AccessorFactory.class);
	/** AccessorBuilder cache */
	private Map<String, AccessorBuilder> builderMap = new HashMap<String, AccessorBuilder>();

	private static String BUILDER_PREFIX = "accessor.builder.";

	/**
	 * Hide from explicit invoke
	 **/
	private AccessorFactory() {

		CoreConfig cc = CoreConfig.getInstance();
		String defaultName = cc.getString("builder.default",CoreConstants.BUILDER_HBASE);
		defaultBuilder = defaultName;

		for (int i = 0; i < 20; i++) {

			String builderClass = cc.getString(BUILDER_PREFIX + i);
			
			if(StringUtils.isBlank(builderClass))
				continue;
			
			try {
				Class<?> builderClazz = getClass().getClassLoader().loadClass(builderClass);

				AccessorBuilder hbaseBuilder = (AccessorBuilder) builderClazz.newInstance();

				builderMap.put(hbaseBuilder.getBuilderName(), hbaseBuilder);

			} catch (ClassNotFoundException e) {
				LOGGER.error("class:{} is not found.", builderClass);
			} catch (InstantiationException e) {
				LOGGER.error("class:{} error in instantiation.", builderClass);
			} catch (IllegalAccessException e) {
				LOGGER.error("class:{} be illegal accessed.", builderClass);
			}

		}

		appendMapping(CoreConstants.BUILDER_HBASE, "com/obal/meta/AccessorMap.hbase.properties");

	}

	/** singleton */
	private static AccessorFactory instance;

	/** default builder */
	// private AccessorBuilder defaultBuilder = null;

	private String defaultBuilder = null;

	/**
	 * Singleton instance
	 * 
	 * @return AccessorFactory the singleton instance.
	 **/
	public static AccessorFactory getInstance() {

		if (instance == null)
			instance = new AccessorFactory();

		return instance;
	}

	/**
	 * Set the default builder name
	 * 
	 * @param defaultBuilder
	 *            the default builder name
	 * 
	 **/
	public void setDefaultBuilder(String defaultBuilder) {

		this.defaultBuilder = defaultBuilder;
	}

	/**
	 * Get the default builder name
	 * 
	 * @return String the default builder name
	 **/
	public String getDefaultBuilder() {

		return this.defaultBuilder;
	}

	/**
	 * Add the AccessorBuilder to accessor factory.
	 * 
	 * @param accessorBuilder
	 *            the accessor builder instance
	 * 
	 **/
	public void addAccessorBuilder(AccessorBuilder accessBuilder) {

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
	public AccessorBuilder getAccessorBuilder(String builderName) {

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
	public void appendMapping(String builderName, String resourcePath) {

		if(!builderMap.containsKey(builderName)){
			LOGGER.warn("builder:{} not exists",builderName);
			return;
		}
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);

		Properties prop = new Properties();
		try {
			prop.load(is);
			Map<String, String> entries = new HashMap<String, String>();

			for (final String name : prop.stringPropertyNames())
				entries.put(name, prop.getProperty(name));

			appendMapping(builderName, entries);

		} catch (IOException e) {

			LOGGER.error("Error during EntryAdmin contrustor.", e);
		}
	}

	/**
	 * Append service mapping to Factory
	 * 
	 * @param builderName
	 *            the name of builder
	 * @param mapping
	 *            the mapping of services
	 **/
	public void appendMapping(String builderName, Map<String, String> mapping) {

		builderMap.get(builderName).appendAccessorMap(mapping);
	}

	/**
	 * Build entry service
	 * 
	 * @param principal
	 *            the principal
	 * @param entryName
	 *            the name of entry, eg. the map key of service class
	 **/
	public <K> K buildEntityAccessor(Principal principal, String entryName)
			throws EntityException {

		AccessorBuilder defaultBuilder = builderMap.get(this.defaultBuilder);
		if (null == defaultBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					this.defaultBuilder);
		}
		K accessor = defaultBuilder.newEntityAccessor(entryName, principal);
		defaultBuilder.assembly(principal, (EntityAccessor<?>) accessor);
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
	public <K> K buildGenericAccessor(Principal principal, String accessorName)
			throws EntityException {
		AccessorBuilder defaultBuilder = builderMap.get(this.defaultBuilder);
		if (null == defaultBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					this.defaultBuilder);
		}
		K accessor = defaultBuilder.newGeneralAccessor(accessorName);
		defaultBuilder.assembly(principal, (GenericAccessor) accessor);
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
	public <K> K buildEntityAccessor(IBaseAccessor mockupAccessor,
			String entryName) throws EntityException {

		AccessorBuilder defaultBuilder = builderMap.get(this.defaultBuilder);
		if (null == defaultBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					this.defaultBuilder);
		}
		Principal principal = null;
		if (mockupAccessor instanceof PrincipalAware)
			principal = ((PrincipalAware) mockupAccessor).getPrincipal();

		K accessor = defaultBuilder.newEntityAccessor(entryName, principal);
		defaultBuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
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
	public <K> K buildGenericAccessor(IBaseAccessor mockupAccessor,
			String accessorName) throws EntityException {

		AccessorBuilder defaultBuilder = builderMap.get(this.defaultBuilder);
		if (null == defaultBuilder) {

			throw new EntityException(
					"The Default AccessorBuilder instance:{} not existed.",
					this.defaultBuilder);
		}
		K accessor = defaultBuilder.newGeneralAccessor(accessorName);
		defaultBuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}

	/**
	 * Build entry service
	 * 
	 * @param builderName
	 *            the builder name
	 * @param principal
	 *            the principal
	 * @param entryName
	 *            the name of entry, eg. the map key of service class
	 **/
	public <K> K buildEntityAccessor(String builderName, Principal principal,
			String entryName) throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		K accessor = accessorbuilder.newEntityAccessor(entryName, principal);
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
	public <K> K buildGenericAccessor(String builderName, Principal principal,
			String accessorName) throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		K accessor = accessorbuilder.newGeneralAccessor(accessorName);
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
	 * @param entryName
	 *            the name of entry
	 **/
	public <K> K buildEntityAccessor(String builderName,
			IBaseAccessor mockupAccessor, String entryName)
			throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		Principal principal = null;
		if (mockupAccessor instanceof PrincipalAware)
			principal = ((PrincipalAware) mockupAccessor).getPrincipal();

		K accessor = accessorbuilder.newEntityAccessor(entryName, principal);
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
	public <K> K buildGenericAccessor(String builderName,
			IBaseAccessor mockupAccessor, String accessorName)
			throws EntityException {

		AccessorBuilder accessorbuilder = builderMap.get(builderName);
		if (null == accessorbuilder) {

			throw new EntityException(
					"The AccessorBuilder instance:{} not existed.", builderName);
		}
		K accessor = accessorbuilder.newGeneralAccessor(accessorName);
		accessorbuilder.assembly(mockupAccessor, (IBaseAccessor) accessor);
		return accessor;
	}
}
