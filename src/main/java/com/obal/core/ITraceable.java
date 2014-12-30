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

import java.util.Date;

/**
 * ITraceable interface indicate the object holds tracing information. 
 * 
 * @see EntryInfo
 * @author despird
 * @version 0.1 2014-3-2
 * @since 0.1
 **/
public interface ITraceable {
	
	/**
	 * The creator of entry 
	 **/
	public String getCreator() ;

	/**
	 * Set the creator of entry 
	 **/
	public void setCreator(String creator) ;

	/**
	 * Get modifier of entry 
	 **/
	public String getModifier() ;

	/**
	 * Set modifier of entry 
	 **/
	public void setModifier(String modifier) ;

	/**
	 * Get create date of entry 
	 **/
	public Date getNewCreate() ;

	/**
	 * Set create date of entry 
	 **/
	public void setNewCreate(Date newCreate) ;

	/**
	 * Get modify date of entry 
	 **/
	public Date getLastModify() ;

	/**
	 * Set modify date of entry 
	 **/
	public void setLastModify(Date lastModify) ;
}
