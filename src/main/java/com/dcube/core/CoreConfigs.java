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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.exception.BaseException;
import com.google.common.base.Strings;

/**
 * Retrieve the configuration variables from environment configuration properties
 * file. 
 * EnvConfiguration.getInstance().getString("demo.key");<br>
 * the property file locate under root of class path:<em>envconfig.properties</em>
 * <p>
 * 	this class extends from Apache configuration PropertiesConfiguration
 * </p>
 * 
 * @see org.apache.commons.configuration.ConfigurationException
 * @author Despird-zh
 * @version 0.1 2014-1-1
 **/
public class CoreConfigs{

	private static Logger LOGGER = LoggerFactory.getLogger(CoreConfigs.class);
	
	private static PropertiesConfiguration selfConfig = null;
	private static PropertiesConfiguration overrideConfig = null;
	
	static{
		try {
			selfConfig = new PropertiesConfiguration("META-INF/dcube-config.properties");
			overrideConfig = new PropertiesConfiguration("dcube-config.properties");
		} catch (ConfigurationException e) {
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("Fail to load configuration properties file",e);
			else
				LOGGER.warn("Fail to load configuration properties file");
		}	
	}
	
	private CoreConfigs() throws ConfigurationException{}
	
	/**
	 * Get the String value of key 
	 * 
	 * @param key
	 * @param defaultVal
	 **/
	public static String getString(String key,String defaultVal){
		
		String val = null;
		if(overrideConfig != null){
			overrideConfig.getString(key);
		}
		
		if(val == null || "".equals(val)){
			val = selfConfig.getString(key, defaultVal);
		}
		
		return val;
	}
	
	/**
	 * Get the String value of key 
	 **/
	public static String getString(String key){
		
		return getString(key,null);
	}
}