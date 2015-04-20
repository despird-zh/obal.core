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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.accessor.IMetaGAccessor;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.hbase.HGenericAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.exception.BaseException;
import com.dcube.exception.EntityException;
import com.dcube.exception.MetaException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityMeta;
import com.dcube.meta.EntityAttr.AttrMode;
import com.dcube.meta.EntityAttr.AttrType;
import com.dcube.meta.EntityConstants.AttrEnum;
import com.dcube.meta.EntityConstants.MetaEnum;
import com.dcube.util.AccessorUtils;

/**
 *  
 * @author despird 
 * @version 0.1 2014-10-2
 * 
 * @version 0.2 2014-12-1 Add category attribute to entity.meta.info table.
 * 
 **/
public class MetaGAccessor extends HGenericAccessor implements IMetaGAccessor{

	public MetaGAccessor() {
		
		super(EntityConstants.ACCESSOR_GENERIC_META);
	}

	static Logger LOGGER = LoggerFactory.getLogger(MetaGAccessor.class);
	
	@Override
	public EntityAttr getEntityAttr(String attrKey) throws AccessorException {
		
		AttrInfoEAccessor attraccessor = null;
		EntityAttr attr = null;
		try{
			attraccessor = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);

			EntityEntry minfo = attraccessor.doGetEntry(attrKey);
		
			String attrName = minfo.getAttrValue(AttrEnum.AttrName.attribute,String.class);
			String column = minfo.getAttrValue(AttrEnum.Column.attribute,String.class);
			String qualifier = minfo.getAttrValue(AttrEnum.Qualifier.attribute,String.class);
			
			AttrType type = AttrType.valueOf(minfo.getAttrValue(AttrEnum.Type.attribute,String.class));
			AttrMode mode = AttrMode.valueOf(minfo.getAttrValue(AttrEnum.Mode.attribute,String.class));
			
			attr = new EntityAttr(attrName,mode,type,column,qualifier);
			attr.setEntryKey(minfo.getEntryKey());
			attr.setEntityName(minfo.getAttrValue(AttrEnum.Entity.attribute,String.class));
			attr.setDescription(minfo.getAttrValue(AttrEnum.Description.attribute,String.class));
			attr.setFormat(minfo.getAttrValue(AttrEnum.Format.attribute,String.class));
			attr.setHidden(minfo.getAttrValue(AttrEnum.Hidden.attribute,Boolean.class));
			attr.setIndexable(minfo.getAttrValue(AttrEnum.Indexable.attribute,Boolean.class));
			attr.setRequired(minfo.getAttrValue(AttrEnum.Required.attribute,Boolean.class));
			attr.setReadonly(minfo.getAttrValue(AttrEnum.Readonly.attribute,Boolean.class));

		}catch(AccessorException ee){
			
			throw new AccessorException("Error when build embed accessor:{}",ee,EntityConstants.ENTITY_META_ATTR);
		}finally{
			
			AccessorUtils.closeAccessor(attraccessor);
		}
		return attr;

	}

	@Override
	public List<EntityAttr> getAttrList(String entityName) throws AccessorException {
		
		Filter filter1 = new RowFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(entityName.getBytes()));
		AttrInfoEAccessor attraccessor = null;
		EntryCollection<EntityEntry> attrs = null;
		List<EntityAttr> rtv = null;
		try{
			attraccessor = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
		
			attrs = attraccessor.doScanEntry(new EntryFilter<Filter>(filter1));
			
			rtv = new ArrayList<EntityAttr>();
			for(EntityEntry minfo:attrs){
	
				String attrName = minfo.getAttrValue(AttrEnum.AttrName.attribute,String.class);
				String column = minfo.getAttrValue(AttrEnum.Column.attribute,String.class);
				String qualifier = minfo.getAttrValue(AttrEnum.Qualifier.attribute,String.class);
				
				AttrType type = AttrType.valueOf(minfo.getAttrValue(AttrEnum.Type.attribute,String.class));
				AttrMode mode = AttrMode.valueOf(minfo.getAttrValue(AttrEnum.Mode.attribute,String.class));
				
				EntityAttr attr = new EntityAttr(attrName,mode,type,column,qualifier);
				attr.setEntityName(minfo.getAttrValue(AttrEnum.Entity.attribute,String.class));
				attr.setDescription(minfo.getAttrValue(AttrEnum.Description.attribute,String.class));
				attr.setFormat(minfo.getAttrValue(AttrEnum.Format.attribute,String.class));
				attr.setHidden(minfo.getAttrValue(AttrEnum.Hidden.attribute,Boolean.class));
				attr.setIndexable(minfo.getAttrValue(AttrEnum.Indexable.attribute,Boolean.class));
				attr.setRequired(minfo.getAttrValue(AttrEnum.Required.attribute,Boolean.class));
				attr.setReadonly(minfo.getAttrValue(AttrEnum.Readonly.attribute,Boolean.class));
				
				rtv.add(attr);
			}
		}catch(AccessorException ee){
			
		}finally{
			
			AccessorUtils.closeAccessor(attraccessor);
		}
		return rtv;
	}

	@Override
	public EntryKey putEntityAttr(EntityAttr attr) throws AccessorException {
		AttrInfoEAccessor attraccessor = null;
		
		try {
			attraccessor = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
			EntryKey key = attraccessor.getEntitySchema().newKey(getContext().getPrincipal());
			EntityEntry minfo = new EntityEntry(key);
			EntityMeta meta = attraccessor.getEntitySchema().getEntityMeta();
			minfo.setAttrValue(meta.getAttr(AttrEnum.AttrName.attribute), attr.getAttrName());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Description.attribute), attr.getDescription());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Format.attribute), attr.getFormat());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Column.attribute), attr.getColumn());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Qualifier.attribute), attr.getQualifier());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Hidden.attribute), attr.isHidden());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Indexable.attribute), attr.isPrimitive());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Required.attribute), attr.isRequired());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Readonly.attribute), attr.isReadonly());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Type.attribute), attr.type.toString());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Mode.attribute), attr.mode.toString());
			minfo.setAttrValue(meta.getAttr(AttrEnum.Entity.attribute), attr.getEntityName());
						
			return attraccessor.doPutEntry(minfo,false);
			
		} catch (AccessorException e) {
			
			throw new AccessorException("Error when put meta attr data.",e);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when create meta attr key.",e);
		}finally{
			
			AccessorUtils.closeAccessor(attraccessor);
		}

	}
	
	@Override
	public EntityMeta getEntityMeta(String entityName) throws AccessorException {

		MetaInfoEAccessor metaAccr = null;
		EntityMeta meta = null;
		try{
			metaAccr = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
		
			EntityEntry minfo = metaAccr.doGetEntry(entityName);
			meta = new EntityMeta(entityName);
			meta.setEntityClass(minfo.getAttrValue(MetaEnum.EntityClass.attribute,String.class));
			meta.setAccessorName(minfo.getAttrValue(MetaEnum.AccessorName.attribute,String.class));
			meta.setDescription(minfo.getAttrValue(MetaEnum.Description.attribute,String.class));
			meta.setEntityName(minfo.getAttrValue(MetaEnum.EntityName.attribute,String.class));
			meta.setSchema(minfo.getAttrValue(MetaEnum.Schema.attribute,String.class));	
			meta.setTraceable(minfo.getAttrValue(MetaEnum.Traceable.attribute,Boolean.class));
			meta.setCategory(minfo.getAttrValue(MetaEnum.Category.attribute,String.class));
			
		}catch (AccessorException ee){
			
			throw new AccessorException("Error when get meta info data.",ee);
		}finally{
			
			AccessorUtils.closeAccessor(metaAccr);
		}
		return meta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntityMeta> getEntityMetaList() throws AccessorException {
		MetaInfoEAccessor metaAccr = null;
		EntryCollection<EntityEntry> rlist = null;
		List<EntityMeta> rtv = null;
		try{
			
			metaAccr = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			rlist = metaAccr.doScanEntry(null);
			rtv = new ArrayList<EntityMeta>();
		
			for(EntityEntry ri:rlist){
				
				EntityMeta meta = new EntityMeta(metaAccr.getEntitySchema().getEntityName());
				meta.setEntryKey(ri.getEntryKey());
				meta.setEntityClass(ri.getAttrValue(MetaEnum.EntityClass.attribute,String.class));
				meta.setAccessorName(ri.getAttrValue(MetaEnum.AccessorName.attribute,String.class));
				meta.setEntityName(ri.getAttrValue(MetaEnum.EntityName.attribute,String.class));
				meta.setDescription(ri.getAttrValue(MetaEnum.Description.attribute,String.class));
				meta.setSchema(ri.getAttrValue(MetaEnum.Schema.attribute,String.class));	
				meta.setTraceable(ri.getAttrValue(MetaEnum.Traceable.attribute,Boolean.class));
				meta.setCategory(ri.getAttrValue(MetaEnum.Category.attribute,String.class));
				
				Map<String, String> attrMap =(ri.getAttrValue(MetaEnum.Attributes.attribute,Map.class));
				
				for(Map.Entry<String, String> et:attrMap.entrySet()){
					// value is key of attribute
					EntityAttr attr = getEntityAttr(et.getValue());
					meta.addAttr(attr);
				}
				rtv.add(meta);
			}
		}catch(BaseException be){
			
			throw new AccessorException("Error when get meta info data.",be);
			
		}finally{
			
			AccessorUtils.closeAccessor(metaAccr);
		}
		return rtv;
	}

	@Override
	public EntryKey putEntityMeta(EntityMeta meta) throws AccessorException {
		
		MetaInfoEAccessor metaAccr = null;
		try {
			metaAccr = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			EntryKey key = metaAccr.newKey();
			EntityEntry minfo = new EntityEntry(key);
			EntityMeta emeta = metaAccr.getEntitySchema().getEntityMeta();
			minfo.setAttrValue(emeta.getAttr(MetaEnum.EntityName.attribute), meta.getEntityName());
			minfo.setAttrValue(emeta.getAttr(MetaEnum.EntityClass.attribute), meta.getEntityClass());
			minfo.setAttrValue(emeta.getAttr(MetaEnum.AccessorName.attribute), meta.getAccessorName());
			minfo.setAttrValue(emeta.getAttr(MetaEnum.Description.attribute), meta.getDescription());
			minfo.setAttrValue(emeta.getAttr(MetaEnum.Traceable.attribute), meta.getTraceable());
			minfo.setAttrValue(emeta.getAttr(MetaEnum.Schema.attribute), meta.getSchema());
			minfo.setAttrValue(emeta.getAttr(MetaEnum.Category.attribute), meta.getCategory());
			
			EntryKey mkey = metaAccr.doPutEntry(minfo,false);
			
			Map<String,String> attrmap = new HashMap<String,String>();
			
			for(EntityAttr tattr:meta.getAllAttrs()){
				
				tattr.setEntityName(meta.getEntityName());
				
				EntryKey akey = putEntityAttr(tattr);
				attrmap.put(tattr.getAttrName(),akey.getKey());
			}
			
			if(!attrmap.isEmpty())
				metaAccr.doPutEntryAttr(mkey.getKey(), MetaEnum.Attributes.attribute, attrmap);

			return mkey;
			
		} catch (BaseException e) {
			
			throw new AccessorException("Error when put metadata.",e);
		}finally{
			
			AccessorUtils.closeAccessor(metaAccr);
		}

	}

}
