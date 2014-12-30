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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.accessor.RawEntry;
import com.obal.exception.WrapperException;
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
public class HRawWrapper extends HEntryWrapper<RawEntry>{

	public static Logger LOGGER = LoggerFactory.getLogger(HRawWrapper.class);

	@Override
	public RawEntry wrap(String entityName,Result rawEntry) throws WrapperException{
						
		Result entry = (Result)rawEntry;	
		EntityMeta meta = EntityManager.getInstance().getEntityMeta(entityName);
		
		List<EntityAttr> attrs = meta.getAllAttrs();
		
		RawEntry gei = new RawEntry(entityName,new String(entry.getRow()));
		
		for(EntityAttr attr: attrs){
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Wrapping entity:{} - attribute:{}",entityName, attr.getAttrName());
			}
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = entry.getValue(column, qualifier);
			switch(attr.mode){
			
				case PRIMITIVE :
					
					Object value = super.getPrimitiveValue(attr, cell);
					gei.put(attr.getAttrName(), value);	
					break;
					
				case MAP :
					
					Map<String, Object> map = super.getMapValue(attr, cell);				
					gei.put(attr.getAttrName(), map);
					break;
					
				case LIST :
					
					List<Object> list = super.getListValue(attr, cell);					
					gei.put(attr.getAttrName(), list);
					break;
					
				case SET :
					
					Set<Object> set = super.getSetValue(attr, cell);					
					gei.put(attr.getAttrName(), set);
					break;
					
				default:
					break;
				
			}
		}
		
		return gei;
	}
	
	@Override
	public RawEntry wrap(List<EntityAttr> attrs,Result rawEntry) throws WrapperException{
						
		Result entry = (Result)rawEntry;
		String entityName = attrs.size()>0? (attrs.get(0).getEntityName()):EntityConstants.ENTITY_BLIND;
		if(entityName == null || entityName.length()==0){
			
			entityName = EntityConstants.ENTITY_BLIND;
		}
		RawEntry gei = new RawEntry(entityName,new String(entry.getRow()));
		
		for(EntityAttr attr: attrs){
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = entry.getValue(column, qualifier);
			switch(attr.mode){
			
				case PRIMITIVE :
				
					Object value = super.getPrimitiveValue(attr, cell);
					gei.put(attr.getAttrName(), value);	
					break;
					
				case MAP :
					
					Map<String, Object> map = super.getMapValue(attr, cell);				
					gei.put(attr.getAttrName(), map);
					break;
					
				case LIST :
					
					List<Object> list = super.getListValue(attr, cell);					
					gei.put(attr.getAttrName(), list);
					break;
					
				case SET :
					
					Set<Object> set = super.getSetValue(attr, cell);					
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
	public Put parse(List<EntityAttr> attrs,RawEntry entryInfo) throws WrapperException{
		Put put = new Put(entryInfo.getKeyBytes());

        for(EntityAttr attr:attrs){

        	Object value = entryInfo.get(attr.getAttrName());
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("-=>parsing attr:{} - value:{}",attr.getAttrName(),value);
        	}
        	if(null == value) continue;
        	
        	switch(attr.mode){
        	
        		case PRIMITIVE:
        			super.putPrimitiveValue(put, attr, value);					
        			break;
        		case MAP:
        			super.putMapValue(put, attr, (Map<String,Object>)value);	
        			break;
        		case LIST:
        			super.putListValue(put, attr, (List<Object>)value);	
        			
        			break;
        		case SET:
        			super.putSetValue(put, attr, (Set<Object>)value);				
        			break;
        		default:
        			break;
        	
        	}
        }
        return put;
	}

}
