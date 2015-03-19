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

import com.dcube.accessor.IMetaGenericAccessor;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.EntryInfo;
import com.dcube.core.accessor.GenericContext;
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
import com.dcube.meta.EntityConstants.AttrInfo;
import com.dcube.meta.EntityConstants.MetaInfo;
import com.dcube.util.Accessors;

public class MetaGenericAccessor extends HGenericAccessor implements IMetaGenericAccessor{

	public MetaGenericAccessor() {
		
		super(EntityConstants.ACCESSOR_GENERIC_META);
	}
	
	public MetaGenericAccessor(GenericContext context) {
		
		super(EntityConstants.ACCESSOR_GENERIC_META,context);
	}

	static Logger LOGGER = LoggerFactory.getLogger(MetaGenericAccessor.class);
	
	@Override
	public EntityAttr getEntityAttr(String attrKey) throws AccessorException {
		
		AttrInfoAccessor attraccessor = null;
		EntityAttr attr = null;
		try{
			attraccessor = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);

			EntryInfo minfo = attraccessor.doGetEntry(attrKey);
		
			String attrName = minfo.getAttrValue(AttrInfo.AttrName.attribute,String.class);
			String column = minfo.getAttrValue(AttrInfo.Column.attribute,String.class);
			String qualifier = minfo.getAttrValue(AttrInfo.Qualifier.attribute,String.class);
			LOGGER.debug("the qualifier:"+qualifier);
			AttrType type = AttrType.valueOf(minfo.getAttrValue(AttrInfo.Type.attribute,String.class));
			AttrMode mode = AttrMode.valueOf(minfo.getAttrValue(AttrInfo.Mode.attribute,String.class));
			
			attr = new EntityAttr(attrName,mode,type,column,qualifier);
			attr.setEntryKey(minfo.getEntryKey());
			attr.setEntityName(minfo.getAttrValue(AttrInfo.Entity.attribute,String.class));
			attr.setDescription(minfo.getAttrValue(AttrInfo.Description.attribute,String.class));
			attr.setFormat(minfo.getAttrValue(AttrInfo.Format.attribute,String.class));
			attr.setHidden(minfo.getAttrValue(AttrInfo.Hidden.attribute,Boolean.class));
			attr.setPrimary(minfo.getAttrValue(AttrInfo.Primary.attribute,Boolean.class));
			attr.setRequired(minfo.getAttrValue(AttrInfo.Required.attribute,Boolean.class));
			attr.setReadonly(minfo.getAttrValue(AttrInfo.Readonly.attribute,Boolean.class));

		}catch(EntityException ee){
			
			throw new AccessorException("Error when build embed accessor:{}",ee,EntityConstants.ENTITY_META_ATTR);
		}finally{
			
			Accessors.closeAccessor(attraccessor);
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
			attraccessor = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
		
			attrs = attraccessor.doScanEntry(new EntryFilter<Filter>(filter1));
			
			rtv = new ArrayList<EntityAttr>();
			for(EntryInfo minfo:attrs){
	
				String attrName = minfo.getAttrValue(AttrInfo.AttrName.attribute,String.class);
				String column = minfo.getAttrValue(AttrInfo.Column.attribute,String.class);
				String qualifier = minfo.getAttrValue(AttrInfo.Qualifier.attribute,String.class);
				
				AttrType type = AttrType.valueOf(minfo.getAttrValue(AttrInfo.Type.attribute,String.class));
				AttrMode mode = AttrMode.valueOf(minfo.getAttrValue(AttrInfo.Mode.attribute,String.class));
				
				EntityAttr attr = new EntityAttr(attrName,mode,type,column,qualifier);
				attr.setEntityName(minfo.getAttrValue(AttrInfo.Entity.attribute,String.class));
				attr.setDescription(minfo.getAttrValue(AttrInfo.Description.attribute,String.class));
				attr.setFormat(minfo.getAttrValue(AttrInfo.Format.attribute,String.class));
				attr.setHidden(minfo.getAttrValue(AttrInfo.Hidden.attribute,Boolean.class));
				attr.setPrimary(minfo.getAttrValue(AttrInfo.Primary.attribute,Boolean.class));
				attr.setRequired(minfo.getAttrValue(AttrInfo.Required.attribute,Boolean.class));
				attr.setReadonly(minfo.getAttrValue(AttrInfo.Readonly.attribute,Boolean.class));
				
				rtv.add(attr);
			}
		}catch(EntityException ee){
			
		}finally{
			
			Accessors.closeAccessor(attraccessor);
		}
		return rtv;
	}

	@Override
	public EntryKey putEntityAttr(EntityAttr attr) throws AccessorException {
		AttrInfoAccessor attraccessor = null;
		
		try {
			attraccessor = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_ATTR);
			EntryKey key = attraccessor.getEntitySchema().newKey(getContext().getPrincipal());
			EntryInfo minfo = new EntryInfo(key);
			EntityMeta meta = attraccessor.getEntitySchema().getEntityMeta();
			minfo.setAttrValue(meta.getAttr(AttrInfo.AttrName.attribute), attr.getAttrName());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Description.attribute), attr.getDescription());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Format.attribute), attr.getFormat());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Column.attribute), attr.getColumn());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Qualifier.attribute), attr.getQualifier());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Hidden.attribute), attr.isHidden());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Primary.attribute), attr.isPrimary());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Required.attribute), attr.isRequired());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Readonly.attribute), attr.isReadonly());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Type.attribute), attr.type.toString());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Mode.attribute), attr.mode.toString());
			minfo.setAttrValue(meta.getAttr(AttrInfo.Entity.attribute), attr.getEntityName());
						
			return attraccessor.doPutEntry(minfo);
			
		} catch (EntityException e) {
			
			throw new AccessorException("Error when put meta attr data.",e);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when create meta attr key.",e);
		}finally{
			
			Accessors.closeAccessor(attraccessor);
		}

	}
	
	@Override
	public EntityMeta getEntityMeta(String entityName) throws AccessorException {

		MetaInfoAccessor metaAccr = null;
		EntityMeta meta = null;
		try{
			metaAccr = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
		
			EntryInfo minfo = metaAccr.doGetEntry(entityName);
			meta = new EntityMeta(entityName);
			meta.setSchemaClass(minfo.getAttrValue(MetaInfo.SchemaClass.attribute,String.class));
			meta.setAccessorName(minfo.getAttrValue(MetaInfo.AccessorName.attribute,String.class));
			meta.setDescription(minfo.getAttrValue(MetaInfo.Description.attribute,String.class));
			meta.setEntityName(minfo.getAttrValue(MetaInfo.EntityName.attribute,String.class));
			meta.setSchema(minfo.getAttrValue(MetaInfo.Schema.attribute,String.class));	
			meta.setTraceable(minfo.getAttrValue(MetaInfo.Traceable.attribute,Boolean.class));
			
		}catch (EntityException ee){
			
			throw new AccessorException("Error when get meta info data.",ee);
		}finally{
			
			Accessors.closeAccessor(metaAccr);
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
			
			metaAccr = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			rlist = metaAccr.doScanEntry(null);
			rtv = new ArrayList<EntityMeta>();
		
			for(EntryInfo ri:rlist){
				
				EntityMeta meta = new EntityMeta(metaAccr.getEntitySchema().getEntityName());
				meta.setEntryKey(ri.getEntryKey());
				meta.setSchemaClass(ri.getAttrValue(MetaInfo.SchemaClass.attribute,String.class));
				meta.setAccessorName(ri.getAttrValue(MetaInfo.AccessorName.attribute,String.class));
				meta.setEntityName(ri.getAttrValue(MetaInfo.EntityName.attribute,String.class));
				meta.setDescription(ri.getAttrValue(MetaInfo.Description.attribute,String.class));
				meta.setSchema(ri.getAttrValue(MetaInfo.Schema.attribute,String.class));	
				meta.setTraceable(ri.getAttrValue(MetaInfo.Traceable.attribute,Boolean.class));

				Map<String, String> attrMap =(ri.getAttrValue(MetaInfo.Attributes.attribute,Map.class));
				
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
			
			Accessors.closeAccessor(metaAccr);
		}
		return rtv;
	}

	@Override
	public EntryKey putEntityMeta(EntityMeta meta) throws AccessorException {
		
		MetaInfoAccessor metaAccr = null;
		try {
			metaAccr = AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_META_INFO);
			EntryKey key = metaAccr.newKey();
			EntryInfo minfo = new EntryInfo(key);
			EntityMeta emeta = metaAccr.getEntitySchema().getEntityMeta();
			minfo.setAttrValue(emeta.getAttr(MetaInfo.EntityName.attribute), meta.getEntityName());
			minfo.setAttrValue(emeta.getAttr(MetaInfo.SchemaClass.attribute), meta.getSchemaClass());
			minfo.setAttrValue(emeta.getAttr(MetaInfo.AccessorName.attribute), meta.getAccessorName());
			minfo.setAttrValue(emeta.getAttr(MetaInfo.Description.attribute), meta.getDescription());
			minfo.setAttrValue(emeta.getAttr(MetaInfo.Traceable.attribute), meta.getTraceable());
			minfo.setAttrValue(emeta.getAttr(MetaInfo.Schema.attribute), meta.getSchema());
			
			EntryKey mkey = metaAccr.doPutEntry(minfo);
			
			Map<String,String> attrmap = new HashMap<String,String>();
			
			for(EntityAttr tattr:meta.getAllAttrs()){
				
				tattr.setEntityName(meta.getEntityName());
				
				EntryKey akey = putEntityAttr(tattr);
				attrmap.put(tattr.getAttrName(),akey.getKey());
			}
			
			if(!attrmap.isEmpty())
				metaAccr.doPutEntryAttr(mkey.getKey(), MetaInfo.Attributes.attribute, attrmap);

			return mkey;
			
		} catch (BaseException e) {
			
			throw new AccessorException("Error when put metadata.",e);
		}finally{
			
			Accessors.closeAccessor(metaAccr);
		}

	}

}
