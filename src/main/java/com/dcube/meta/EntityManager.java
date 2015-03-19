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

import com.dcube.core.ITraceable;
import com.dcube.core.security.IAccessControl;
import com.dcube.exception.MetaException;
import com.dcube.meta.EntityConstants.AccessControlTraceInfo;
import com.dcube.meta.EntityConstants.AttrInfo;
import com.dcube.meta.EntityConstants.MetaInfo;

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
					entitymeta.getSchemaClass());
			if (BaseEntity.class.isAssignableFrom(t)) {

				schemaclz = (Class<BaseEntity>) t;
				Constructor<BaseEntity> constructor = (Constructor<BaseEntity>) schemaclz
						.getConstructor(EntityMeta.class);
				schemainstance = constructor.newInstance(entitymeta);
			}

		} catch (ClassNotFoundException e) {

			throw new MetaException("The schema class-{} is not found", e,
					entitymeta.getSchemaClass());
		} catch (InstantiationException e) {

			throw new MetaException("The class-{} fail Instantize", e,
					entitymeta.getSchemaClass());
		} catch (IllegalAccessException e) {

			throw new MetaException("The class-{} illegal access", e,
					entitymeta.getSchemaClass());
		} catch (SecurityException e) {

			throw new MetaException("The class-{} illegal access", e,
					entitymeta.getSchemaClass());
		} catch (NoSuchMethodException e) {

			throw new MetaException("The class-{} no constructor", e,
					entitymeta.getSchemaClass());
		} catch (IllegalArgumentException e) {

			throw new MetaException("The class-{} illegal parameter", e,
					entitymeta.getSchemaClass());
		} catch (InvocationTargetException e) {

			throw new MetaException("The class-{} fail invocation", e,
					entitymeta.getSchemaClass());
		}

		return schemainstance;
	}
	
	/** initial the meta schema */
	private void initialMetaSchema() {
		/** ---------- obal.meta.attr ------------- */
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_META_ATTR);
		meta.setSchemaClass(GenericEntity.class.getName());
		EntityAttr attr = new EntityAttr(AttrInfo.AttrName.attribute, 
				AttrInfo.AttrName.colfamily, 
				AttrInfo.AttrName.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Format.attribute, 
				AttrInfo.Format.colfamily, 
				AttrInfo.Format.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Column.attribute, 
				AttrInfo.Column.colfamily, 
				AttrInfo.Column.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Qualifier.attribute, 
				AttrInfo.Qualifier.colfamily, 
				AttrInfo.Qualifier.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Hidden.attribute, EntityAttr.AttrType.BOOL, 
				AttrInfo.Hidden.colfamily, 
				AttrInfo.Hidden.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Readonly.attribute, EntityAttr.AttrType.BOOL, 				
				AttrInfo.Readonly.colfamily, 
				AttrInfo.Readonly.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Required.attribute, EntityAttr.AttrType.BOOL, 
				AttrInfo.Required.colfamily, 
				AttrInfo.Required.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Primary.attribute, EntityAttr.AttrType.BOOL, 				
				AttrInfo.Primary.colfamily, 
				AttrInfo.Primary.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Entity.attribute, 				
				AttrInfo.Entity.colfamily, 
				AttrInfo.Entity.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Type.attribute, 				
				AttrInfo.Type.colfamily, 
				AttrInfo.Type.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Mode.attribute, 				
				AttrInfo.Mode.colfamily, 
				AttrInfo.Mode.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(AttrInfo.Description.attribute, 				
				AttrInfo.Description.colfamily, 
				AttrInfo.Description.qualifier);
		meta.addAttr(attr);
		
		GenericEntity ae = new GenericEntity(meta);
		metaMap.put(ae.getEntityName(), ae.getEntityMeta());
		
		/** ---------- obal.meta.info ------------- */
		meta = new EntityMeta(EntityConstants.ENTITY_META_INFO);
		meta.setSchemaClass(GenericEntity.class.getName());
		attr = new EntityAttr(MetaInfo.SchemaClass.attribute, 
							  MetaInfo.SchemaClass.colfamily, 
							  MetaInfo.SchemaClass.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaInfo.EntityName.attribute, 
				MetaInfo.EntityName.colfamily, 
				MetaInfo.EntityName.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaInfo.AccessorName.attribute, 
				MetaInfo.AccessorName.colfamily, 
				MetaInfo.AccessorName.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaInfo.Description.attribute, 
				MetaInfo.Description.colfamily, 
				MetaInfo.Description.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaInfo.Traceable.attribute, 
				EntityAttr.AttrType.BOOL, 
				MetaInfo.Traceable.colfamily, 
				MetaInfo.Traceable.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaInfo.Attributes.attribute, 
				EntityAttr.AttrMode.MAP, 
				EntityAttr.AttrType.STRING, 
				MetaInfo.Attributes.colfamily, 
				MetaInfo.Attributes.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(MetaInfo.Schema.attribute, 
				EntityAttr.AttrType.STRING, 				
				MetaInfo.Schema.colfamily, 
				MetaInfo.Schema.qualifier);		
		meta.addAttr(attr);
		
		GenericEntity me = new GenericEntity(meta);
		metaMap.put(me.getEntityName(), me.getEntityMeta());

	}

	/**
	 * Get the traceable attributes 
	 **/
	public List<EntityAttr> getTraceableAttributes(String entityname){
		
		List<EntityAttr> attrs = new ArrayList<EntityAttr>();
		EntityAttr attr = new EntityAttr(AccessControlTraceInfo.Creator.attribute, 
				AccessControlTraceInfo.Creator.colfamily, 
				AccessControlTraceInfo.Creator.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		attr = new EntityAttr(AccessControlTraceInfo.Modifier.attribute, 
				AccessControlTraceInfo.Modifier.colfamily, 
				AccessControlTraceInfo.Modifier.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		attr = new EntityAttr(AccessControlTraceInfo.NewCreate.attribute, EntityAttr.AttrType.DATE, 
				AccessControlTraceInfo.NewCreate.colfamily, 
				AccessControlTraceInfo.NewCreate.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		attr = new EntityAttr(AccessControlTraceInfo.LastModify.attribute, EntityAttr.AttrType.DATE, 
				AccessControlTraceInfo.LastModify.colfamily, 
				AccessControlTraceInfo.LastModify.qualifier);
		attr.setEntityName(entityname);
		attrs.add(attr);
		return attrs;
	}
	
	/**
	 * Get the access control attribute
	 * @param entityname the attribute name 
	 **/
	public EntityAttr getAccessControlAttribute(String entityname){
		EntityAttr attr = new EntityAttr(IAccessControl.ATTR_ACL, "c0", "acl");
		attr.setEntityName(entityname);
		return attr;
	}
}
