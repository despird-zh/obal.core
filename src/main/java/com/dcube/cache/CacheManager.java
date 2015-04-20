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
package com.dcube.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntityAccessor;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.security.Principal;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.disruptor.EventType;
import com.dcube.exception.AccessorException;
import com.dcube.util.AccessorUtils;

/**
 * CacheManager provide entrance to get/put entry in-out backend cache.
 * <p>
 * cache data <b>Put</b> operation runs in producer/consumer mode(disruptor), it's in asynchronized mode.
 * cache data <b>Get</b> operation runs in thread safe mode. entry be fetched directly.
 * </p>
 * 
 *  @author despird
 *  @version 0.1 2014-2-1
 *  @since 0.1
 *  
 **/
public class CacheManager{

	static Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);
	
	/** singleton instance */ 
	private static CacheManager instance;
	
	private static Map<EntryKey, CacheEntryPipe> entryPipeMap = null;
	
	/** default constructor */
	private CacheManager(){
		entryPipeMap = new ConcurrentHashMap<EntryKey, CacheEntryPipe>(20);
	}
	
	/**
	 * Get the instance of CacheManager 
	 * 
	 * @return CacheManager the singleton instance of manager.
	 * 
	 **/
	public static CacheManager getInstance(){
		
		if(null == instance)
			instance = new CacheManager();
		
		return instance;
	}
	
	/**
	 * Put entry into cache. Internally entry will be posted to cache asynchronously.
	 * the cached entry must be EntryKey subclass instance.
	 * 
	 * @param principal the principal 
	 * @param entry the entry object to be cached.
	 *  
	 **/
	public void cachePut(Principal principal, EntityEntry entry){
		
		CacheInfo data = new CacheInfo(principal);
		data.setPutEntryData(entry);
		
		offerCacheQueue(entry.getEntryKey(), data);
 
	}
	
	/**
	 * Put entry attribute data in cache. it need entity and key information, here they 
	 * are wrapped in entryKey object.
	 * 
	 * @param principal the principal 
	 * @param entryKey the entry key object to hold key and entity information
	 * @param attrName the attribute name
	 * @param value the attribute value object.
	 **/
	public void cachePutAttr(Principal principal, EntryKey entryKey, String attrName, Object value){
		
		CacheInfo data = new CacheInfo(principal);
		data.setPutAttrData(entryKey.getKey(), attrName, value);
		
		offerCacheQueue(entryKey, data);
 
	}
	
	/**
	 * Fetch entry from cache, the returned value must be the EntryKey subclass instance. 
	 * 
	 * @param principal the principal 
	 * @param key the key of entry data 
	 **/
	public EntityEntry cacheGet(Principal principal, EntryKey key){
		
		EntityEntry cacheData = null;
		IEntityAccessor<EntityEntry> eaccessor = null;
		try {
			
			eaccessor = AccessorFactory.buildCacheAccessor(principal, key.getEntityName());				
			cacheData = eaccessor.doGetEntry(key.getKey());
			
		} catch (AccessorException e) {
			
			LOGGER.error("Error when get entry data({})", key ,e);
		} finally{
			
			AccessorUtils.closeAccessor(eaccessor);
		}
		
		return cacheData;
	}

	/**
	 * Fetch entry from cache, the returned value must be the EntryKey subclass instance. 
	 * 
	 * @param principal the principal 
	 * @param key the key of entry data 
	 * @param attributes the attribute array
	 **/
	public EntityEntry cacheGet(Principal principal, EntryKey key, String... attributes){
		
		EntityEntry cacheData = null;
		IEntityAccessor<EntityEntry> eaccessor = null;
		try {
			
			eaccessor = AccessorFactory.buildCacheAccessor(principal, key.getEntityName());				
			cacheData = eaccessor.doGetEntry(key.getKey(), attributes);
			
		} catch (AccessorException e) {
			
			LOGGER.error("Error when get entry data({})", key.toString() +" -> "+ attributes.toString() ,e);
		} finally{
			
			AccessorUtils.closeAccessor(eaccessor);
		}
		
		return cacheData;
	}
	
	/**
	 * Fetch entry attribute from cache, the returned value wrap the List,SET, Map. 
	 * 
	 * @param principal the principal 
	 * @param entityName the entity name
	 * @param key the key of entry data 
	 * @param attrName the attribute name
	 * 
	 **/
	public <M> M cacheGetAttr(Principal principal, EntryKey key, String attrName){
	
		M cacheAttr = null;
		IEntityAccessor<?> eaccessor = null;
		try {
			
			eaccessor = AccessorFactory.buildCacheAccessor(principal, key.getEntityName());				
			cacheAttr = eaccessor.doGetEntryAttr(key.getKey(), attrName);
			
		} catch (AccessorException e) {
			
			LOGGER.error("Error when get entry data({})", key.toString() + " -> " + attrName ,e);
		} finally{
			
			AccessorUtils.closeAccessor(eaccessor);
		}
		
		return cacheAttr;
	}
	
	/**
	 * Delete entry from cache
	 * 
	 * @param principal the principal 
	 * @param entityName the entity name
	 * @param keys the string array of key
	 * 
	 **/
	public void cacheDel(Principal principal, String entityName, String ...keys){
		
		for(String key :keys){
			
			CacheInfo data = new CacheInfo(principal);			
			data.setDelEntryData(key);
			EntryKey entryKey = new EntryKey(entityName, key);
			offerCacheQueue(entryKey, data);
		}
	}
	
	/**
	 * Delete entry attribute from cache
	 * 
	 * @param principal the principal 
	 * @param entityName name of entity
	 * @param attribute the entry attribute
	 * @param keys the key array
	 **/
	public void cacheDelAttr(Principal principal, String entityName, String attribute, String... keys){
		for(String key :keys){
			
			CacheInfo data = new CacheInfo(principal);			
			data.setDelAttrData(key, attribute);
			EntryKey entryKey = new EntryKey(entityName, key);
			offerCacheQueue(entryKey, data);
		}
	}
	
	/**
	 * Add cacheInfo to queue
	 * 
	 * @param entryKey 
	 * @param data 
	 * 
	 * @return the queue contains cache info.
	 **/
	public void offerCacheQueue(EntryKey entryKey, CacheInfo data){
		
		CacheEntryPipe cachePipe = entryPipeMap.get(entryKey);
		if(cachePipe == null){// create new one
			
			cachePipe = new CacheEntryPipe(entryKey);
			entryPipeMap.put(entryKey, cachePipe);
			cachePipe.offer(data);
			
			EventDispatcher.getInstance().sendPayload(cachePipe,EventType.CACHE);
			
		}else if(cachePipe.isEmpty()){// since empty let cache hooker to drop it.
			
			cachePipe = new CacheEntryPipe(entryKey);
			entryPipeMap.put(entryKey, cachePipe);
			cachePipe.offer(data);
			
			EventDispatcher.getInstance().sendPayload(cachePipe,EventType.CACHE);
			
		}else{// not empty push data to existed queue, let hook digest it.
			
			cachePipe.offer(data);
		}
	}
	
	/**
	 * Drop CacheInfo queue from queue map. This to be called in CacheHooker to 
	 * clear cache queue.
	 * 
	 * @param entryPipe the queue to be removed
	 * 
	 * @return true: cache queue not exist in map; false: cache queue exist in map
	 **/
	public boolean dropCacheInfoQueue(CacheEntryPipe entryPipe){
		
		CacheEntryPipe newEntryPipe = entryPipeMap.get(entryPipe.getEntryKey());
		
		if(entryPipe.isEmpty() && entryPipe.equals(newEntryPipe)){
			
			entryPipeMap.remove(entryPipe.getEntryKey());
			return true;
			
		}else if(entryPipe.isEmpty() && !entryPipe.equals(newEntryPipe)){
			
			return true;
		}else{
			// not empty
			return false;
		}
	}
}
