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
package com.obal.core.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.obal.core.CoreConstants;
import com.obal.core.accessor.RawEntry;
import com.obal.exception.AccessorException;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityConstants;
import com.obal.meta.EntityManager;
import com.obal.meta.EntityMeta;

/**
 * Hbase Raw entry wrapper in charge of hbase Result to object list conversion.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public class RRawWrapper extends REntryWrapper<RawEntry> {

	public static Logger LOGGER = LoggerFactory.getLogger(RRawWrapper.class);

	@Override
	public RawEntry wrap(String entityName, String key, Jedis rawEntry) throws AccessorException{
		String redisKey = entityName + CoreConstants.KEYS_SEPARATOR + key;
		Jedis entry = rawEntry;
		// not exist return null;
		if(!entry.exists(redisKey)){
			LOGGER.debug("The target[key:{}-{}] data not exist in Jedis.",entityName,key);
			return null;	
		}
		EntityMeta meta = EntityManager.getInstance().getEntityMeta(entityName);
		
		if(meta == null)
			throw new AccessorException("The meta data:{} not exists in EntityManager.",entityName);
		
		List<EntityAttr> attrs = meta.getAllAttrs();
		RawEntry gei = new RawEntry(entityName, key);
		
		for (EntityAttr attr : attrs) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Wrapping entity:{} - attribute:{}", entityName,
						attr.getAttrName());
			}
			Map<byte[], byte[]> cells = null;
			
			switch (attr.mode) {

			case PRIMITIVE:
				byte[] cell = entry.hget(redisKey.getBytes(), attr.getAttrName().getBytes());
				Object value = super.getPrimitiveValue(attr, cell);
				gei.put(attr.getAttrName(), value);
				break;
			case MAP:
				String mapkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
				cells = entry.hgetAll(mapkey.getBytes());
				Map<String, Object> map = super.getMapValue(attr, cells);
				gei.put(attr.getAttrName(), map);
				break;
			case LIST:
				String listkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
				Long llen = entry.llen(listkey.getBytes());
				List<byte[]> listcells = entry.lrange(listkey.getBytes(), 0,llen);
				List<Object> list = super.getListValue(attr, listcells);
				gei.put(attr.getAttrName(), list);
				break;

			case SET:
				String setkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
				Set<byte[]> setcells = entry.smembers(setkey.getBytes());

				Set<Object> set = super.getSetValue(attr, setcells);
				gei.put(attr.getAttrName(), set);
				break;

			default:
				break;

			}
		}

		return gei;
	}

	@Override
	public RawEntry wrap(List<EntityAttr> attrs, String key, Jedis rawEntry) throws AccessorException{
		
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
		RawEntry gei = new RawEntry(entityName, key);
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
					Object value = super.getPrimitiveValue(attr, cell);
					gei.put(attr.getAttrName(), value);
					break;
				case MAP:
					String mapkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					cells = entry.hgetAll(mapkey.getBytes());
					Map<String, Object> map = super.getMapValue(attr, cells);
					gei.put(attr.getAttrName(), map);
					break;
				case LIST:
					String listkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					Long llen = entry.llen(listkey.getBytes());
					List<byte[]> listcells = entry.lrange(listkey.getBytes(), 0,llen);
					List<Object> list = super.getListValue(attr, listcells);
					gei.put(attr.getAttrName(), list);
					break;
	
				case SET:
					String setkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					Set<byte[]> setcells = entry.smembers(setkey.getBytes());
	
					Set<Object> set = super.getSetValue(attr, setcells);
					gei.put(attr.getAttrName(), set);
					break;
				default:
					break;
			}
		}

		return gei;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parse(List<EntityAttr> attrs,Jedis jedis, RawEntry entryInfo)  throws AccessorException{

		if(entryInfo == null) 
			throw new AccessorException("entryInfo can not be null.");	
		
		if(attrs == null || attrs.size() == 0) 
			throw new AccessorException("Attributes can not be empty.");	
		
		String redisKey = entryInfo.getEntityName() + CoreConstants.KEYS_SEPARATOR + entryInfo.getKey();
			
		for (EntityAttr attr : attrs) {

			Object value = entryInfo.get(attr.getAttrName());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("-key:{} =>attr:{} - value:{}",new String[]{redisKey, attr.getAttrName(),String.valueOf(value)});
			}
			if (null == value)
				continue;

			switch (attr.mode) {

			case PRIMITIVE:
				super.putPrimitiveValue(jedis,redisKey, attr, value);
				break;
			case MAP:
				super.putMapValue(jedis, redisKey, attr, (Map<String, Object>) value);
				break;
			case LIST:
				super.putListValue(jedis, redisKey, attr, (List<Object>) value);
				break;
			case SET:
				super.putSetValue(jedis, redisKey, attr, (Set<Object>) value);
				break;
			default:
				break;

			}
		}
	}

}
