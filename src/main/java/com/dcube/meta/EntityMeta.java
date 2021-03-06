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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dcube.core.EntryKey;


/**
 * The meta information of Entry records.
 * 
 * <p>It holds the entry schema class and key class, both of them is used to build EntryAccessor</p>
 * <p>It holds the entry attributes, these attributes be reserved in a Map. it provide methods to 
 * get the <b>Required,Readonly,Visual,Indexable</b> attributes
 * </p>
 * The schema name (table name) will be same as entity name by default.
 * 
 * @author despird-zh
 * @version 0.1 2014-2-5 Initial
 * @version 0.1 2014-4-2 Add Indexable Attribute support
 * @since 0.1
 * @see com.dcube.meta.BaseEntity
 * @see com.dcube.core.EntryKey
 **/
public class EntityMeta{

	private String entityClazz = null;
	private Map<String, EntityAttr> attrMap = new HashMap<String, EntityAttr>();
	private String entityName;
	private String description;
	private String schema;
	private EntryKey entryKey = null;
	private Boolean traceable = false;
	private Boolean accessControllable = false;
	private String accessorName = null;
	private String category = null;
	
	/**
	 * Entry Meta constructor 
	 * 
	 * @param entryName The name of Entry
	 * @param entityClazz the class name of Entity class
	 * 
	 * 
	 **/
	public EntityMeta(String entityName, String entityClazz){
		
		this.entityName = entityName;
		this.entityClazz = entityClazz;
		this.schema = entityName;
	}

	/**
	 * Entry Meta constructor 
	 * 
	 * @param entryName The name of Entry
	 * @param entityClazz the class name of Entity Class
	 * @param schema the schema name
	 * 
	 * 
	 **/
	public EntityMeta(String entityName, String entityClazz, String schema){
		
		this.entityName = entityName;
		this.entityClazz = entityClazz;
		this.schema = schema;
	}
	
	/**
	 * Entry Meta constructor 
	 * The schema name is same as entityName by default.
	 * 
	 * @param entryName The name of Entry
	 * 
	 * 
	 **/
	public EntityMeta(String entityName){
		
		this.entityName = entityName;
		this.schema = entityName;
	}
	
	/**
	 * Set the Entry schema class
	 * 
	 * @param  entrySchemaClazz the class name of entry schema
	 **/
	public void setEntityClass(String entityClazz){
		
		this.entityClazz = entityClazz;
		
	}
	
	/**
	 * Get the Entry schema class
	 * 
	 * @return String the class name of entry schema 
	 **/
	public String getEntityClass(){
		
		return this.entityClazz;
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
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else {
				
				attr.setEntityName(entityName);
			}
		}
	}

	/**
	 * Get the entity description 
	 **/
	public String getDescription() {
		return description;
	}

	/**
	 * Set the entity description 
	 **/
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the accessor class
	 **/
	public String getAccessorName() {
		
		return accessorName;
	}

	/**
	 * Set the accessor class
	 **/
	public void setAccessorName(String accessorName) {
		
		this.accessorName = accessorName;
	}
	
	/**
	 * Add Entry Attribute
	 * 
	 * @param attr The Entry Attribute
	 *  
	 * @see com.dcube.meta.EntityAttr
	 **/
	public void addAttr(EntityAttr attr){
		attr.setEntityName(entityName);
		attrMap.put(attr.getFullName(), attr);
	}
	
	/**
	 * Get the entry key 
	 **/
	public EntryKey getEntryKey(){
		
		return this.entryKey;
	}
	
	/**
	 * Public set the entry key 
	 **/
	public void setEntryKey(EntryKey entryKey){
		
		this.entryKey = entryKey;		
	}
	
	/**
	 * Get the hidden Attributes
	 * 
	 * @return List<EntryAttr> the EntryAttribute List
	 * 
	 * @see com.dcube.meta.EntityAttr
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
	 * @see com.dcube.meta.EntityAttr
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
	 * @see com.dcube.meta.EntityAttr
	 **/
	public List<EntityAttr> getRequiredAttrs(){
		
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
	 * @see com.dcube.meta.EntityAttr
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
	 * @see com.dcube.meta.EntityAttr
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
			else if(primitiveFlag && attr.mode == EntityAttr.AttrMode.PRIMITIVE){
				
				attrs.add(attr);
			}else if(!primitiveFlag && attr.mode != EntityAttr.AttrMode.PRIMITIVE){
				
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
				break;
			}
		}
		
		return rtv;
	}

	/**
	 * Get EntityAttr info List by attribute names
	 * @param attrName the attribute names
	 * @return EntryAttr  
	 **/
	public List<EntityAttr> getAttrs(String... attrName){
		
		List<String> attrnames = Arrays.asList(attrName);

		ArrayList<EntityAttr> rtv = new ArrayList<EntityAttr>();
		
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else {
				
				if(attrnames.contains(attr.getAttrName())){			
				
					rtv.add(attr);
				}
			}
		}
		
		return rtv;
	}
	
	/**
	 * Get the schema name ie. the table name 
	 **/
	public String getSchema(){
		
		return schema;
	}
	
	/**
	 * Set schema name 
	 **/
	public void setSchema(String schema){
		
		this.schema = schema;
	}
	
	/**
	 * Set the entity is traceable flag, default not traceable. 
	 **/
	public void setTraceable(Boolean traceable){
		List<EntityAttr> tattrs = EntityManager.getInstance().getTraceableAttributes(entityName);
		if(!traceable){
			
			for(EntityAttr attr:tattrs)
				attrMap.remove(attr.getFullName());
		}else{
			for(EntityAttr attr:tattrs)
				attrMap.put(attr.getFullName(),attr);			
		}
		this.traceable = traceable;
	}
	
	/**
	 * Get the traceable flag 
	 **/
	public Boolean getTraceable(){
		
		return this.traceable;
	}

	/**
	 * set the entity access controllable flag
	 **/
	public void setAccessControllable(Boolean accessControllable){

		this.accessControllable = accessControllable;
	}
	
	/**
	 * get the entity access controllable flag 
	 **/
	public Boolean getAccessControllable(){
		
		return this.accessControllable;
	}

	/**
	 * Get the category of entity 
	 **/
	public String getCategory() {
		return category;
	}

	/**
	 * Set the category of entity 
	 **/
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * Check exist indexable attribute or not. 
	 **/
	public boolean hasIndexableAttr(){
		
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else {
				
				if(attr.isIndexable()){			
				
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Get all the indexable attributes 
	 **/
	public List<EntityAttr> getIndexableAttrs(){
		ArrayList<EntityAttr> attrs = new ArrayList<EntityAttr>();
		for(Map.Entry<String, EntityAttr> entry:attrMap.entrySet()){
			
			EntityAttr attr = entry.getValue();
			if(null == attr) 
				continue;
			else if(attr.isIndexable()){
				
				attrs.add(attr);
			}
		}
		
		return attrs;
	}
}
