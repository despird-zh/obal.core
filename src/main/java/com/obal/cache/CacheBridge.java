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
package com.obal.cache;

import com.lmax.disruptor.EventHandler;

/**
 * Cache bridge handle the in-out exchange of data cache. In cache manager we use disruptor to handle 
 * the cache requests, so it provides EventHandler for disruptor. 
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * @since 0.1
 **/
public interface CacheBridge<K> {
	
	/**
	 * Get the event handler instance to swallow the disruptor ringbuffer event. 
	 **/
	public EventHandler<CacheInfo> getEventHandler();
	
	/**
	 * Put the entry data into cache
	 * 
	 * @param entry the entry data
	 **/
	public void doCachePut(K entry);
	
	/**
	 * Put the entry attribute data into cache
	 * 
	 * @param key the entry key
	 * @param entity the entity name
	 * @param attrName the attribute name
	 * @param value the attribute value
	 * 
	 **/
	public void doCachePutAttr(String key, String entity,  String attrName, Object value);
	
	/**
	 * Get the entry data from cache 
	 * 
	 * @param entityName the entity name
	 * @param key the key of entry
	 **/
	public K doCacheGet(String entityName, String key); 

	/**
	 * Get the entry data from cache 
	 * 
	 * @param entityName the entity name
	 * @param key the key of entry 
	 * @param attrName the attribute name
	 **/
	public <M> M doCacheGetAttr(String entityName, String key, String attrName); 
	
	/**
	 * Delete the entry from cache
	 * 
	 * @param entityName the entity name
	 * @param keys the array of key
	 **/
	public void doCacheDel(String entityName, String ... keys);
}
