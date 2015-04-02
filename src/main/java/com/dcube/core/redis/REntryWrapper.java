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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.dcube.core.CoreConstants;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityAttr;

public abstract class REntryWrapper<GB extends EntityEntry> {

	

	/**
	 * Wrap the rawentry into bean object
	 * 
	 * @param attrs the attributes of rawEntry
	 * @param key the entry key
	 * @param rawEntry the jedis object to retrieve data
	 * 
	 * @return GB the bean object. 
	 **/
	public abstract GB wrap(List<EntityAttr> attrs, String key, Jedis rawEntry) throws AccessorException;
	
	/**
	 * Parse bean object into raw Object
	 * 
	 * @param attrs the attributes of target entity
	 * @param jedis the jedis object to write entry
	 * @param entryInfo the entry information bean
	 * 
	 * @return Object the raw object. 
	 **/	
	public abstract void parse(List<EntityAttr> attrs,Jedis jedis, GB entryInfo) throws AccessorException;

	
}
