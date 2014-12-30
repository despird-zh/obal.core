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
package com.obal.meta.hbase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.AccessorFactory;
import com.obal.core.EntryFilter;
import com.obal.core.EntryKey;
import com.obal.core.accessor.RawEntry;
import com.obal.core.hbase.HGenericAccessor;
import com.obal.exception.AccessorException;
import com.obal.exception.BaseException;
import com.obal.exception.EntityException;
import com.obal.exception.MetaException;
import com.obal.meta.AttrMode;
import com.obal.meta.AttrType;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityConstants;
import com.obal.meta.EntityMeta;
import com.obal.meta.accessor.IMetaGenericAccessor;
import com.obal.util.AccessorUtils;

public class MetaGenericAccessor extends HGenericAccessor implements IMetaGenericAccessor{

	static Logger LOGGER = LoggerFactory.getLogger(MetaGenericAccessor.class);
	
	@Override
	public EntityAttr getEntityAttr(String attrKey) throws AccessorException {
		
		AttrInfoAccessor attraccessor = null;
		EntityAttr attr = null;
		try{
			attraccessor = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);

			RawEntry minfo = attraccessor.doGetEntry(attrKey);
			String attrName = (String)minfo.get("i_attr_name");
			String column = (String)minfo.get("i_column");
			String qualifier = (String)minfo.get("i_qualifier");
			LOGGER.debug("the qualifier:"+qualifier);
			AttrType type = AttrType.valueOf((String)minfo.get("i_type"));
			AttrMode mode = AttrMode.valueOf((String)minfo.get("i_mode"));
			
			attr = new EntityAttr(attrName,mode,type,column,qualifier);
			attr.setEntryKey(minfo.getEntryKey());
			attr.setEntityName((String)minfo.get("i_entity"));
			attr.setDescription((String)minfo.get("i_description"));
			attr.setFormat((String)minfo.get("i_format"));
			attr.setHidden((Boolean)minfo.get("i_hidden"));
			attr.setPrimary((Boolean)minfo.get("i_primary"));
			attr.setRequired((Boolean)minfo.get("i_required"));
			attr.setReadonly((Boolean)minfo.get("i_readonly"));
			attr.setCreator((String)minfo.get("i_creator"));
			attr.setNewCreate((Date)minfo.get("i_newcreate"));
			attr.setCreator((String)minfo.get("i_modifier"));
			attr.setLastModify((Date)minfo.get("i_lastmodify"));
		}catch(EntityException ee){
			
			throw new AccessorException("Error when build embed accessor:{}",ee,EntityConstants.ENTITY_META_ATTR);
		}finally{
			
			AccessorUtils.releaseAccessor(attraccessor);
		}
		return attr;

	}

	@Override
	public List<EntityAttr> getAttrList(String entityName) throws AccessorException {
		
		Filter filter1 = new RowFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(entityName.getBytes()));
		AttrInfoAccessor attraccessor = null;
		List<RawEntry> attrs = null;
		List<EntityAttr> rtv = null;
		try{
			attraccessor = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
		
			attrs = attraccessor.doScanEntry(new EntryFilter<Filter>(filter1));
			
			rtv = new ArrayList<EntityAttr>();
			for(RawEntry minfo:attrs){
	
				String attrName = (String)minfo.get("i_attr_name");
				String column = (String)minfo.get("i_column");
				String qualifier = (String)minfo.get("i_qualifier");
				
				AttrType type = AttrType.valueOf((String)minfo.get("i_type"));
				AttrMode mode = AttrMode.valueOf((String)minfo.get("i_mode"));
				
				EntityAttr attr = new EntityAttr(attrName,mode,type,column,qualifier);
				attr.setEntityName((String)minfo.get("i_entity"));
				attr.setDescription((String)minfo.get("i_description"));
				attr.setFormat((String)minfo.get("i_format"));
				attr.setHidden((Boolean)minfo.get("i_hidden"));
				attr.setPrimary((Boolean)minfo.get("i_primary"));
				attr.setRequired((Boolean)minfo.get("i_required"));
				attr.setReadonly((Boolean)minfo.get("i_readonly"));
				
				attr.setCreator((String)minfo.get("i_creator"));
				attr.setNewCreate((Date)minfo.get("i_newcreate"));
				attr.setCreator((String)minfo.get("i_modifier"));
				attr.setLastModify((Date)minfo.get("i_lastmodify"));
				rtv.add(attr);
			}
		}catch(EntityException ee){
			
		}finally{
			
			AccessorUtils.releaseAccessor(attraccessor);
		}
		return rtv;
	}

	@Override
	public EntryKey putEntityAttr(EntityAttr attr) throws AccessorException {
		AttrInfoAccessor attraccessor = null;
		
		try {
			attraccessor = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
			EntryKey key = attraccessor.getEntitySchema().newKey();
			RawEntry minfo = new RawEntry(key);

			minfo.put("i_attr_name", attr.getAttrName());
			minfo.put("i_description", attr.getDescription());
			minfo.put("i_format", attr.getFormat());
			minfo.put("i_column", attr.getColumn());
			minfo.put("i_qualifier", attr.getQualifier());
			minfo.put("i_hidden", attr.isHidden());
			minfo.put("i_primary", attr.isPrimary());
			minfo.put("i_required", attr.isRequired());
			minfo.put("i_readonly", attr.isReadonly());
			minfo.put("i_type", attr.type.toString());
			minfo.put("i_mode", attr.mode.toString());
			minfo.put("i_entity", attr.getEntityName());
			
			minfo.put("i_creator",attraccessor.getPrincipal().getName());
			minfo.put("i_modifier",attraccessor.getPrincipal().getName());
			minfo.put("i_newcreate", new Date());
			minfo.put("i_lastmodify", new Date());
						
			return attraccessor.doPutEntry(minfo);
			
		} catch (EntityException e) {
			
			throw new AccessorException("Error when put meta attr data.",e);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when create meta attr key.",e);
		}finally{
			
			AccessorUtils.releaseAccessor(attraccessor);
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EntityMeta getEntityMeta(String entityName) throws AccessorException {

		MetaInfoAccessor metaAccr = null;
		EntityMeta meta = null;
		try{
			metaAccr = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
		
			RawEntry minfo = metaAccr.doGetEntry(entityName);
			meta = new EntityMeta(entityName);
			meta.setSchemaClass((String)minfo.get("i_schema_class"));
			meta.setDescription((String)minfo.get("i_description"));
			meta.setEntityName((String)minfo.get("i_entity_name"));
			meta.setSchemas((List<String>)minfo.get("i_schemas"));	
			meta.setTraceable((Boolean) minfo.get("i_traceable"));
			meta.setCreator((String)minfo.get("i_creator"));
			meta.setNewCreate((Date)minfo.get("i_newcreate"));
			meta.setCreator((String)minfo.get("i_modifier"));
			meta.setLastModify((Date)minfo.get("i_lastmodify"));
			
		}catch (EntityException ee){
			
			throw new AccessorException("Error when get meta info data.",ee);
		}finally{
			
			AccessorUtils.releaseAccessor(metaAccr);
		}
		return meta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntityMeta> getEntityMetaList() throws AccessorException {
		MetaInfoAccessor metaAccr = null;
		List<RawEntry> rlist = null;
		List<EntityMeta> rtv = null;
		try{
			
			metaAccr = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			rlist = metaAccr.doScanEntry(null);
			rtv = new ArrayList<EntityMeta>();
		
			for(RawEntry ri:rlist){
				
				EntityMeta meta = new EntityMeta(metaAccr.getEntitySchema().getEntityName());
				meta.setEntryKey(ri.getEntryKey());
				meta.setSchemaClass((String)ri.get("i_schema_class"));
				meta.setEntityName((String)ri.get("i_entity_name"));
				meta.setDescription((String)ri.get("i_description"));
				meta.setSchemas((List<String>)ri.get("i_schemas"));	
				meta.setTraceable((Boolean) ri.get("i_traceable"));
				meta.setCreator((String)ri.get("i_creator"));
				meta.setNewCreate((Date)ri.get("i_newcreate"));
				meta.setCreator((String)ri.get("i_modifier"));
				meta.setLastModify((Date)ri.get("i_lastmodify"));
				Map<String, String> attrMap =(Map<String, String>) ri.get("i_attributes");
				
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
			
			AccessorUtils.releaseAccessor(metaAccr);
		}
		return rtv;
	}

	@Override
	public EntryKey putEntityMeta(EntityMeta meta) throws AccessorException {
		
		MetaInfoAccessor metaAccr = null;
		try {
			metaAccr = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			EntryKey key = metaAccr.getEntitySchema().newKey();
			RawEntry minfo = new RawEntry(key);

			minfo.put("i_entity_name", meta.getEntityName());
			minfo.put("i_schema_class", meta.getSchemaClass());
			minfo.put("i_description", meta.getDescription());
			minfo.put("i_traceable", meta.getTraceable());
			minfo.put("i_creator",metaAccr.getPrincipal().getName());
			minfo.put("i_modifier",metaAccr.getPrincipal().getName());
			minfo.put("i_newcreate", new Date());
			minfo.put("i_lastmodify", new Date());
			minfo.put("i_schemas", meta.getSchemas());
			
			EntryKey mkey = metaAccr.doPutEntry(minfo);
			
			Map<String,String> attrmap = new HashMap<String,String>();
			
			for(EntityAttr tattr:meta.getAllAttrs()){
				
				tattr.setEntityName(meta.getEntityName());
				
				EntryKey akey = putEntityAttr(tattr);
				attrmap.put(tattr.getAttrName(),akey.getKey());
			}
			
			if(!attrmap.isEmpty())
				metaAccr.doPutEntryAttr(mkey.getKey(), "i_attributes", attrmap);

			return mkey;
			
		} catch (BaseException e) {
			
			throw new AccessorException("Error when put metadata.",e);
		}finally{
			
			AccessorUtils.releaseAccessor(metaAccr);
		}

	}

}
