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

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.accessor.IMetaGAccessor;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.hbase.HGenericAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityMeta;
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
			IEntryConverter<EntityEntry,EntityAttr> converter = attraccessor.getEntryConverter(EntityAttr.class);
			EntityEntry minfo = attraccessor.doGetEntry(attrKey);
		
			attr = converter.toTarget(minfo);

		}catch(BaseException ee){
			
			throw new AccessorException("Error when build embed accessor:{}",ee,EntityConstants.ENTITY_META_ATTR);
		}finally{
			
			AccessorUtils.closeAccessor(attraccessor);
		}
		return attr;

	}

	@Override
	public List<EntityAttr> getAttrList(String entityName) throws AccessorException {
		
		Filter filter1 = new SingleColumnValueFilter(AttrEnum.Entity.colfamily.getBytes(), 
				AttrEnum.Entity.qualifier.getBytes(), 
				CompareFilter.CompareOp.EQUAL, entityName.getBytes());
		
		AttrInfoEAccessor attraccessor = null;
		EntryCollection<EntityEntry> attrs = null;
		List<EntityAttr> rtv = null;
		try{
			attraccessor = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
		
			attrs = attraccessor.doScanEntry(new EntryFilter<Filter>(filter1));
			// convert it into EntityAttr
			IEntryConverter<EntityEntry,EntityAttr> converter = attraccessor.getEntryConverter(EntityAttr.class);
			rtv = new ArrayList<EntityAttr>();
			for(EntityEntry minfo:attrs){
	
				EntityAttr attr = converter.toTarget(minfo);
				
				rtv.add(attr);
			}
		}catch(BaseException ee){
			
			throw new AccessorException("Error when read attributes of entity:{}",ee,entityName);
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
			EntryKey key = attr.getEntryKey();
			if(null == key){
				// key is null reset it
				key = attraccessor.getEntitySchema().newKey(getContext().getPrincipal());
				attr.setEntryKey(key);
			}
			// convert it into EntityEntry
			IEntryConverter<EntityEntry,EntityAttr> converter = attraccessor.getEntryConverter(EntityAttr.class);
			EntityEntry minfo = converter.toSource(attr);
			// save it
			return attraccessor.doPutEntry(minfo,false);
			
		} catch (BaseException e) {
			
			throw new AccessorException("Error when put meta attr data.",e);
		} finally{
			
			AccessorUtils.closeAccessor(attraccessor);
		}

	}
	
	@Override
	public EntityMeta getEntityMeta(String entityName) throws AccessorException {

		MetaInfoEAccessor metaAccr = null;
		EntityMeta meta = null;
		try{
			metaAccr = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			IEntryConverter<EntityEntry,EntityMeta> converter = metaAccr.getEntryConverter(EntityMeta.class);
			EntityEntry minfo = metaAccr.doGetEntry(entityName);
			meta = converter.toTarget(minfo);
			Map<String, String> attrMap =(minfo.getAttrValue(MetaEnum.Attributes.attribute,Map.class));
			
			for(Map.Entry<String, String> et:attrMap.entrySet()){
				// map.entry value is key of attribute
				EntityAttr attr = getEntityAttr(et.getValue());
				// push to meta
				meta.addAttr(attr);
			}
			
		}catch (BaseException ee){
			
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
			IEntryConverter<EntityEntry,EntityMeta> converter = metaAccr.getEntryConverter(EntityMeta.class);
			
			for(EntityEntry ri:rlist){
				
				EntityMeta meta = converter.toTarget(ri);
				
				Map<String, String> attrMap =(ri.getAttrValue(MetaEnum.Attributes.attribute,Map.class));
				
				for(Map.Entry<String, String> et:attrMap.entrySet()){
					// map.entry value is key of attribute
					EntityAttr attr = getEntityAttr(et.getValue());
					// push to meta
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
			
			EntryKey key = meta.getEntryKey();
			if(key == null){
				// key not set, generate one			
				key = metaAccr.newKey();
				meta.setEntryKey(key);// reset EntryKey
			}
			IEntryConverter<EntityEntry,EntityMeta> converter = metaAccr.getEntryConverter(EntityMeta.class);
			// convert to EntityEntry
			EntityEntry minfo = converter.toSource(meta);
			// save EntityEntry
			EntryKey mkey = metaAccr.doPutEntry(minfo,false);	
			// Initial attribute map
			Map<String,String> attrmap = new HashMap<String,String>();
			// Iterate the attribute and store it
			for(EntityAttr tattr:meta.getAllAttrs()){
				// set entity name
				tattr.setEntityName(meta.getEntityName());
				// save attribute
				EntryKey akey = putEntityAttr(tattr);
				// keep relation data to map
				attrmap.put(tattr.getAttrName(),akey.getKey());
			}
			// save relation data
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
