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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

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
 * @author G.Obal
 * @version 0.1 2014-1-1
 **/
public class CoreConfig extends PropertiesConfiguration{

	private static CoreConfig instance;

	private CoreConfig() throws ConfigurationException{
		
		super("obal-config.properties");
	}
	
	/**
	 * Get single instance of configuration, use it get variables by Key. 
	 **/
	public static CoreConfig getInstance(){
		
		if(null == instance)
			try {
				instance = new CoreConfig();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		
		return instance;
	}
}