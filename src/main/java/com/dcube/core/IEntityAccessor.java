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
package com.dcube.core;

import com.dcube.core.accessor.EntryCollection;
import com.dcube.exception.AccessorException;
import com.dcube.meta.BaseEntity;
/**
 * Base interface of Entry Accessor intance
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 * @see IBaseAccessor
 * @see BaseEntity
 * @see EntryWrapper
 * @see EntryKey
 **/
public interface IEntityAccessor <GB extends IEntityEntry> extends IBaseAccessor{

	public static final String AUDIT_OPER_NEWKEY = "new.key";
	public static final String AUDIT_OPER_PUT_ENTRY = "put.entry";
	public static final String AUDIT_OPER_PUT_ATTR = "put.entry.attr";
	public static final String AUDIT_OPER_GET_ENTRY = "get.entry";
	public static final String AUDIT_OPER_GET_ATTR = "get.entry.attr";
	public static final String AUDIT_OPER_DEL_ENTRY = "del.entry";
	public static final String AUDIT_OPER_DEL_ATTR = "del.entry.attr";
	public static final String AUDIT_OPER_SCAN = "scan.entry";
	
	/**
	 * Generate new key for entity 
	 * @throws AccessorException 
	 * 
	 **/
	public abstract EntryKey newKey(Object ... parameter) throws AccessorException;
	
	/**
	 * Get the entry schema  
	 * @return entry schema
	 **/
	public abstract BaseEntity getEntitySchema();

	/**
	 * put entry object
	 * @param entryInfo the entry information
	 * @param changedOnly true:only changed be updated; false:update all
	 **/
	public abstract EntryKey doPutEntry(GB entryInfo, boolean changedOnly) throws AccessorException;
	
	/**
	 * put attribute to entry
	 * 
	 * @param entryKey the entry key
	 * @param attrName the attribute name
	 * @param value the value
	 * 
	 *  @return EntryKey the key updated
	 **/
	public abstract EntryKey doPutEntryAttr(String entryKey, String attrName, Object value) throws AccessorException;
	
	/**
	 * get entry object
	 * @param entryKey the entry key
	 * @return GB - the entry information
	 **/
	public abstract GB doGetEntry(String entryKey)throws AccessorException;

	/**
	 * Get entry object
	 * 
	 * @param entryKey the entry key
	 * @param attributes the attribute array
	 * 
	 * @return GB - the entry information
	 **/
	public abstract GB doGetEntry(String entryKey, String... attributes)throws AccessorException;
	
	/**
	 * Get entry attribute 
	 * 
	 * @param entryKey the entry key
	 * @param attrName the attribute name
	 * 
	 *  @return the value of attribute
	 **/
	public abstract <K> K doGetEntryAttr(String entryKey,String attrName) throws AccessorException;
	
	/**
	 * Delete entry object
	 * 
	 * @param entryKey the entry key array
	 **/
	public abstract void doDelEntry(String... entryKey)throws AccessorException;

	/**
	 * Delete entry object's specified attribute
	 * 
	 * @param attribute the attribute of entity
	 * @param entryKey the entry key array
	 **/
	public abstract void doDelEntryAttr(String attribute, String... entryKey)throws AccessorException;
	
	/**
	 * Get entry object list
	 * 
	 * @param scanfilter the scan filter object
	 * 
	 * @return EntryCollection<GB> - the entry collection
	 **/
	public abstract EntryCollection<GB> doScanEntry(EntryFilter<?> scanfilter)throws AccessorException;


	/**
	 * get entry object list
	 * 
	 * @param scanfilter the scan filter object
	 * @param attributes the attribute array
	 * 
	 * @return EntryCollection<GB> - the entry collection
	 **/
	public abstract EntryCollection<GB> doScanEntry(EntryFilter<?> scanfilter, String... attributes)throws AccessorException;

	
	/**
	 * check filter object is supported or not
	 * @param scanfilter the filter used to scan entries
	 * @param throwExcep in case not supported, true:throw exception; false:return false;  
	 * 
	 * @return true: supported ; false: unsupported
	 **/
	public abstract boolean isFilterSupported(EntryFilter<?> scanfilter,boolean throwExcep) throws AccessorException;
	
	/**
	 * Here define a blank method  
	 **/
	public <To> IEntryConverter<GB, To> getEntryConverter(Class<To> cto);
}
