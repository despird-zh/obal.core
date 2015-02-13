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
package com.doccube.core.accessor;

import com.doccube.core.IBaseAccessor;

/**
 * The interface general use Accessor, these accessor provides method not
 * constraint on certain entry. eg a method might operation on more than one
 * entry, we will write it in GeneralAccessor.
 **/
public abstract class GenericAccessor implements IBaseAccessor {
	
	/** thread local */
	private ThreadLocal<AccessorContext> localContext = new ThreadLocal<AccessorContext>();
	
	/**
	 * Constructor with entry schema information 
	 * 
	 * @param context the context that provides principal etc. 
	 **/
	public GenericAccessor(AccessorContext context){
		
		localContext.set(context);
	}
	
	private boolean embed = false;
	
	public boolean isEmbed(){
		
		return embed;
	}
	
	public void setEmbed(boolean embed){
		
		this.embed = embed;
	}
	
	public void setAccessorContext(AccessorContext context){
		
		localContext.set(context);
	}
	
	public AccessorContext getAccessorContext(){
		
		return localContext.get();
	}
	
	/**
	 * Release the entity schema and clear the principal in it.
	 **/
	public void release(){
		
		if(localContext != null){
			AccessorContext context = localContext.get();
			// clear entity schema
			//BaseEntity schema = context.getEntitySchema();
		
			context.clear();// release objects.			
			localContext.remove();
			
		}
	}
}
