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

package com.dcube.meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dcube.exception.MetaException;
import com.dcube.meta.EntityConstants.IndexableEnum;
import com.dcube.meta.EntityConstants.TraceableEnum;
import com.dcube.meta.EntityConstants.AttrEnum;
import com.dcube.meta.EntityConstants.MetaEnum;

/**
 * EntityManager manage all the Entity schema object.
 * <p>
 * It implements singleton pattern, and maintain two cache maps, one is for Entry
 * Meta, other one for Entity Schema.
 * </p>
 * <p>
 * It provides method to retrieve the Entity Meta and Entity Schema. All the
 * Entity meta is added to Manager, when request for Entity Schema by entity
 * name, firstly it check cache for existence. No Entity Schema instance then
 * use the Entity Schema class provided by Entity Meta to create new instance,
 * otherwise return existed one directly.
 * </p>
 * 
 * @since 0.1
 * @author G.Obal
 **/
public class EntityManager {

	private static EntityManager instance;

	/** cache map for EntryMeta */
	private Map<String, EntityMeta> metaMap = new HashMap<String, EntityMeta>();

	/** Hide default constructor */
	private EntityManager() {
		initialMetaSchema();
	}

	/**
	 * Get single instance
	 **/
	public static EntityManager getInstance() {

		if (null == instance)
			instance = new EntityManager();

		return instance;
	}

	/**
	 * Get EntityMeta object
	 * 
	 * @param entityName
	 *            the entity name
	 * @return EntryMeta
	 **/
	public EntityMeta getEntityMeta(String entityName) {

		return metaMap.get(entityName);
	}

	/**
	 * Remove the entity meta information from manager. ps. remove from manager
	 * not means drop table in hbase.
	 * 
	 * @param entityName
	 *            the entity name
	 * 
	 **/
	public void removeEntityMeta(String entityName) {

		metaMap.remove(entityName);
	}

	/**
	 * Put EntityMeta object
	 * 
	 * @param entrymeta
	 *            the EntityMeta object
	 **/
	public void putEntityMeta(EntityMeta entitymeta) {

		metaMap.put(entitymeta.getEntityName(), entitymeta);
	}

	/**
	 * Get the entity attribute info 
	 * 
	 * @param entityName the name of entity
	 * @param attrName the name of attribute.
	 **/
	public EntityAttr getEntityAttr(String entityName, String attrName)throws MetaException{
		
		EntityMeta emeta = metaMap.get(entityName);
		if(emeta == null)
			throw new MetaException("The meta[{}] info not exists.",entityName);
		
		EntityAttr eattr = emeta.getAttr(attrName);
		if(eattr == null)
			throw new MetaException("The attr[{}-{}] info not exists.",entityName,attrName);
		
		return eattr;
	}
	
	/**
	 * Get the entry schema instance
	 * 
	 * @param entryName  the entry name
	 * @return EntrySchema the EntrySchema instance
	 * 
	 * @throws MetaException
	 *             the exception when create new EntrySchema instance
	 **/
	@SuppressWarnings("unchecked")
	public BaseEntity getEntitySchema(String entityName) throws MetaException {

		BaseEntity schemainstance = null;
		EntityMeta entitymeta = metaMap.get(entityName);
		// entity meta not exist then return null
		if (entitymeta == null)
			throw new MetaException("The entity meta[{}] not exist.",
					entityName);

		Class<BaseEntity> schemaclz = null;

		try {
			// schema not exists create new one

			Class<?> t = ClassLoader.getSystemClassLoader().loadClass(
					entitymeta.getEntityClass());
			if (BaseEntity.class.isAssignableFrom(t)) {

				schemaclz = (Class<BaseEntity>) t;
				Constructor<BaseEntity> constructor = (Constructor<BaseEntity>) schemaclz
						.getConstructor(EntityMeta.class);
				schemainstance = constructor.newInstance(entitymeta);
			}

		} catch (ClassNotFoundException e) {

			throw new MetaException("The entity class-{} is not found", e,
					entitymeta.getEntityClass());
		} catch (InstantiationException e) {

			throw new MetaException("The class-{} fail Instantize", e,
					entitymeta.getEntityClass());
		} catch (IllegalAccessException e) {

			throw new MetaException("The class-{} illegal access", e,
					entitymeta.getEntityClass());
		} catch (SecurityException e) {

			throw new MetaException("The class-{} illegal access", e,
					entitymeta.getEntityClass());
		} catch (NoSuchMethodException e) {

			throw new MetaException("The class-{} no constructor", e,
					entitymeta.getEntityClass());
		} catch (IllegalArgumentException e) {

			throw new MetaException("The class-{} illegal parameter", e,
					entitymeta.getEntityClass());
		} catch (InvocationTargetException e) {

			throw new MetaException("The class-{} fail invocation", e,
					entitymeta.getEntityClass());
		}

		return schemainstance;
	}
	
	/** initial the meta schema */
	private void initialMetaSchema() {
		/** ---------- obal.meta.attr ------------- */
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_META_ATTR);
		meta.setEntityClass(GenericEntity.class.getName());
		meta.setAccessorName(EntityConstants.ACCESSOR_ENTITY_ATTR);
		EntityAttr attr = new EntityAttr(AttrEnum.AttrName.attribute, 
				AttrEnum.AttrName.colfamily, 
				AttrEnum.AttrName.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Format.attribute, 
				AttrEnum.Format.colfamily, 
				AttrEnum.Format.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Column.attribute, 
				AttrEnum.Column.colfamily, 
				AttrEnum.Column.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Qualifier.attribute, 
				AttrEnum.Qualifier.colfamily, 
				AttrEnum.Qualifier.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Hidden.attribute, EntityAttr.AttrType.BOOL, 
				AttrEnum.Hidden.colfamily, 
				AttrEnum.Hidden.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Readonly.attribute, EntityAttr.AttrType.BOOL, 				
				AttrEnum.Readonly.colfamily, 
				AttrEnum.Readonly.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Required.attribute, EntityAttr.AttrType.BOOL, 
				AttrEnum.Required.colfamily, 
				AttrEnum.Required.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Indexable.attribute, EntityAttr.AttrType.BOOL, 				
				AttrEnum.Indexable.colfamily, 
				AttrEnum.Indexable.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Entity.attribute, 				
				AttrEnum.Entity.colfamily, 
				AttrEnum.Entity.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Type.attribute, 				
				AttrEnum.Type.colfamily, 
				AttrEnum.Type.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Mode.attribute, 				
				AttrEnum.Mode.colfamily, 
				AttrEnum.Mode.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrEnum.Description.attribute, 				
				AttrEnum.Description.colfamily, 
				AttrEnum.Description.qualifier);
		meta.addAttr(attr);
		
		GenericEntity ae = new GenericEntity(meta);
		metaMap.put(ae.getEntityName(), ae.getEntityMeta());
		
		/** ---------- obal.meta.info ------------- */
		meta = new EntityMeta(EntityConstants.ENTITY_META_INFO);
		meta.setEntityClass(GenericEntity.class.getName());
		meta.setAccessorName(EntityConstants.ACCESSOR_ENTITY_META);
		
		attr = new EntityAttr(MetaEnum.EntityClass.attribute, 
							  MetaEnum.EntityClass.colfamily, 
							  MetaEnum.EntityClass.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaEnum.EntityName.attribute, 
				MetaEnum.EntityName.colfamily, 
				MetaEnum.EntityName.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaEnum.AccessorName.attribute, 
				MetaEnum.AccessorName.colfamily, 
				MetaEnum.AccessorName.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaEnum.Description.attribute, 
				MetaEnum.Description.colfamily, 
				MetaEnum.Description.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaEnum.Traceable.attribute, 
				EntityAttr.AttrType.BOOL, 
				MetaEnum.Traceable.colfamily, 
				MetaEnum.Traceable.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaEnum.Attributes.attribute, 
				EntityAttr.AttrMode.MAP, 
				EntityAttr.AttrType.STRING, 
				MetaEnum.Attributes.colfamily, 
				MetaEnum.Attributes.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaEnum.Schema.attribute, 
				EntityAttr.AttrType.STRING, 				
				MetaEnum.Schema.colfamily, 
				MetaEnum.Schema.qualifier);		
		meta.addAttr(attr);
		/**
		 * 2014-12-1 Add category attribute.
		 **/ 
		attr = new EntityAttr(MetaEnum.Category.attribute, 
				EntityAttr.AttrType.STRING, 				
				MetaEnum.Category.colfamily, 
				MetaEnum.Category.qualifier);		
		meta.addAttr(attr);
		
		GenericEntity me = new GenericEntity(meta);
		metaMap.put(me.getEntityName(), me.getEntityMeta());

	}

	/**
	 * Get the traceable attributes 
	 **/
	public List<EntityAttr> getTraceableAttributes(String entityname){
		
		List<EntityAttr> attrs = new ArrayList<EntityAttr>();
		EntityAttr attr = new EntityAttr(TraceableEnum.Creator.attribute, 
				TraceableEnum.Creator.colfamily, 
				TraceableEnum.Creator.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		attr = new EntityAttr(TraceableEnum.Modifier.attribute, 
				TraceableEnum.Modifier.colfamily, 
				TraceableEnum.Modifier.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		attr = new EntityAttr(TraceableEnum.NewCreate.attribute, EntityAttr.AttrType.DATE, 
				TraceableEnum.NewCreate.colfamily, 
				TraceableEnum.NewCreate.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		attr = new EntityAttr(TraceableEnum.LastModify.attribute, EntityAttr.AttrType.DATE, 
				TraceableEnum.LastModify.colfamily, 
				TraceableEnum.LastModify.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		return attrs;
	}
	
	/**
	 * Get the Index table attributes, this is only used for create index table
	 * 
	 **/
	public List<EntityAttr> getIndexAttributes(String entityname){
		
		List<EntityAttr> attrs = new ArrayList<EntityAttr>();
		EntityAttr attr = new EntityAttr(IndexableEnum.Default.attribute, 
				IndexableEnum.Default.colfamily, 
				IndexableEnum.Default.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		return attrs;
	}
}
