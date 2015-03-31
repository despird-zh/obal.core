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
package com.dcube.core.accessor;

import java.util.Objects;

import com.dcube.core.IBaseAccessor;

/**
 * The interface general use Accessor, these accessor provides method not
 * constraint on certain entry. eg a method might operation on more than one
 * entry, we will write it in GeneralAccessor.
 * 
 * @author despird-zh
 * @version 0.1 2014-3-1
 * 
 **/
public abstract class GenericAccessor implements IBaseAccessor {
		
	/** thread local */
	private GenericContext context;
	
	private String accessorName;
	
	/**
	 * Constructor with entry schema information 
	 * 
	 * @param context the context that provides principal etc. 
	 **/
	public GenericAccessor(String accessorName, GenericContext context){
		this.accessorName = accessorName;
		this.context = context;
	}

	/**
	 * Get the accessor name 
	 **/
	public String getAccessorName(){
		
		return this.accessorName;
	}
	
	/**
	 * Set the context of GenericAccessor 
	 **/
	public void setContext(GenericContext context){
		
		this.context = context;
	}
	
	/**
	 * Get the context of GenericAccessor 
	 **/
	public GenericContext getContext(){
		
		return this.context;
	}
	
	/**
	 * Release the entity schema and clear the principal in it.
	 **/
	public void close(){
		
		if(context != null){
			// not embed accessor, purge all resource;embed only release object pointers.
			context.clear();		
			
		}
	}
		
	public boolean isEmbed(){
		Objects.requireNonNull(this.context);
		return context.isEmbed();
	}
	
}
