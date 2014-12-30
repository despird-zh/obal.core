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
package com.obal.core.hbase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.EntryKey;
import com.obal.core.ITraceable;
import com.obal.exception.WrapperException;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityConstants;
import com.obal.meta.EntityManager;


/**
 * HEntryWrapper wrap or parse the java bean object.
 * 
 * @author despird-zh
 * @version 0.1 2014-3-2
 **/
public abstract class HEntryWrapper<GB extends EntryKey> {

	protected static ObjectMapper objectMapper = new ObjectMapper();
	
	public static Logger LOGGER = LoggerFactory.getLogger(HEntryWrapper.class);
	
	/**
	 * Get primitive value from cell, primitive means int,long,double,string,date
	 * 
	 * @param attr the attribute of entry
	 * @param cell the Cell of certain Row in hbase
	 * 
	 * @return Object the value object
	 **/
	public Object getPrimitiveValue(EntityAttr attr, byte[] value)throws WrapperException{
		
		Object rtv = null;

		switch(attr.type){
			case INTEGER:
				rtv = Bytes.toInt(value);
				break;
			case BOOL:
				rtv = Bytes.toBoolean(value);
				break;
			case DOUBLE:
				rtv = Bytes.toDouble(value);
				break;
			case LONG:
				rtv = Bytes.toLong(value);
				break;
			case STRING:
				rtv = Bytes.toString(value);
				break;
			case DATE:
				Long time = Bytes.toLong(value);
				rtv = new Date(time);
				break;
			default:
				break;
		}
		if(LOGGER.isDebugEnabled()){
			
			LOGGER.debug("PRIMITIVE -> attribute:{} | value:{}", new String[]{attr.getAttrName(),String.valueOf(rtv)});
		}
		return rtv;
	}

	/**
	 * Get map value from cells, every cell is the entry of map
	 * 
	 * @param attr the attribute of entry
	 * @param cells the Cells of certain Row in hbase
	 * 
	 * @return Object the map object
	 **/
	@SuppressWarnings("unchecked")
	public Map<String,Object> getMapValue(EntityAttr attr, byte[] value)throws WrapperException{
		Map<String,?> map = null;
		String jsonStr = Bytes.toString(value);
		try{
			ObjectReader oReader = null;
			switch(attr.type){
				case INTEGER:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Integer>>(){});					
					map = oReader.readValue(jsonStr);	
			
					break;
				case BOOL:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Boolean>>(){});					
					map = oReader.readValue(jsonStr);	
					
					break;
				case DOUBLE:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Double>>(){});					
					map = oReader.readValue(jsonStr);	
					break;
				case LONG:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Long>>(){});					
					map = oReader.readValue(jsonStr);	
					break;
				case STRING:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,String>>(){});					
					map = oReader.readValue(jsonStr);	
					break;
				case DATE:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Date>>(){});					
					map = oReader.readValue(jsonStr);	
					
					break;
				default:
					
					break;
			}
			
			if(LOGGER.isDebugEnabled()){
						
				LOGGER.debug("SET -> attribute:{} - value:{}", 
								new Object[]{attr.getAttrName(), jsonStr});
			}
			
		}catch(Exception e){
			
			throw new WrapperException("Error when wrap set value",e);
		}
				
		return (Map<String,Object>)map;
		
	}

	/**
	 * Get list value from cells, every cell is the entry of map
	 * 
	 * @param attr the attribute of entry
	 * @param cells the Cells of certain Row in hbase
	 * 
	 * @return Object the list object
	 **/
	@SuppressWarnings("unchecked")
	public List<Object> getListValue(EntityAttr attr, byte[] value)throws WrapperException{
		List<?> list = null;

		String jsonStr = Bytes.toString(value);
		try{
			ObjectReader oReader = null;
			switch(attr.type){
				case INTEGER:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Integer>>(){});					
					list = (jsonStr == null)? new ArrayList<Integer>() : (List<?>)oReader.readValue(jsonStr);					
					break;
					
				case BOOL:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Boolean>>(){});					
					list = (jsonStr == null)? new ArrayList<Boolean>() : (List<?>)oReader.readValue(jsonStr);	
					
					break;
				case DOUBLE:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Double>>(){});					
					list = (jsonStr == null)? new ArrayList<Double>() : (List<?>)oReader.readValue(jsonStr);	
					break;
				case LONG:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Long>>(){});					
					list = (jsonStr == null)? new ArrayList<Long>() : (List<?>)oReader.readValue(jsonStr);	
					break;
				case STRING:
					oReader=objectMapper.reader(new TypeReference<ArrayList<String>>(){});					
					list = (jsonStr == null)? new ArrayList<String>() : (List<?>)oReader.readValue(jsonStr);	
					break;
				case DATE:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Date>>(){});					
					list = (jsonStr == null)? new ArrayList<Date>() : (List<?>)oReader.readValue(jsonStr);	
					
					break;
				default:
					
					break;
			}
			
			if(LOGGER.isDebugEnabled()){
						
				LOGGER.debug("SET -> attribute:{} - value:{}", 
								new Object[]{attr.getAttrName(), jsonStr});
			}
			
		}catch(Exception e){
			
			throw new WrapperException("Error when wrap set value",e);
		}
				
		return (List<Object>)list;
		
	}


	/**
	 * Get Set value from cells, every cell is the element of set
	 * 
	 * @param attr the attribute of entry
	 * @param cells the Cells of certain Row in hbase
	 * 
	 * @return Object the list object
	 **/
	@SuppressWarnings("unchecked")
	public Set<Object> getSetValue(EntityAttr attr, byte[] value)throws WrapperException{
		
		Set<?> set = null;
		String jsonStr = Bytes.toString(value);
		try{
			ObjectReader oReader = null;
			switch(attr.type){
				case INTEGER:
					oReader=objectMapper.reader(new TypeReference<HashSet<Integer>>(){});					
					set = (jsonStr == null)? new HashSet<Integer>() : (Set<?>) oReader.readValue(jsonStr);
					
					break;
				case BOOL:
					oReader=objectMapper.reader(new TypeReference<HashSet<Boolean>>(){});					
					set = (jsonStr == null)? new HashSet<Boolean>() : (Set<?>) oReader.readValue(jsonStr);
					
					break;
				case DOUBLE:
					oReader=objectMapper.reader(new TypeReference<HashSet<Double>>(){});					
					set = (jsonStr == null)? new HashSet<Double>() : (Set<?>) oReader.readValue(jsonStr);
					break;
				case LONG:
					oReader=objectMapper.reader(new TypeReference<HashSet<Long>>(){});					
					set = (jsonStr == null)? new HashSet<Long>() : (Set<?>) oReader.readValue(jsonStr);
					break;
				case STRING:
					oReader=objectMapper.reader(new TypeReference<HashSet<String>>(){});					
					set = (jsonStr == null)? new HashSet<String>() : (Set<?>) oReader.readValue(jsonStr);
					break;
				case DATE:
					oReader=objectMapper.reader(new TypeReference<HashSet<Date>>(){});					
					set = (jsonStr == null)? new HashSet<Date>() : (Set<?>) oReader.readValue(jsonStr);
					
					break;
				default:
					
					break;
			}
			
			if(LOGGER.isDebugEnabled()){
						
				LOGGER.debug("SET -> attribute:{} - value:{}", 
								new Object[]{attr.getAttrName(), jsonStr});
			}
			
		}catch(Exception e){
			
			throw new WrapperException("Error when wrap set value",e);
		}
				
		return (Set<Object>)set;
	}
	
	/**
	 * Put the Primitive value to target Put operation
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 * 
	 **/
	public void putPrimitiveValue(Put put, EntityAttr attr, Object value){
		byte[] bval = null;
    	if(value == null) return;    	
    	switch(attr.type){
			case INTEGER:
				bval = Bytes.toBytes((Integer)value);
				break;
			case BOOL:
				bval = Bytes.toBytes((Boolean)value);
				break;
			case DOUBLE:
				bval = Bytes.toBytes((Double)value);
				break;
			case LONG:
				bval = Bytes.toBytes((Long)value);
				break;							
			case STRING:
				bval = Bytes.toBytes((String)value);
				break;
			case DATE:
				bval = Bytes.toBytes(((Date)value).getTime());
				break;						
			default:
				
				break;					
		}
    	put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
	}
	
	/**
	 * Put the map value to target Put operation object
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 **/
	public void putMapValue(Put put, EntityAttr attr, Map<String,Object> mapVal)throws WrapperException{
		byte[] bval = null;
    	if(mapVal == null) return;    	
		String mapJson = null;
		try{
			mapJson = objectMapper.writeValueAsString(mapVal);
			bval = mapJson.getBytes();
			put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
		}catch(Exception e){
			
			throw new WrapperException("Error when convert Map object to Json",e);
		} 
    	
	}

	/**
	 * Put the list value to target Put operation object
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 **/
	public void putListValue(Put put, EntityAttr attr, List<Object> listVal)throws WrapperException{
		byte[] bval = null;
		String listJson = null;
		if(listVal == null) return;
		try{
			listJson = objectMapper.writeValueAsString(listVal);
			bval = listJson.getBytes();
			put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
		}catch(Exception e){
			
			throw new WrapperException("Error when convert List object to Json",e);
		}   	
    	
	}
	

	/**
	 * Put the set value to target Put operation object
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 **/
	public void putSetValue(Put put, EntityAttr attr, Set<Object> setVal)throws WrapperException{
		byte[] bval = null;
		String setJson = null;
		if(setVal == null) return;
		try{
			setJson = objectMapper.writeValueAsString(setVal);
			bval = setJson.getBytes();
			put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
		}catch(Exception e){
			
			throw new WrapperException("Error when convert Set object to Json",e);
		}    	
    	
	}

	/**
	 * Wrap the rawentry into bean object
	 * 
	 * @param attrs the attributes of rawEntry
	 * @param rawEntry the entry information
	 * @return GB the bean object. 
	 **/
	public abstract GB wrap(List<EntityAttr> attrs, Result rawEntry) throws WrapperException;
	
	/**
	 * Parse bean object into raw Object
	 * 
	 * @param attrs the attributes of target entity
	 * @param entryInfo the entry information bean
	 * @return Object the raw object. 
	 **/	
	public abstract Put parse(List<EntityAttr> attrs, GB entryInfo)throws WrapperException;

	
	/**
	 * Wrap the rawentry into bean object
	 * @param entityName the entity name of rawEntry
	 * @param rawEntry the entry information
	 * 
	 * @return GB the bean object. 
	 **/
	public abstract GB wrap(String entityName, Result rawEntry)throws WrapperException;
	
	/**
	 * Wrap the trace infomation to traceable object
	 * @param traceInfo the object to be traceable
	 * @param rawEntry the Hbase Result object 
	 **/
	public void wrapTraceable(ITraceable traceInfo, Result rawEntry) throws WrapperException{
		
		List<EntityAttr> attrs = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_TRACEABLE).getAllAttrs();
		for(EntityAttr attr: attrs){
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = rawEntry.getValue(column, qualifier);
			if("i_creator".equals(attr.getAttrName())){
				String val = (String)getPrimitiveValue(attr, cell);
				traceInfo.setCreator(val);
				continue;
			}else if("i_modifier".equals(attr.getAttrName())){
				
				String mval = (String)getPrimitiveValue(attr, cell);
				traceInfo.setModifier(mval);
				continue;
			}else if("i_newcreate".equals(attr.getAttrName())){
				
				Date val = (Date)getPrimitiveValue(attr, cell);
				traceInfo.setNewCreate(val);
				continue;
			}else if("i_lastmodify".equals(attr.getAttrName())){
				
				Date val = (Date)getPrimitiveValue(attr, cell);
				traceInfo.setLastModify(val);
				continue;
			}
		}
	}
	
	/**
	 * Parse the traceable data into Put 
	 * 
	 * @param put the Put operation
	 * @param traceInfo the traceable object
	 **/
	public void parseTraceable(Put put, ITraceable traceInfo){
		
		List<EntityAttr> attrs = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_TRACEABLE).getAllAttrs();
		for(EntityAttr attr: attrs){
			
			if("i_creator".equals(attr.getAttrName())){
				Object val = traceInfo.getCreator();
				putPrimitiveValue(put, attr, val);
				continue;
			}else if("i_modifier".equals(attr.getAttrName())){
				
				Object val = traceInfo.getModifier();
				putPrimitiveValue(put, attr, val);
				continue;
			}else if("i_newcreate".equals(attr.getAttrName())){
				
				Object val = traceInfo.getNewCreate();
				putPrimitiveValue(put, attr, val);
				continue;
			}else if("i_lastmodify".equals(attr.getAttrName())){
				
				Object val = traceInfo.getLastModify();
				putPrimitiveValue(put, attr, val);
				continue;
			}
		}
	}
}
