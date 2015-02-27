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

import java.util.HashMap;
import java.util.Map;

import com.doccube.core.IBaseAccessor;

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
	private ThreadLocal<Map<String, Object>> localVars = new ThreadLocal<Map<String, Object>>();
	
	/**
	 * Constructor with entry schema information 
	 * 
	 * @param context the context that provides principal etc. 
	 **/
	public GenericAccessor(GenericContext context){
		
		localVars.set(new HashMap<String, Object>());
		localVars.get().put(LOCAL_CONTEXT, context);
	}

	
	public void setAccessorContext(GenericContext context){
		
		localVars.get().put(LOCAL_CONTEXT, context);
	}
	
	public GenericContext getAccessorContext(){
		
		return (GenericContext)localVars.get().get(LOCAL_CONTEXT);
	}
	
	/**
	 * Get the local variables 
	 **/
	protected ThreadLocal<Map<String, Object>> getLocalVars(){
		
		return localVars;
	}
	
	/**
	 * Release the entity schema and clear the principal in it.
	 **/
	public void release(){
		
		GenericContext context = (GenericContext)localVars.get().get(LOCAL_CONTEXT);

		if(context != null){
			// not embed accessor, purge all resource;embed only release object pointers.
			context.clear();		
			localVars.get().clear();// release objects.	
			
		}
	}
		
	public boolean isEmbed(){
		
		GenericContext context = (GenericContext)localVars.get().get(LOCAL_CONTEXT);
		return context.isEmbed();
	}
	
	public void setEmbed(boolean embed){
		
		GenericContext context = (GenericContext)localVars.get().get(LOCAL_CONTEXT);
		context.setEmbed(embed);
	}
}
