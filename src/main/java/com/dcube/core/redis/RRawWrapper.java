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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.dcube.core.CoreConstants;
import com.dcube.core.EntryKey;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;

/**
 * Hbase Raw entry wrapper in charge of hbase Result to object list conversion.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public class RRawWrapper extends REntryWrapper<EntityEntry> {

	public static Logger LOGGER = LoggerFactory.getLogger(RRawWrapper.class);

	@Override
	public EntityEntry wrap(List<EntityAttr> attrs, String key, Jedis rawEntry) throws AccessorException{
		
		Jedis entry = rawEntry;

		String entityName = null;
		if(attrs == null || attrs.size()==0){
			
			throw new AccessorException("The attribute list is empty!");
		}else{
			
			entityName = attrs.get(0).getEntityName();
		}
		
		if (entityName == null || entityName.length() == 0) {
			entityName = EntityConstants.ENTITY_BLIND;
		}
		EntityEntry gei = new EntityEntry(entityName, key);
		String redisKey = entityName + CoreConstants.KEYS_SEPARATOR + key;
		// not exist return null;
		if(!entry.exists(redisKey)){
			LOGGER.debug("The target[key:{}-{}] data not exist in Jedis.",entityName,key);
			return null;	
		}		
		
		for (EntityAttr attr : attrs) {

			Map<byte[], byte[]> cells = null;
			switch (attr.mode) {

				case PRIMITIVE:
					byte[] cell = entry.hget(redisKey.getBytes(), attr.getAttrName().getBytes());
					Object value = REntryWrapperUtils.getPrimitiveValue(attr, cell);
					gei.setAttrValue(attr, value);
					break;
				case MAP:
					String mapkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					cells = entry.hgetAll(mapkey.getBytes());
					Map<String, Object> map = REntryWrapperUtils.getMapValue(attr, cells);
					gei.setAttrValue(attr, map);
					break;
				case LIST:
					String listkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					Long llen = entry.llen(listkey.getBytes());
					List<byte[]> listcells = entry.lrange(listkey.getBytes(), 0,llen);
					List<Object> list = REntryWrapperUtils.getListValue(attr, listcells);
					gei.setAttrValue(attr, list);
					break;
	
				case SET:
					String setkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					Set<byte[]> setcells = entry.smembers(setkey.getBytes());
	
					Set<Object> set = REntryWrapperUtils.getSetValue(attr, setcells);
					gei.setAttrValue(attr, set);
					break;
				default:
					break;
			}
		}

		return gei;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parse(List<EntityAttr> attrs,Jedis jedis, EntityEntry entryInfo)  throws AccessorException{

		if(entryInfo == null) 
			throw new AccessorException("entryInfo can not be null.");	
		
		if(attrs == null || attrs.size() == 0) 
			throw new AccessorException("Attributes can not be empty.");	
		EntryKey key = entryInfo.getEntryKey();
		String redisKey = key.getEntityName() + CoreConstants.KEYS_SEPARATOR + key.getKey();
			
		for (EntityAttr attr : attrs) {

			Object value = entryInfo.getAttrValue(attr.getAttrName());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("-key:{} =>attr:{} - value:{}",new String[]{redisKey, attr.getAttrName(),String.valueOf(value)});
			}
			if (null == value)
				continue;

			switch (attr.mode) {

			case PRIMITIVE:
				REntryWrapperUtils.putPrimitiveValue(jedis,redisKey, attr, value);
				break;
			case MAP:
				REntryWrapperUtils.putMapValue(jedis, redisKey, attr, (Map<String, Object>) value);
				break;
			case LIST:
				REntryWrapperUtils.putListValue(jedis, redisKey, attr, (List<Object>) value);
				break;
			case SET:
				REntryWrapperUtils.putSetValue(jedis, redisKey, attr, (Set<Object>) value);
				break;
			default:
				break;

			}
		}
	}

}
