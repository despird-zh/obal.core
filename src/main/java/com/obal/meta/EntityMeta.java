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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.obal.core.EntryKey;
import com.obal.core.ITraceable;


/**
 * The meta information of Entry records.
 * 
 * <p>It holds the entry schema class and key class, both of them is used to build EntryAccessor</p>
 * <p>It holds the entry attributes, these attributes be reserved in a Map. it provide methods to 
 * get the <b>Required,Readonly,Visual</b> attributes
 * </p>
 * 
 * @since 0.1
 * @see com.obal.meta.BaseEntity
 * @see com.obal.core.EntryKey
 **/
public class EntityMeta implements ITraceable{

	private String schemaClazz = null;
	private Map<String, EntityAttr> attrMap = new HashMap<String, EntityAttr>();
	private String entityName;
	private String description;
	private List<String> schemas;
	private EntryKey entryKey = null;
	private Boolean traceable = false;
	/**
	 * Entry Meta constructor 
	 * 
	 * @param entryName The name of Entry
	 * @param schemaClazz the class name of EntrySchema
	 * @param keyclazz the class name of EntryKey
	 * 
	 * @see com.obal.meta.BaseEntity
	 * @see com.obal.core.EntryKey
	 * 
	 **/
	public EntityMeta(String entityName, String schemaClazz){
		
		this.entityName = entityName;
		this.schemaClazz = schemaClazz;
	}

	/**
	 * Entry Meta constructor 
	 * 
	 * @param entryName The name of Entry
	 * 
	 * 
	 **/
	public EntityMeta(String entityName){
		
		this.entityName = entityName;
	}
	
	/**
	 * Set the Entry schema class
	 * 
	 * @param  entrySchemaClazz the class name of entry schema
	 **/
	public void setSchemaClass(String schemaClazz){
		
		this.schemaClazz = schemaClazz;
		
	}
	
	/**
	 * Get the Entry schema class
	 * 
	 * @return String the class name of entry schema 
	 **/
	public String getSchemaClass(){
		
		return this.schemaClazz;
	}
		
	/**
	 * Get the entryName
	 * @return  String the name of entry 
	 **/
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Set the name of entry
	 * 
	 * @param entryName the name of entry
	 **/
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Add Entry Attribute
	 * 
	 * @param attr The Entry Attribute
	 *  
	 * @see com.obal.meta.EntityAttr
	 **/
	public void addAttr(EntityAttr attr){

		attrMap.put(attr.getAttrName(), attr);
	}
	
	public EntryKey getEntryKey(){
		
		return this.entryKey;
	}
	
	public void setEntryKey(EntryKey entryKey){
		
		this.entryKey = entryKey;		
	}
	
	/**
	 * Get the hidden Attributes
	 * 
	 * @return List<EntryAttr> the EntryAttribute List
	 * 
	 * @see com.obal.meta.EntityAttr
	 **/
	public List<EntityAttr> getHiddenAttrs(){
		
		ArrayList<EntityAttr> hiddens = new ArrayList<EntityAttr>();
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(attr.isHidden()){
				
				hiddens.add(attr);
			}
		}
		
		return hiddens;
	}
	
	/**
	 * Get the readonly Attributes
	 * 
	 * @return List<EntryAttr> the EntryAttribute List
	 * 
	 * @see com.obal.meta.EntityAttr
	 **/
	public List<EntityAttr> getReadonlyAttrs(){
		
		ArrayList<EntityAttr> readonlys = new ArrayList<EntityAttr>();
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(attr.isReadonly()){
				
				readonlys.add(attr);
			}
		}
		
		return readonlys;
	}

	/**
	 * Get the mandatory Attributes
	 * 
	 * @return List<EntryAttr> the EntryAttribute List
	 * 
	 * @see com.obal.meta.EntityAttr
	 **/
	public List<EntityAttr> getMandatoryAttrs(){
		
		ArrayList<EntityAttr> requireds = new ArrayList<EntityAttr>();
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(attr.isRequired()){
				
				requireds.add(attr);
			}
		}
		
		return requireds;
	}

	/**
	 * Get the visible Attributes
	 * 
	 * @return List<EntryAttr> the EntryAttribute List
	 * 
	 * @see com.obal.meta.EntityAttr
	 **/
	public List<EntityAttr> getVisibleAttrs(){
		
		ArrayList<EntityAttr> visibles = new ArrayList<EntityAttr>();
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(!attr.isHidden()){
				
				visibles.add(attr);
			}
		}
		
		return visibles;
	}
	
	/**
	 * Get the Attributes of column family
	 * 
	 * @param column the name of column family
	 * @return List<EntryAttr> the EntryAttribute List
	 * 
	 * @see com.obal.meta.EntityAttr
	 **/
	public List<EntityAttr> getAttrs(String column){
		
		if(StringUtils.isBlank(column))
			return getAllAttrs();
		
		ArrayList<EntityAttr> attrs = new ArrayList<EntityAttr>();
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(column.equals(attr.getColumn())){
				
				attrs.add(attr);
			}
		}
		
		return attrs;
	}
	
	/**
	 * Get all the attributes
	 * @return List<EntryAttr> the EntryAttribute List
	 */
	public List<EntityAttr> getAllAttrs(){
		
		Collection<EntityAttr> attrs = attrMap.values();
		return new ArrayList<EntityAttr>(attrs);
	}
	
	/**
	 * Get the attributes as per the primitive flag.
	 * 
	 * @param primitiveFlag true:return only primitive attrs; false:return collection attributes
	 * 
	 * @return List<EntryAttr> the EntryAttribute List
	 */
	public List<EntityAttr> getAttrs(boolean primitiveFlag){
	
		ArrayList<EntityAttr> attrs = new ArrayList<EntityAttr>();
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(primitiveFlag && attr.mode == AttrMode.PRIMITIVE){
				
				attrs.add(attr);
			}else if(!primitiveFlag && attr.mode != AttrMode.PRIMITIVE){
				
				attrs.add(attr);
			}
		}
		
		return attrs;
	}
	
	/**
	 * Get Attr info by attrname
	 * @param attrName the attribute name
	 * @return EntryAttr  
	 **/
	public EntityAttr getAttr(String attrName){
		
		EntityAttr rtv = null;
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(attr.getAttrName().equalsIgnoreCase(attrName)){
				
				rtv = attr;
			}
		}
		
		return rtv;
	}
	
	public List<String> getSchemas(){
		
		return schemas;
	}
	
	public void setSchemas(List<String> schemas){
		
		this.schemas = schemas;
	}
	
	public void setTraceable(Boolean traceable){
		
		this.traceable = traceable;
	}
	
	public Boolean getTraceable(){
		
		return this.traceable;
	}

	private String creator;
	private String modifier;
	private Date newCreate;
	private Date lastModify;
	@Override
	public String getCreator() {
		
		return this.creator;
	}

	@Override
	public void setCreator(String creator) {
		
		this.creator = creator;
		
	}

	@Override
	public String getModifier() {
		
		return this.modifier;
	}

	@Override
	public void setModifier(String modifier) {
		
		this.modifier = modifier;
	}

	@Override
	public Date getNewCreate() {
		
		return this.newCreate;
	}

	@Override
	public void setNewCreate(Date newCreate) {
		this.newCreate = newCreate;
	}

	@Override
	public Date getLastModify() {
		
		return this.lastModify;
	}

	@Override
	public void setLastModify(Date lastModify) {
		this.lastModify = lastModify;
	}
}
