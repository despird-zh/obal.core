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

import com.doccube.core.IEntityAccessor;
import com.doccube.core.security.Principal;
import com.doccube.meta.BaseEntity;

/**
 * Abstract EntryAccessor with EntrySchema information, it provides operation on
 * certain Entity
 * <p>The EntityAccessor instances if cached in AccessorBuilder, in order to ensure the thread safe 
 * the AccessorContext is a thread-local variable. to avoid memory leak the release() method must be called</p>
 * <p>In case of the EntityAccessor(EA) be acquired in GenericAccessor(GA), the GA and EA share the same connection
 * object, in fact the EA's connection is fetch from GE but the embed flag is set true by AccessorBuilder. When release
 * Connection must do it in GE, otherwise(embed is false) release directly.</p>
 * 
 * @author despird-zh
 * @version 0.1 2014-3-2
 * 
 **/
public abstract class EntityAccessor<GB extends EntryInfo> implements IEntityAccessor <GB>{

	private boolean embed = false;
	private ThreadLocal<GenericContext> localContext = new ThreadLocal<GenericContext>();
	
	/**
	 * Constructor with entry schema information 
	 * 
	 * @param context the context that provides principal etc. 
	 **/
	public EntityAccessor(AccessorContext context){
		
		localContext.set(context);
	}
	
	public void setAccessorContext(GenericContext context){
		
		localContext.set(context);
	}
	
	public AccessorContext getAccessorContext(){
		
		return (AccessorContext)localContext.get();
	}
	
	/**
	 * Get the entity schema  
	 * 
	 * @return entity schema
	 **/
	@Override
	public BaseEntity getEntitySchema(){
		AccessorContext context = (AccessorContext)localContext.get();
		return context == null? null:context.getEntitySchema();
	}
	
	/**
	 * Get the principal bound to the EntityAccessor object. 
	 **/
	public Principal getPrincipal(){
		AccessorContext context = (AccessorContext)localContext.get();
		return context == null? null:context.getPrincipal();
	}
	
	/**
	 * Release the entity schema and clear the principal in it.
	 **/
	public void release(){
		// not embed accessor
		if(localContext != null){
			
			AccessorContext context = (AccessorContext)localContext.get();
			// clear entity schema
			//BaseEntity schema = context.getEntitySchema();

			context.clear();// release objects.			
			localContext.remove();
			
		}
	}
		
	public boolean isEmbed(){
		
		return embed;
	}
	
	public void setEmbed(boolean embed){
		
		this.embed = embed;
	}

}
