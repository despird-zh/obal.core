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

package com.obal.meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.obal.core.security.Principal;
import com.obal.core.security.PrincipalAware;
import com.obal.exception.MetaException;

/**
 * EntityManager manage all the Entity schema object.
 * <p>It implements sigleton pattern, and maintain two cache maps, one is for Entry Meta, 
 * other one for Entity Schema.</p>
 * <p>It provides method to retrieve the Entity Meta and Entity Schema.
 * All the Entity meta is added to Manager, when request for Entity Schema by entity name, 
 * firstly it check cache for existence. No Entity Schema instance then use the Entity Schema 
 * class provided by Entity Meta to create new instance, otherwise return existed one directly.
 * </p>
 * 
 * @since 0.1
 * @author G.Obal
 **/
public class EntityManager {
	
	private static EntityManager instance;
	
	/** cache map for EntryMeta */
	private Map<String, EntityMeta> metaMap = new HashMap<String, EntityMeta>();
	
	/** cache map for EntrySchema */
	private Map<String, BaseEntity> schemaMap = new HashMap<String, BaseEntity>();
	
	/** Hide default constructor */
	private EntityManager(){		
		initialMetaSchema();
	}
	
	/**
	 * Get single instance 
	 **/
	public static EntityManager getInstance(){
		
		if(null == instance)
			instance = new EntityManager();
		
		return instance;
	}
	
	/**
	 * Get EntityMeta object
	 * @param entityName the entity name
	 * @return EntryMeta
	 **/
	public EntityMeta getEntityMeta(String entityName){
		
		return metaMap.get(entityName);
	}
	
	/**
	 * Remove the entity meta information from manager.
	 * ps. remove from manager not means drop table in hbase.
	 * 
	 * @param entityName the entity name
	 * 
	 **/
	public void removeEntityMeta(String entityName){
		
		metaMap.remove(entityName);
	}
	
	/**
	 * Put EntityMeta object
	 * 
	 * @param entrymeta the EntityMeta object 
	 **/
	public void putEntityMeta(EntityMeta entitymeta){
		
		metaMap.put(entitymeta.getEntityName(), entitymeta);
	}
	
	/**
	 * Get the entry schema instance 
	 * 
	 * @param  entryName the entry name
	 * @param principal the principal information
	 * 
	 * @return EntrySchema the EntrySchema instance
	 * 
	 * @throws MetaException the exception when create new EntrySchema instance
	 **/
	@SuppressWarnings("unchecked")
	public BaseEntity getEntitySchema(String entityName, Principal principal) throws MetaException{
		
		BaseEntity schemainstance = schemaMap.get(entityName);
		EntityMeta entitymeta = metaMap.get(entityName);
		// entitymeta not exist then return null
		if(entitymeta == null) 
			throw new MetaException("The entity meta[{}] not exist.",entityName);
		
		Class<BaseEntity> schemaclz = null ;
		
		try {
			// schema not exists create new one
			if(schemainstance == null){
				Class<?> t = ClassLoader.getSystemClassLoader().loadClass(entitymeta.getSchemaClass());
				if(BaseEntity.class.isAssignableFrom(t)){
					
					schemaclz = (Class<BaseEntity>)t;	
					Constructor<BaseEntity> constructor = (Constructor<BaseEntity>)schemaclz.getConstructor(EntityMeta.class);
					schemainstance = constructor.newInstance(entitymeta);
				}
				// cache instance into map
				schemaMap.put(entityName, schemainstance);
			}
			
			if(schemainstance != null && schemainstance instanceof PrincipalAware){
				
				((PrincipalAware)schemainstance).setPrincipal(principal);
			}		
			
		} catch (ClassNotFoundException e) {
			
			throw new MetaException("The schema class-{} is not found", e ,entitymeta.getSchemaClass());
		} catch (InstantiationException e) {
			
			throw new MetaException("The class-{} fail Instantize", e ,entitymeta.getSchemaClass());
		} catch (IllegalAccessException e) {
			
			throw new MetaException("The class-{} illegal access", e ,entitymeta.getSchemaClass());
		} catch (SecurityException e) {

			throw new MetaException("The class-{} illegal access", e ,entitymeta.getSchemaClass());
		} catch (NoSuchMethodException e) {

			throw new MetaException("The class-{} no constructor", e ,entitymeta.getSchemaClass());
		} catch (IllegalArgumentException e) {

			throw new MetaException("The class-{} illegal parameter", e ,entitymeta.getSchemaClass());
		} catch (InvocationTargetException e) {

			throw new MetaException("The class-{} fail invocation", e ,entitymeta.getSchemaClass());
		}
		
		return schemainstance;
	}
	
	/** initial the meta schema*/
	private void initialMetaSchema(){
		/** ---------- obal.meta.attr ------------- */
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_META_ATTR);
		meta.setSchemaClass(GenericEntity.class.getName());
		EntityAttr attr = new EntityAttr("i_attr_name","c0","attr_name");
		meta.addAttr(attr);
		attr = new EntityAttr("i_format","c0","format");
		meta.addAttr(attr);
		attr = new EntityAttr("i_column","c0","column");
		meta.addAttr(attr);
		attr = new EntityAttr("i_qualifier","c0","qualifier");
		meta.addAttr(attr);
		attr = new EntityAttr("i_hidden",AttrType.BOOL,"c0","hidden");
		meta.addAttr(attr);
		attr = new EntityAttr("i_readonly",AttrType.BOOL,"c0","readonly");
		meta.addAttr(attr);
		attr = new EntityAttr("i_required",AttrType.BOOL,"c0","required");
		meta.addAttr(attr);
		attr = new EntityAttr("i_primary",AttrType.BOOL,"c0","primary");
		meta.addAttr(attr);
		attr = new EntityAttr("i_creator","c0","creator");
		meta.addAttr(attr);
		attr = new EntityAttr("i_modifier","c0","modifier");
		meta.addAttr(attr);
		attr = new EntityAttr("i_newcreate",AttrType.DATE,"c0","newcreate");
		meta.addAttr(attr);
		attr = new EntityAttr("i_lastmodify",AttrType.DATE,"c0","lastmodify");
		meta.addAttr(attr);
		attr = new EntityAttr("i_entity","c1","entity");
		meta.addAttr(attr);
		attr = new EntityAttr("i_type","c0","type");
		meta.addAttr(attr);
		attr = new EntityAttr("i_mode","c0","mode");
		meta.addAttr(attr);
		GenericEntity ae = new GenericEntity(meta);
		metaMap.put(ae.getEntityName(), ae.getEntityMeta());
		schemaMap.put(ae.getEntityName(), ae);
		/** ---------- obal.meta.info ------------- */
		meta = new EntityMeta(EntityConstants.ENTITY_META_INFO);
		meta.setSchemaClass(GenericEntity.class.getName());
		attr = new EntityAttr("i_schema_class","c0","schemaclass");
		meta.addAttr(attr);
		attr = new EntityAttr("i_entity_name","c0","entityname");
		meta.addAttr(attr);
		attr = new EntityAttr("i_description","c0","description");
		meta.addAttr(attr);
		attr = new EntityAttr("i_traceable",AttrType.BOOL,"c0","traceable");
		meta.addAttr(attr);
		attr = new EntityAttr("i_creator","c0","creator");
		meta.addAttr(attr);
		attr = new EntityAttr("i_modifier","c0","modifier");
		meta.addAttr(attr);
		attr = new EntityAttr("i_newcreate",AttrType.DATE,"c0","newcreate");
		meta.addAttr(attr);
		attr = new EntityAttr("i_lastmodify",AttrType.DATE,"c0","lastmodify");
		meta.addAttr(attr);
		attr = new EntityAttr("i_attributes",AttrMode.MAP,AttrType.STRING,"c1","a");
		meta.addAttr(attr);
		attr = new EntityAttr("i_schemas",AttrMode.LIST,AttrType.STRING,"c1","s");
		meta.addAttr(attr);
		GenericEntity me = new GenericEntity(meta);
		metaMap.put(me.getEntityName(), me.getEntityMeta());
		schemaMap.put(me.getEntityName(), me);
		/** ---------- obal.traceable ------------- */
		meta = new EntityMeta(EntityConstants.ENTITY_TRACEABLE);
		meta.setSchemaClass(GenericEntity.class.getName());
		attr = new EntityAttr("i_creator","c0","creator");
		meta.addAttr(attr);
		attr = new EntityAttr("i_modifier","c0","modifier");
		meta.addAttr(attr);
		attr = new EntityAttr("i_newcreate",AttrType.DATE,"c0","newcreate");
		meta.addAttr(attr);
		attr = new EntityAttr("i_lastmodify",AttrType.DATE,"c0","lastmodify");
		meta.addAttr(attr);
		GenericEntity te = new GenericEntity(meta);
		metaMap.put(te.getEntityName(), te.getEntityMeta());
		schemaMap.put(te.getEntityName(), te);
	}

}
