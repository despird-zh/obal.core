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
package com.obal.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.security.Principal;
import com.obal.exception.MetaException;
import com.obal.meta.BaseEntity;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityManager;
import com.obal.meta.EntityMeta;

/**
 * Utility tool to Access meta information
 * 
 * @author despird 
 * @version 0.1 2014-3-4
 *  
 **/
public class EntityUtils {

	static Logger LOGGER = LoggerFactory.getLogger(EntityUtils.class);
	
	/**
	 * Get entity meta
	 * 
	 * @param entityName the entity name
	 * @return EntityMeta object, not existed entity, return null.
	 **/
	public static EntityMeta getEntityMeta(String entityName){
		
		return EntityManager.getInstance().getEntityMeta(entityName);
	}
	
	/**
	 * Get entity schema instance
	 * 
	 * @param entityName the entity name
	 * @param principal the principal object 
	 * 
	 * @return BaseEntity the entity object
	 **/
	public static BaseEntity getEntitySchema(String entityName, Principal principal){
		
		BaseEntity schema = null;
		try {
			schema = (BaseEntity) EntityManager.getInstance().getEntitySchema(entityName, principal);
		} catch (MetaException e) {
			
			LOGGER.error("Error when get schema instance from manager.",e);
		}
		
		return schema;
	}
	
	/**
	 * Get the entity attribute 
	 * 
	 *  @param entityName the entity name
	 *  @param attrname the entity attribute name
	 *  
	 *  @return EntityAttr the entity attribute object
	 *  
	 **/
	public static EntityAttr getEntityAttr(String entityName, String attrName){
		
		EntityMeta meta =  EntityManager.getInstance().getEntityMeta(entityName);
		
		if(meta == null)
			return null;
		else
			return meta.getAttr(attrName);
	}
	
	/**
	 * Get the entity attribute list 
	 * 
	 *  @param entityName the entity name
	 *  @param attrname the entity attribute name
	 *  
	 *  @return List<EntityAttr> the entity attribute list object
	 *  
	 **/
	public static List<EntityAttr> getEntityAttrs(String entityName){
		
		EntityMeta meta =  EntityManager.getInstance().getEntityMeta(entityName);
		if(meta == null)
			return null;
		
		return meta.getAllAttrs();
	}
	
	/**
	 * Get hidden entity attribute list 
	 * 
	 *  @param entityName the entity name
	 *  @param attrname the entity attribute name
	 *  
	 *  @return List<EntityAttr> the entity attribute list object
	 *  
	 **/
	public static List<EntityAttr> getHiddenAttrs(String entityName){
		
		EntityMeta meta =  EntityManager.getInstance().getEntityMeta(entityName);
		if(meta == null)
			return null;
		
		return meta.getHiddenAttrs();
	}
	
	/**
	 * Get read-only entity attribute list 
	 * 
	 *  @param entityName the entity name
	 *  @param attrname the entity attribute name
	 *  
	 *  @return List<EntityAttr> the entity attribute list object
	 *  
	 **/
	public static List<EntityAttr> getReadonlyAttrs(String entityName){
		
		EntityMeta meta =  EntityManager.getInstance().getEntityMeta(entityName);
		if(meta == null)
			return null;
		
		return meta.getReadonlyAttrs();
	}
	
	/**
	 * Get mandatory entity attribute list 
	 * 
	 *  @param entityName the entity name
	 *  @param attrname the entity attribute name
	 *  
	 *  @return List<EntityAttr> the entity attribute list object
	 *  
	 **/
	public static List<EntityAttr> getMandatoryAttrs(String entityName){
		
		EntityMeta meta =  EntityManager.getInstance().getEntityMeta(entityName);
		if(meta == null)
			return null;
		
		return meta.getMandatoryAttrs();
	}
}
