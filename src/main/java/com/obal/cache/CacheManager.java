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

import com.obal.core.AccessorFactory;
import com.obal.core.CoreConstants;
import com.obal.core.EntryKey;
import com.obal.core.IEntityAccessor;
import com.obal.core.security.Principal;
import com.obal.disruptor.EventDispatcher;
import com.obal.disruptor.EventType;
import com.obal.exception.AccessorException;
import com.obal.exception.EntityException;
import com.obal.util.AccessorUtils;

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

	/** singleton instance */ 
	private static CacheManager instance;
	
	/** default constructor */
	private CacheManager(){

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
	 * @param entry the entry object to be cached.
	 *  
	 **/
	public <K extends EntryKey> void cachePut(K entry){
		
		CacheInfo data = new CacheInfo();
		data.setPutEntryData(entry);
		
		EventDispatcher.getInstance().sendPayload(data,EventType.CACHE);
 
	}
	
	/**
	 * Put entry attribute data in cache. it need entity and key information, here they 
	 * are wrapped in entryKey object.
	 * 
	 * @param entryKey the entry key object to hold key and entity information
	 * @param attrName the attribute name
	 * @param value the attribute value object.
	 **/
	public void cachePutAttr(EntryKey entryKey, String attrName, Object value){
		
		CacheInfo data = new CacheInfo();
		data.setPutAttrData(entryKey.getKey(), entryKey.getEntityName(), attrName, value);
		
		EventDispatcher.getInstance().sendPayload(data,EventType.CACHE);
 
	}
	
	/**
	 * Fetch entry from cache, the returned value must be the EntryKey subclass instance. 
	 * 
	 * @param entityName the entity name
	 * @param key the key of entry data 
	 **/
	public <K extends EntryKey> K cacheGet(String entityName, String key){
		
		Principal principal = null;		
		K cacheData = null;
		IEntityAccessor<K> eaccessor = null;
		try {
			eaccessor = 
				AccessorFactory.getInstance().buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						entityName);	
				
			cacheData = eaccessor.doGetEntry(key);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			
			AccessorUtils.releaseAccessor(eaccessor);
		}
		
		return cacheData;
		//return (K)cacheBridge.doCacheGet(entityName, key);
	}

	/**
	 * Fetch entry attribute from cache, the returned value wrap the List,SET, Map. 
	 * 
	 * @param entityName the entity name
	 * @param key the key of entry data 
	 * @param attrName the attribute name
	 * 
	 **/
	public <M> M cacheGetAttr(String entityName, String key, String attrName){
		Principal principal = null;		
		M cacheAttr = null;
		IEntityAccessor<?> eaccessor = null;
		try {
			eaccessor = 
				AccessorFactory.getInstance().buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						entityName);	
				
			cacheAttr = eaccessor.doGetEntryAttr(key, attrName);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			
			eaccessor.release();
		}
		
		return cacheAttr;
		//return (M)cacheBridge.doCacheGetAttr(entityName, key, attrName);
	}
	
	/**
	 * Delete entry from cache
	 * 
	 * @param entityName the entity name
	 * @param keys the string array of key
	 * 
	 **/
	public void cacheDel(String entityName, String ...keys){
		
		CacheInfo data = new CacheInfo();
		data.setDelData(entityName, keys);
		
		EventDispatcher.getInstance().sendPayload(data,EventType.CACHE);
	
	}
	
	
}
