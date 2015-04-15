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
package com.dcube.core.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.dcube.core.AccessorBuilder;
import com.dcube.core.CoreConstants;
import com.dcube.core.IBaseAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
/**
 * Jedis-wise implementation of AccessorBuilder.
 * All accessors that access the Redis will be created by this class
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 * @see AccessorBuilder
 * @see REntityAccessor
 * @see RGeneralAccessor
 **/
public class RAccessorBuilder extends AccessorBuilder{

	static Logger LOGGER = LoggerFactory.getLogger(RAccessorBuilder.class);
		
	/**
	 * Default Constructor 
	 **/
	public RAccessorBuilder() throws AccessorException{
		
		super(CoreConstants.BUILDER_REDIS);
		initial(); // initialize hbase access 
	}

	/**
	 * constructor 
	 * @param builderName 
	 * @param accessorMap 
	 **/
	public void initial() throws AccessorException{
		
		JedisUtils.initialPool();
	}
	
	@Override
	public void assembly(Principal principal,IBaseAccessor accessor) {
		
		// ignore		
	}

	@Override
	public void assembly(IBaseAccessor mockupAccessor,
			IBaseAccessor... accessors) throws AccessorException {
		// ignore
	}
}
