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
package com.dcube.core.hbase;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.accessor.EntityEntry;
import com.dcube.exception.WrapperException;
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
public class HRawWrapper extends HEntryWrapper<EntityEntry>{

	public static Logger LOGGER = LoggerFactory.getLogger(HRawWrapper.class);
	
	@Override
	public EntityEntry wrap(List<EntityAttr> attrs,Result rawEntry) throws WrapperException{
						
		Result entry = (Result)rawEntry;
		String entityName = attrs.size()>0? (attrs.get(0).getEntityName()):EntityConstants.ENTITY_BLIND;
		if(entityName == null || entityName.length()==0){
			
			entityName = EntityConstants.ENTITY_BLIND;
		}
		EntityEntry gei = new EntityEntry(entityName,new String(entry.getRow()));
		
		for(EntityAttr attr: attrs){
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = entry.getValue(column, qualifier);
			switch(attr.mode){
			
				case PRIMITIVE :
				
					Object value = (cell== null)? null: HEntryWrapperUtils.getPrimitiveValue(attr, cell);
					gei.setAttrValue(attr, value);	
					break;
					
				case MAP :
					
					Map<String, Object> map = (cell== null)? null: HEntryWrapperUtils.getJMapValue(attr, cell);				
					gei.setAttrValue(attr, map);
					break;
					
				case LIST :
					
					List<Object> list = (cell== null)? null: HEntryWrapperUtils.getJListValue(attr, cell);					
					gei.setAttrValue(attr, list);
					break;
					
				case SET :
					
					Set<Object> set = (cell== null)? null: HEntryWrapperUtils.getJSetValue(attr, cell);					
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
	public Put parse(List<EntityAttr> attrs,EntityEntry entryInfo) throws WrapperException{
		Put put = new Put(entryInfo.getEntryKey().getKey().getBytes());

        for(EntityAttr attr:attrs){

        	Object value = entryInfo.getAttrValue(attr.getAttrName());
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("Put -> attribute:{} - value:{}",attr.getAttrName(),value);
        	}
        	if(null == value) continue;
        	
        	switch(attr.mode){
        	
        		case PRIMITIVE:
        			HEntryWrapperUtils.putPrimitiveValue(put, attr, value);					
        			break;
        		case MAP:
        			HEntryWrapperUtils.putJMapValue(put, attr, (Map<String,Object>)value);	
        			break;
        		case LIST:
        			HEntryWrapperUtils.putJListValue(put, attr, (List<Object>)value);	
        			
        			break;
        		case SET:
        			HEntryWrapperUtils.putJSetValue(put, attr, (Set<Object>)value);				
        			break;
        		default:
        			break;
        	
        	}
        }
        return put;
	}

}
