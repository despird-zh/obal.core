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

/**
 * Entry Filter will wrap the different implementation of filter,
 * the filter object is prepared base on different back-end platform, Hbase, redis etc. 
 **/
public class EntryFilter<F> {
	
	F filter ;
	
	public EntryFilter(){}
	
	/**
	 * Constructor
	 * @param filter The filter object 
	 **/
	public EntryFilter(F filter){
		
		this.filter = filter;
	};
	
	/**
	 * Get the filter object 
	 **/
	public F getFilter(){
		
		return this.filter;
	}
	
	/**
	 * Set filter object
	 **/
	public void setFilter(F filter){
		
		this.filter = filter;
	}
	
	/**
	 * Null check 
	 **/
	public boolean isNull(){
		
		return null == this.filter;
	}
	
	/**
	 * Support check 
	 **/
	public boolean supportCheck(Class<?> clazz){
		
		return clazz.isInstance(filter);
	}
}
