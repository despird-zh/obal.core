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
package com.doccube.meta.hbase;

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

import com.doccube.core.AccessorFactory;
import com.doccube.core.EntryFilter;
import com.doccube.core.EntryKey;
import com.doccube.core.accessor.EntryCollection;
import com.doccube.core.accessor.EntryInfo;
import com.doccube.core.accessor.GenericContext;
import com.doccube.core.hbase.HGenericAccessor;
import com.doccube.exception.AccessorException;
import com.doccube.exception.BaseException;
import com.doccube.exception.EntityException;
import com.doccube.exception.MetaException;
import com.doccube.meta.EntityAttr;
import com.doccube.meta.EntityConstants;
import com.doccube.meta.EntityMeta;
import com.doccube.meta.EntityAttr.AttrMode;
import com.doccube.meta.EntityAttr.AttrType;
import com.doccube.meta.accessor.IMetaGenericAccessor;
import com.doccube.util.AccessorUtils;

public class MetaGenericAccessor extends HGenericAccessor implements IMetaGenericAccessor{

	public MetaGenericAccessor(GenericContext context) {
		super(context);
	}

	static Logger LOGGER = LoggerFactory.getLogger(MetaGenericAccessor.class);
	
	@Override
	public EntityAttr getEntityAttr(String attrKey) throws AccessorException {
		
		AttrInfoAccessor attraccessor = null;
		EntityAttr attr = null;
		try{
			attraccessor = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);

			EntryInfo minfo = attraccessor.doGetEntry(attrKey);
		
			String attrName = minfo.getAttrValue("i_attr_name",String.class);
			String column = minfo.getAttrValue("i_column",String.class);
			String qualifier = minfo.getAttrValue("i_qualifier",String.class);
			LOGGER.debug("the qualifier:"+qualifier);
			AttrType type = AttrType.valueOf(minfo.getAttrValue("i_type",String.class));
			AttrMode mode = AttrMode.valueOf(minfo.getAttrValue("i_mode",String.class));
			
			attr = new EntityAttr(attrName,mode,type,column,qualifier);
			attr.setEntryKey(minfo.getEntryKey());
			attr.setEntityName(minfo.getAttrValue("i_entity",String.class));
			//attr.setDescription(minfo.getAttrValue("i_description",String.class));
			attr.setFormat(minfo.getAttrValue("i_format",String.class));
			attr.setHidden(minfo.getAttrValue("i_hidden",Boolean.class));
			attr.setPrimary(minfo.getAttrValue("i_primary",Boolean.class));
			attr.setRequired(minfo.getAttrValue("i_required",Boolean.class));
			attr.setReadonly(minfo.getAttrValue("i_readonly",Boolean.class));

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
		EntryCollection<EntryInfo> attrs = null;
		List<EntityAttr> rtv = null;
		try{
			attraccessor = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
		
			attrs = attraccessor.doScanEntry(new EntryFilter<Filter>(filter1));
			
			rtv = new ArrayList<EntityAttr>();
			for(EntryInfo minfo:attrs){
	
				String attrName = minfo.getAttrValue("i_attr_name",String.class);
				String column = minfo.getAttrValue("i_column",String.class);
				String qualifier = minfo.getAttrValue("i_qualifier",String.class);
				
				AttrType type = AttrType.valueOf(minfo.getAttrValue("i_type",String.class));
				AttrMode mode = AttrMode.valueOf(minfo.getAttrValue("i_mode",String.class));
				
				EntityAttr attr = new EntityAttr(attrName,mode,type,column,qualifier);
				attr.setEntityName(minfo.getAttrValue("i_entity",String.class));
				attr.setDescription(minfo.getAttrValue("i_description",String.class));
				attr.setFormat(minfo.getAttrValue("i_format",String.class));
				attr.setHidden(minfo.getAttrValue("i_hidden",Boolean.class));
				attr.setPrimary(minfo.getAttrValue("i_primary",Boolean.class));
				attr.setRequired(minfo.getAttrValue("i_required",Boolean.class));
				attr.setReadonly(minfo.getAttrValue("i_readonly",Boolean.class));
				
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
			EntryKey key = attraccessor.getEntitySchema().newKey(getContext().getPrincipal());
			EntryInfo minfo = new EntryInfo(key);
			EntityMeta meta = attraccessor.getEntitySchema().getEntityMeta();
			minfo.setAttrValue(meta.getAttr("i_attr_name"), attr.getAttrName());
			minfo.setAttrValue(meta.getAttr("i_description"), attr.getDescription());
			minfo.setAttrValue(meta.getAttr("i_format"), attr.getFormat());
			minfo.setAttrValue(meta.getAttr("i_column"), attr.getColumn());
			minfo.setAttrValue(meta.getAttr("i_qualifier"), attr.getQualifier());
			minfo.setAttrValue(meta.getAttr("i_hidden"), attr.isHidden());
			minfo.setAttrValue(meta.getAttr("i_primary"), attr.isPrimary());
			minfo.setAttrValue(meta.getAttr("i_required"), attr.isRequired());
			minfo.setAttrValue(meta.getAttr("i_readonly"), attr.isReadonly());
			minfo.setAttrValue(meta.getAttr("i_type"), attr.type.toString());
			minfo.setAttrValue(meta.getAttr("i_mode"), attr.mode.toString());
			minfo.setAttrValue(meta.getAttr("i_entity"), attr.getEntityName());
			
			minfo.setAttrValue(meta.getAttr("i_creator"),attraccessor.getPrincipal().getName());
			minfo.setAttrValue(meta.getAttr("i_modifier"),attraccessor.getPrincipal().getName());
			minfo.setAttrValue(meta.getAttr("i_newcreate"), new Date());
			minfo.setAttrValue(meta.getAttr("i_lastmodify"), new Date());
						
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
		
			EntryInfo minfo = metaAccr.doGetEntry(entityName);
			meta = new EntityMeta(entityName);
			meta.setSchemaClass(minfo.getAttrValue("i_schema_class",String.class));
			meta.setDescription(minfo.getAttrValue("i_description",String.class));
			meta.setEntityName(minfo.getAttrValue("i_entity_name",String.class));
			meta.setSchemas(minfo.getAttrValue("i_schemas",List.class));	
			meta.setTraceable(minfo.getAttrValue("i_traceable",Boolean.class));
			
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
		EntryCollection<EntryInfo> rlist = null;
		List<EntityMeta> rtv = null;
		try{
			
			metaAccr = AccessorFactory.getInstance().buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			rlist = metaAccr.doScanEntry(null);
			rtv = new ArrayList<EntityMeta>();
		
			for(EntryInfo ri:rlist){
				
				EntityMeta meta = new EntityMeta(metaAccr.getEntitySchema().getEntityName());
				meta.setEntryKey(ri.getEntryKey());
				meta.setSchemaClass(ri.getAttrValue("i_schema_class",String.class));
				meta.setEntityName(ri.getAttrValue("i_entity_name",String.class));
				meta.setDescription(ri.getAttrValue("i_description",String.class));
				meta.setSchemas(ri.getAttrValue("i_schemas",List.class));	
				meta.setTraceable(ri.getAttrValue("i_traceable",Boolean.class));

				Map<String, String> attrMap =(ri.getAttrValue("i_attributes",Map.class));
				
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
			EntryKey key = metaAccr.newKey();
			EntryInfo minfo = new EntryInfo(key);
			EntityMeta emeta = metaAccr.getEntitySchema().getEntityMeta();
			minfo.setAttrValue(emeta.getAttr("i_entity_name"), meta.getEntityName());
			minfo.setAttrValue(emeta.getAttr("i_schema_class"), meta.getSchemaClass());
			minfo.setAttrValue(emeta.getAttr("i_description"), meta.getDescription());
			minfo.setAttrValue(emeta.getAttr("i_traceable"), meta.getTraceable());
			minfo.setAttrValue(emeta.getAttr("i_creator"),metaAccr.getPrincipal().getName());
			minfo.setAttrValue(emeta.getAttr("i_modifier"),metaAccr.getPrincipal().getName());
			minfo.setAttrValue(emeta.getAttr("i_newcreate"), new Date());
			minfo.setAttrValue(emeta.getAttr("i_lastmodify"), new Date());
			minfo.setAttrValue(emeta.getAttr("i_schemas"), meta.getSchemas());
			
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
