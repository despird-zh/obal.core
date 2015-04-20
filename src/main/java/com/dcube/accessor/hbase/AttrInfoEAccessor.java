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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;
import com.dcube.meta.EntityAttr.AttrMode;
import com.dcube.meta.EntityAttr.AttrType;
import com.dcube.meta.EntityConstants.AttrEnum;

public class AttrInfoEAccessor extends HEntityAccessor<EntityEntry>{

	public static Logger LOGGER = LoggerFactory.getLogger(AttrInfoEAccessor.class);
	
	public AttrInfoEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_ATTR);		
	}

	@Override
	public EntityEntry newEntryObject() {
		
		return new EntityEntry();
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	public <To> IEntryConverter<EntityEntry, To> getEntryConverter(Class<To> cto){
		
		if(cto.equals(EntityAttr.class)){
			
			IEntryConverter<EntityEntry,EntityAttr> converter = new IEntryConverter<EntityEntry,EntityAttr>(){

				@Override
				public EntityAttr toTarget(EntityEntry fromObject)
						throws BaseException {
					
					String attrName = fromObject.getAttrValue(AttrEnum.AttrName.attribute,String.class);
					String column = fromObject.getAttrValue(AttrEnum.Column.attribute,String.class);
					String qualifier = fromObject.getAttrValue(AttrEnum.Qualifier.attribute,String.class);
					
					AttrType type = AttrType.valueOf(fromObject.getAttrValue(AttrEnum.Type.attribute,String.class));
					AttrMode mode = AttrMode.valueOf(fromObject.getAttrValue(AttrEnum.Mode.attribute,String.class));
					
					EntityAttr attr = new EntityAttr(attrName,mode,type,column,qualifier);
					attr.setEntryKey(fromObject.getEntryKey());
					attr.setEntityName(fromObject.getAttrValue(AttrEnum.Entity.attribute,String.class));
					attr.setDescription(fromObject.getAttrValue(AttrEnum.Description.attribute,String.class));
					attr.setFormat(fromObject.getAttrValue(AttrEnum.Format.attribute,String.class));
					attr.setHidden(fromObject.getAttrValue(AttrEnum.Hidden.attribute,Boolean.class));
					attr.setIndexable(fromObject.getAttrValue(AttrEnum.Indexable.attribute,Boolean.class));
					attr.setRequired(fromObject.getAttrValue(AttrEnum.Required.attribute,Boolean.class));
					attr.setReadonly(fromObject.getAttrValue(AttrEnum.Readonly.attribute,Boolean.class));

					return attr;
				}

				@Override
				public EntityEntry toSource(EntityAttr toObject)
						throws BaseException {
										
					EntityEntry minfo = new EntityEntry(toObject.getEntryKey());
					
					EntityMeta meta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_META_ATTR);
					
					minfo.setAttrValue(meta.getAttr(AttrEnum.AttrName.attribute), toObject.getAttrName());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Description.attribute), toObject.getDescription());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Format.attribute), toObject.getFormat());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Column.attribute), toObject.getColumn());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Qualifier.attribute), toObject.getQualifier());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Hidden.attribute), toObject.isHidden());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Indexable.attribute), toObject.isPrimitive());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Required.attribute), toObject.isRequired());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Readonly.attribute), toObject.isReadonly());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Type.attribute), toObject.type.toString());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Mode.attribute), toObject.mode.toString());
					minfo.setAttrValue(meta.getAttr(AttrEnum.Entity.attribute), toObject.getEntityName());
					
					return minfo;
				}
				
			};
			
			return (IEntryConverter<EntityEntry, To>) converter;
		}
		
		return super.getEntryConverter(cto);
	}
}
