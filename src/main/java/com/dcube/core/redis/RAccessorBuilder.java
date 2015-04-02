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
import com.dcube.exception.EntityException;
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
	
	private JedisPoolConfig  config = null;
	
	private JedisPool jedisPool = null;
	
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
		
		config = new JedisPoolConfig();  
	    config.setMaxIdle(20000);  
	    config.setTestOnBorrow(true);  
	    config.setTestOnReturn(true);
	    try{    
 
	    	jedisPool = new JedisPool(config, "192.168.1.133", 6379 , 12000);  
        } catch(Exception e) {  
        	
        	LOGGER.error("Error when create JedisPool object",e); 
        } 
	}

	@Override
	public void assembly(Principal principal,IBaseAccessor accessor) {
		Jedis jedis = null;		
		if(accessor instanceof RedisAware){
			
			try {
				
				jedis = jedisPool.getResource();
				((RedisAware) accessor).setJedis(jedis);
				
			} catch (Exception e) {
				
				LOGGER.error("Error when assembly Accessor:set Jedis",e);
			}			
		}
				
	}

	@Override
	public void assembly(IBaseAccessor mockupAccessor,
			IBaseAccessor... accessors) throws EntityException {
		
		Jedis jedis = null;		
		for(IBaseAccessor accessor:accessors){
			
			if((mockupAccessor instanceof RedisAware) 
					&& (accessor instanceof RedisAware)){
				
				jedis = ((RedisAware) mockupAccessor).getJedis();
				((RedisAware) accessor).setJedis(jedis);		
			}

		}
	}
	
	/**
	 * Return the Jedis object 
	 **/
	public void returnJedis(Jedis jedis){
		
		jedisPool.returnResource(jedis);
	}
}
