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

import com.dcube.audit.AuditInfo;
import com.dcube.audit.Predicate;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntityAccessor;
import com.dcube.core.IEntityEntry;
import com.dcube.core.IEntryConverter;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.exception.MetaException;
import com.dcube.meta.BaseEntity;

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
public abstract class EntityAccessor<GB extends IEntityEntry> implements IEntityAccessor <GB>{

	
	private AccessorContext context;
	
	private String accessorName;
	
	/**
	 * Constructor with entry schema information 
	 * 
	 * @param context the context that provides principal etc. 
	 **/
	public EntityAccessor(String accessorName, AccessorContext context){
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
	 * Set the context of EntityAccessor 
	 **/
	public void setContext(GenericContext context) throws AccessorException{
		
		if(!(context instanceof AccessorContext))
			throw new AccessorException("context must be AccessorContext.");
		
		this.context = (AccessorContext)context;
	}
	
	/**
	 * Get the context of EntityAccessor
	 **/
	public AccessorContext getContext(){
		
		return context;
	}
	
	@Override
	public EntryKey newKey(Object ... parameter) throws AccessorException{
		
		context.auditBegin(AUDIT_OPER_NEWKEY);
		EntryKey key = null;
		try {
			if(null == getEntitySchema())
				throw new AccessorException("The entity schema not set yet");
			
			key = getEntitySchema().newKey(getContext().getPrincipal(),parameter);
		} catch (MetaException e) {
			
			throw new AccessorException("Error when generating entry key",e);
		}
		// collect audit information
		AuditInfo audit = context.getAuditInfo();
		audit.getVerb(AUDIT_OPER_NEWKEY)
			.setTarget(key.toString());
		audit.addPredicate(AUDIT_OPER_NEWKEY, Predicate.KEY_PARAM, parameter);
		context.auditEnd();
		return key;
	}
	
	/**
	 * Get the entity schema  
	 * 
	 * @return entity schema
	 **/
	@Override
	public BaseEntity getEntitySchema(){
	
		return context == null? null:context.getEntitySchema();
	}
	
	/**
	 * Get the principal bound to the EntityAccessor object. 
	 **/
	public Principal getPrincipal(){
		
		return context == null? null:context.getPrincipal();
	}

	
	/**
	 * Create an empty EntityEntry Object, this is used for cache R/W to avoid 
	 * Generic type GB new operation.
	 * 
	 * @return wrapper object 
	 **/
	public abstract GB newEntityEntryObject();
	
	/**
	 * For cache access only
	 **/
	public <K> K doGetEntryAttr(String entryKey ,String attrName ) throws AccessorException{
		
		GB placeholder = newEntityEntryObject();// Only used to retrieve class object.
		@SuppressWarnings("unchecked")
		IEntityAccessor<GB> cacheAccessor = (IEntityAccessor<GB>)AccessorFactory.buildCacheAccessor(context, placeholder.getClass());
		// Read cache data
		@SuppressWarnings("unchecked")
		K rtv = (K)cacheAccessor.doGetEntryAttr(entryKey, attrName);
		
		return rtv;
	}
	
	/**
	 * For cache access only 
	 **/
	public GB doGetEntry(String entryKey) throws AccessorException{
		
		GB placeholder = newEntityEntryObject();// Only used to retrieve class object.
		@SuppressWarnings("unchecked")
		IEntityAccessor<GB> cacheAccessor = (IEntityAccessor<GB>)AccessorFactory.buildCacheAccessor(context, placeholder.getClass());
		// Read cache data
		placeholder = cacheAccessor.doGetEntry(entryKey);
		
		return placeholder;
	}
	
	/**
	 * For cache access only 
	 **/
	public GB doGetEntry(String entryKey, String... attributes)throws AccessorException{
		
		GB placeholder = newEntityEntryObject();// Only used to retrieve class object.
		@SuppressWarnings("unchecked")
		IEntityAccessor<GB> cacheAccessor = (IEntityAccessor<GB>)AccessorFactory.buildCacheAccessor(context, placeholder.getClass());
		// Read cache data
		placeholder = cacheAccessor.doGetEntry(entryKey, attributes);
		
		return placeholder;
	}

	/**
	 * For cache access only 
	 **/
	@Deprecated
	public EntryKey doPutEntryAttr(String entryKey, String attrName,  Object value) throws AccessorException{
		
		GB placeholder = newEntityEntryObject();// Only used to retrieve class object.
		@SuppressWarnings("unchecked")
		IEntityAccessor<GB> cacheAccessor = (IEntityAccessor<GB>)AccessorFactory.buildCacheAccessor(context, placeholder.getClass());
		// Read cache data
		EntryKey rtv = cacheAccessor.doPutEntryAttr(entryKey, attrName, value);
		return rtv;
	}
	
	/**
	 * For cache access only 
	 **/
	@Deprecated
	public EntryKey doPutEntry(GB entryInfo) throws AccessorException {
		
		GB placeholder = newEntityEntryObject();// Only used to retrieve class object.
		@SuppressWarnings("unchecked")
		IEntityAccessor<GB> cacheAccessor = (IEntityAccessor<GB>)AccessorFactory.buildCacheAccessor(context, placeholder.getClass());
		// Read cache data
		EntryKey rtv = cacheAccessor.doPutEntry(entryInfo);
		
		return rtv;
	}
	
	/**
	 * For cache access only 
	 **/
	@Deprecated
	public void doDelEntry(String... rowkeys) throws AccessorException {
		
		GB placeholder = newEntityEntryObject();// Only used to retrieve class object.
		@SuppressWarnings("unchecked")
		IEntityAccessor<GB> cacheAccessor = (IEntityAccessor<GB>)AccessorFactory.buildCacheAccessor(context, placeholder.getClass());
		// Read cache data
		cacheAccessor.doDelEntry(rowkeys);
	}
	
	/**
	 * For cache access only 
	 **/
	@Deprecated
	public void doDelEntryAttr(String attribute, String... rowkeys)throws AccessorException{
		GB placeholder = newEntityEntryObject();// Only used to retrieve class object.
		@SuppressWarnings("unchecked")
		IEntityAccessor<GB> cacheAccessor = (IEntityAccessor<GB>)AccessorFactory.buildCacheAccessor(context, placeholder.getClass());
		// Read cache data
		cacheAccessor.doDelEntryAttr(attribute, rowkeys);
	}
	
	/**
	 * Release the entity schema and clear the principal in it.
	 **/
	public void close(){
			
		if(context != null){
			// not embed accessor, purge all resource;embed only release object pointers.
			context.clear();		
			context = null;
		}
	}
	
	/**
	 * Check if the EntityAccessor is an embedded one. 
	 **/
	public boolean isEmbed(){
		
		return context.isEmbed();
	}

	/**
	 * Get the entry converter object.
	 * Here define a blank method  
	 **/
	public <To> IEntryConverter<GB, To> getEntryConverter(Class<To> cto){
		
		throw new UnsupportedOperationException("Not define any converter yet.");
	}
}
