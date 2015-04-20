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
package com.dcube.accessor.hbase;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;
import com.dcube.meta.EntityConstants.MetaEnum;

public class MetaInfoEAccessor extends HEntityAccessor<EntityEntry>{

	public MetaInfoEAccessor() {
		
		super(EntityConstants.ACCESSOR_ENTITY_META);
	}

	public static Logger LOGGER = LoggerFactory.getLogger(MetaInfoEAccessor.class);

	@Override
	public TraceableEntry newEntryObject() {
		
		return new TraceableEntry();
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	public <To> IEntryConverter<EntityEntry, To> getEntryConverter(Class<To> cto){
		
		if(cto.equals(EntityMeta.class)){
			
			IEntryConverter<EntityEntry,EntityMeta> converter = new IEntryConverter<EntityEntry,EntityMeta>(){

				@Override
				public EntityMeta toTarget(EntityEntry fromObject)
						throws BaseException {

					EntityMeta meta = new EntityMeta(null);// entity is unknown yet
					meta.setEntryKey(fromObject.getEntryKey());
					meta.setEntityClass(fromObject.getAttrValue(MetaEnum.EntityClass.attribute,String.class));
					meta.setAccessorName(fromObject.getAttrValue(MetaEnum.AccessorName.attribute,String.class));
					meta.setDescription(fromObject.getAttrValue(MetaEnum.Description.attribute,String.class));
					meta.setEntityName(fromObject.getAttrValue(MetaEnum.EntityName.attribute,String.class));
					meta.setSchema(fromObject.getAttrValue(MetaEnum.Schema.attribute,String.class));	
					meta.setTraceable(fromObject.getAttrValue(MetaEnum.Traceable.attribute,Boolean.class));
					meta.setCategory(fromObject.getAttrValue(MetaEnum.Category.attribute,String.class));
					// here not set the EntityAttr yet, they'll be set outside.					
					return meta;
				}

				@Override
				public EntityEntry toSource(EntityMeta toObject)
						throws BaseException {
					
					EntryKey key = toObject.getEntryKey();
					EntityEntry minfo = new EntityEntry(key);
					EntityMeta emeta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_META_INFO);
					
					minfo.setAttrValue(emeta.getAttr(MetaEnum.EntityName.attribute), toObject.getEntityName());
					minfo.setAttrValue(emeta.getAttr(MetaEnum.EntityClass.attribute), toObject.getEntityClass());
					minfo.setAttrValue(emeta.getAttr(MetaEnum.AccessorName.attribute), toObject.getAccessorName());
					minfo.setAttrValue(emeta.getAttr(MetaEnum.Description.attribute), toObject.getDescription());
					minfo.setAttrValue(emeta.getAttr(MetaEnum.Traceable.attribute), toObject.getTraceable());
					minfo.setAttrValue(emeta.getAttr(MetaEnum.Schema.attribute), toObject.getSchema());
					minfo.setAttrValue(emeta.getAttr(MetaEnum.Category.attribute), toObject.getCategory());
					
					return minfo;
				}
				
			};
			
			return (IEntryConverter<EntityEntry, To>) converter;
		}
		
		return super.getEntryConverter(cto);
	}
}
