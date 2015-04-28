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
import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntityEntry;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityAccessor;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.exception.AccessorException;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityAttr.AttrMode;

public abstract class REntityAccessor <GB extends IEntityEntry> extends EntityAccessor<GB> implements RedisAware{

	Logger LOGGER = LoggerFactory.getLogger(REntityAccessor.class);
		
	public REntityAccessor(String accessorName, AccessorContext context) {
		super(accessorName, context);
	}
	
	@Override
	public EntryKey doPutEntry(GB entryInfo, boolean changedOnly) throws AccessorException {

		EntryKey rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		Jedis jedis = null;
		try{
			jedis = this.borrowJedis();
			parse(entitySchema.getEntityMeta().getAllAttrs(), jedis, entryInfo);
		}finally{
			this.returnJedis(jedis);
		}
		rtv = entryInfo.getEntryKey();

		return rtv;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EntryKey doPutEntryAttr(String entryKey, String attrName, Object value)
			throws AccessorException {
		EntryKey rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		EntityAttr attr = entitySchema.getEntityMeta().getAttr(attrName);

		if(LOGGER.isDebugEnabled()){
            LOGGER.debug("--==>>attr:{} - value:{}",attr.getAttrName(),value);
        }
        String redisKey = entitySchema.getEntityName() + CoreConstants.KEYS_SEPARATOR + entryKey;
		Jedis jedis = null;
		try{
			jedis = this.borrowJedis();
	        switch(attr.mode){
	        
	            case PRIMITIVE:
	            	RWrapperUtils.putPrimitiveValue(jedis,redisKey, attr, value);
	            	break;
	            case MAP:
	            	if(!(value instanceof Map<?,?>))
	        			throw new AccessorException("the attr:{} value is not Map object",attrName);        		
	            	RWrapperUtils.putMapValue(jedis,redisKey, attr, (Map<String,Object>)value);	
	        		break;
	            case LIST:
	            	if(!(value instanceof List<?>))
	        			throw new AccessorException("the attr:{} value is not List object",attrName);        		
	            	RWrapperUtils.putListValue(jedis,redisKey, attr, (List<Object>)value);	
	        		break;
	            case SET:
	            	if(!(value instanceof List<?>))
	        			throw new AccessorException("the attr:{} value is not List object",attrName);        		
	            	RWrapperUtils.putSetValue(jedis, redisKey, attr, (Set<Object>)value);	
	        		break;
	            default:
	            	break;      	
	        }
		}finally{
			this.returnJedis(jedis);
		}
		return rtv;
	}

	@Override
	public GB doGetEntry(String entryKey) throws AccessorException {
		GB rtv = newEntryObject();
		BaseEntity entrySchema = (BaseEntity)getEntitySchema();
		Jedis jedis = null;
		try{
			jedis = this.borrowJedis();
			wrap(entrySchema.getEntityMeta().getAllAttrs(),entryKey, jedis, rtv);
		}finally{
			this.returnJedis(jedis);
		}
		return rtv;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> K doGetEntryAttr(String entryKey, String attrName)
			throws AccessorException {
		Object rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		EntityAttr attr = entitySchema.getEntityMeta().getAttr(attrName);

    	String redisKey = entitySchema.getEntityName() + CoreConstants.KEYS_SEPARATOR + entryKey;
		Jedis jedis = null;
		try{
			jedis = this.borrowJedis();
	    	switch(attr.mode){
		    	case PRIMITIVE:
		    		byte[] cell = jedis.hget(redisKey.getBytes(), attr.getAttrName().getBytes());
					rtv = RWrapperUtils.getPrimitiveValue(attr, cell);
		    		break;
		    	case MAP:
		    		redisKey += CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
		        	Map<byte[], byte[]> cells = jedis.hgetAll(redisKey.getBytes());
					rtv = RWrapperUtils.getMapValue(attr, cells);		    		
		    		break;
		    	case LIST:
		    		redisKey += CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
		    		long len = jedis.llen(redisKey);
		    		List<byte[]> celllist = jedis.lrange(redisKey.getBytes(), 0, len);
					rtv = RWrapperUtils.getListValue(attr, celllist);		    		
		    		break;
		    	case SET:
		    		redisKey += CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
		    		Set<byte[]> cellset = jedis.smembers(redisKey.getBytes());
					rtv = RWrapperUtils.getSetValue(attr, cellset);	
		    		break;
		    	default:
		    		break;
	    	}
		}finally{
			this.returnJedis(jedis);
		}
		return (K)rtv;
	}

	@Override
	public void doRemoveEntry(String... entryKeys) throws AccessorException {
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		// get non-primitive attributes
		List<EntityAttr> attrs = entitySchema.getEntityMeta().getAttrs(false);
		
		for(String entrykey:entryKeys){
			
			String redisKey = entitySchema.getEntityName()+ CoreConstants.KEYS_SEPARATOR + entrykey;
			Jedis jedis = null;
			try{
				jedis = this.borrowJedis();
				// delete primitive data
				jedis.del(redisKey); 
				for(EntityAttr attr:attrs){
					// delete non-primitive data
					jedis.del(redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName());
				}
			}finally{
				this.returnJedis(jedis);
			}
		}
	}

	/**
	 * Since we use redis mostly for the chache of hot data purpose, so the scan implementation most time 
	 * is ignored. get/put by key operations is enough.
	 *  
	 **/
	@Deprecated
	@Override
	public EntryCollection<GB> doScanEntry(EntryFilter<?> scanfilter)
			throws AccessorException {
		
		throw new UnsupportedOperationException("Jedis Accessor not support Scan Operation.");
	}

	@Override
	public boolean isFilterSupported(EntryFilter<?> scanfilter, boolean throwExcep)
			throws AccessorException {
		
		return false;
	}

	@Override
	public GB doGetEntry(String entryKey, String... attributes)
			throws AccessorException {
		GB rtv = newEntryObject();
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		List<EntityAttr> attrs = entitySchema.getEntityMeta().getAttrs(attributes);
		Jedis jedis = null;
		try{
			jedis = this.borrowJedis();
			wrap(attrs,entryKey, jedis, rtv);
		}finally{
			this.returnJedis(jedis);
		}
		return rtv;
	}

	@Override
	public void doRemoveEntryAttr(String attribute, String... entryKeys)
			throws AccessorException {
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		// get non-primitive attributes
		EntityAttr attr = entitySchema.getEntityMeta().getAttr(attribute);
		Jedis jedis = null;
		try{
			jedis = this.borrowJedis();
			for(String entrykey:entryKeys){
				
				String redisKey = entitySchema.getEntityName()+ CoreConstants.KEYS_SEPARATOR + entrykey;
						
				if(attr.mode != AttrMode.PRIMITIVE){
					// delete non-primitive data
					jedis.del(redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName());
				}else{
					// delete primitive data	
					jedis.del(redisKey); 
				}
			}
		}finally{
			this.returnJedis(jedis);
		}
	}

	@Deprecated
	@Override
	public EntryCollection<GB> doScanEntry(EntryFilter<?> scanfilter,
			String... attributes) throws AccessorException {
		
		throw new UnsupportedOperationException("Jedis Accessor not support Scan Operation.");
	}

	public void wrap(List<EntityAttr> attrs, String key, Jedis rawEntry, GB entryInfo) throws AccessorException{
		
		Jedis jedis = rawEntry;

		String entityName = null;
		if(attrs == null || attrs.size()==0){
			
			throw new AccessorException("The attribute list is empty!");
		}else{
			
			entityName = attrs.get(0).getEntityName();
		}
		
		if (entityName == null || entityName.length() == 0) {
			entityName = EntityConstants.ENTITY_BLIND;
		}
		String redisKey = entityName + CoreConstants.KEYS_SEPARATOR + key;
		// not exist return null;
		if(!jedis.exists(redisKey)){
			LOGGER.debug("The target[key:{}-{}] data not exist in Jedis.",entityName,key);
			return;
		}		
		
		for (EntityAttr attr : attrs) {

			Map<byte[], byte[]> cells = null;
			switch (attr.mode) {

				case PRIMITIVE:
					byte[] cell = jedis.hget(redisKey.getBytes(), attr.getAttrName().getBytes());
					Object value = RWrapperUtils.getPrimitiveValue(attr, cell);
					entryInfo.setAttrValue(attr, value);
					break;
				case MAP:
					String mapkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					cells = jedis.hgetAll(mapkey.getBytes());
					Map<String, Object> map = RWrapperUtils.getMapValue(attr, cells);
					entryInfo.setAttrValue(attr, map);
					break;
				case LIST:
					String listkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					Long llen = jedis.llen(listkey.getBytes());
					List<byte[]> listcells = jedis.lrange(listkey.getBytes(), 0,llen);
					List<Object> list = RWrapperUtils.getListValue(attr, listcells);
					entryInfo.setAttrValue(attr, list);
					break;
	
				case SET:
					String setkey = redisKey + CoreConstants.KEYS_SEPARATOR + attr.getAttrName();
					Set<byte[]> setcells = jedis.smembers(setkey.getBytes());
	
					Set<Object> set = RWrapperUtils.getSetValue(attr, setcells);
					entryInfo.setAttrValue(attr, set);
					break;
				default:
					break;
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void parse(List<EntityAttr> attrs,Jedis jedis, GB entryInfo)  throws AccessorException{

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
				RWrapperUtils.putPrimitiveValue(jedis,redisKey, attr, value);
				break;
			case MAP:
				RWrapperUtils.putMapValue(jedis, redisKey, attr, (Map<String, Object>) value);
				break;
			case LIST:
				RWrapperUtils.putListValue(jedis, redisKey, attr, (List<Object>) value);
				break;
			case SET:
				RWrapperUtils.putSetValue(jedis, redisKey, attr, (Set<Object>) value);
				break;
			default:
				break;

			}
		}
	}
	
	@Override
	public void returnJedis(Jedis jedis){

		try {
			JedisUtils.returnJedis(jedis);
		} catch (AccessorException e) {

			LOGGER.error("Fail return jedis.",e);
		}
	}

	@Override
	public Jedis borrowJedis() {
		Jedis jedis = null;
		try {
			jedis = JedisUtils.borrowJedis();
		} catch (AccessorException e) {

			LOGGER.error("Fail return jedis.",e);
		}
		return jedis;
	}

	@Override
	public void close(){
		try {

			super.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
