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

import java.util.Date;

import com.obal.core.EntryKey;
import com.obal.core.ITraceable;

/**
 * EntryAttr wrap the setting of entry attribute.
 * 
 * @author G.Obal
 * @since 0.1
 **/
public class EntityAttr implements ITraceable{

	public AttrType type = AttrType.STRING;
	public AttrMode mode = AttrMode.PRIMITIVE;
	
	private String attrName;
	private String format;
	private String column;
	private String entityName;
	private String qualifier;	
	private boolean hidden = false;
	private boolean readonly = false;
	private boolean required = false;
	private boolean primary = false;
	private String description = null;
	private EntryKey entryKey = null;
	
	/**
	 * Constructor
	 * <p>The attribute default mode is PRIMITVE, default type is STRING</p>
	 * @param attrName the attribute name
	 *  @param column the column name
	 *  @param qualifier the qualifier for attribute
	 **/
	public EntityAttr(String attrName, String column, String qualifier){
		
		this.type = AttrType.STRING;
		this.attrName = attrName;
		this.column = column;
		this.qualifier = qualifier;
	}

	/**
	 * Constructor
	 * <p>The attribute default mode is PRIMITVE</p>
	 * @param attrName the attribute name
	 * @param type the Attribute type 
	 *  @param column the column name
	 *  @param qualifier the qualifier for attribute
	 **/	
	public EntityAttr(String attrName, AttrType type, String column, String qualifier){
		
		this.type = type == null ? AttrType.STRING : type;
		this.attrName = attrName;
		this.column = column;
		this.qualifier = qualifier;
	}

	/**
	 * Constructor
	 * 
	 * @param attrName the attribute name
	 * @param mode the Attribute mode:PRIMITIVE,MAP,LIST 
	 * @param type the Attribute type:INT,DOUBLE etc.
	 *  @param column the column name
	 *  @param qualifier the qualifier for attribute
	 **/	
	public EntityAttr(String attrName,AttrMode mode, AttrType type, String column, String qualifier){
	
		this.mode = mode == null ? AttrMode.PRIMITIVE : mode;
		this.type = type == null ? AttrType.STRING : type;
		this.attrName = attrName;
		this.column = column;
		this.qualifier = qualifier;
	}
	
	/**
	 * Get the attribute name 
	 **/
	public String getAttrName(){
		
		return this.attrName;
	}
	
	/**
	 * Get the column name 
	 **/
	public String getColumn(){
		
		return this.column;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Get the Qualifier name 
	 * 
	 * 
	 **/
	public String getQualifier(){
		
		return this.qualifier;
	}
	
	/**
	 * Get the format of attribute 
	 **/
	public String getFormat(){
				
		return this.format;
	}
	
	/**
	 * Set the format of attribute.
	 **/	
	public void setFormat(String format){
		
		this.format = format;
	}
	
	/**
	 * is the attribute numberic 
	 **/
	public boolean isNumberic(){
		
		return type == AttrType.DOUBLE || type == AttrType.INTEGER;
	}
	
	/**
	 * is the attribute hidden 
	 **/
	public boolean isHidden(){
		
		return this.hidden;
	}
	
	/**
	 * set the attribute hidden 
	 **/
	public void setHidden(boolean hidden){
		
		this.hidden = hidden;
	}

	/**
	 * is attribute readonly 
	 **/
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * set the attribute readonly 
	 **/
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * is the attribute mandatory 
	 **/
	public boolean isRequired() {
		return required;
	}

	/**
	 * set the attribute mandatory 
	 **/
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * is the attribute primitive 
	 **/
	public boolean isPrimary() {
		return primary;
	}

	/**
	 * set the attribute primitive 
	 **/
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}	
	
	public EntryKey getEntryKey(){
		
		return this.entryKey;
	}
	
	public void setEntryKey(EntryKey entryKey){
		
		this.entryKey = entryKey;		
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
