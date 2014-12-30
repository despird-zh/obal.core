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
package com.obal.core;

import java.util.List;

import com.obal.exception.AccessorException;
import com.obal.meta.BaseEntity;
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
public interface IEntityAccessor <GB extends EntryKey> extends IBaseAccessor{
	
	/**
	 * Generate new key for entity 
	 * @throws AccessorException 
	 * 
	 **/
	public abstract EntryKey newKey() throws AccessorException;
	
	/**
	 * Get the entry schema  
	 * @return entry schema
	 **/
	public abstract BaseEntity getEntitySchema();

	/**
	 * put entry object
	 * @param entryInfo the entry information
	 **/
	public abstract EntryKey doPutEntry(GB entryInfo) throws AccessorException;
	
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
	 * @param entrykey the entry key
	 * @return GB - the entry information
	 **/
	public abstract GB doGetEntry(String entryKey)throws AccessorException;

	/**
	 * get entry attribute 
	 * @param entryKey the entry key
	 * @param attrName the attribute name
	 * 
	 *  @return the value of attribute
	 **/
	public abstract <K> K doGetEntryAttr(String entryKey,String attrName) throws AccessorException;
	
	/**
	 * delete entry object
	 * @param entrykey the entry key array
	 **/
	public abstract void doDelEntry(String... entryKey)throws AccessorException;

	/**
	 * get entry object list
	 * @param scan the scan object
	 * @return List<GB> - the entry information
	 **/
	public abstract List<GB> doScanEntry(EntryFilter<?> scanfilter)throws AccessorException;

	/**
	 * check filter object is supported or not
	 * @param scanfilter the filter used to scan entries
	 * @param throwExcep in case not supported, true:throw exception; false:return false;  
	 **/
	public abstract boolean isFilterSupported(EntryFilter<?> scanfilter,boolean throwExcep) throws AccessorException;
}
