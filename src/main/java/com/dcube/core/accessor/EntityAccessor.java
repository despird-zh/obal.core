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

import java.util.ArrayList;
import java.util.List;

import com.dcube.audit.AuditInfo;
import com.dcube.audit.Predicate;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntityAccessor;
import com.dcube.core.IEntityEntry;
import com.dcube.core.IEntryConverter;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.exception.MetaException;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityAttr;

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
	 * get entry wrapper
	 * @return wrapper object 
	 **/
	public abstract GB newEntryObject();
		
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
		
		return context == null? false:context.isEmbed();
	}

	/**
	 * Get the entry converter object.
	 * Here define a blank method  
	 **/
	public <To> IEntryConverter<GB, To> getEntryConverter(Class<To> cto){
		
		throw new UnsupportedOperationException("Not define any converter yet.");
	}
	
	/**
	 * Validate if the attribute not missing 
	 * @param entryInfo 
	 * @return boolean true: entry's required attributes is present; false not valid.
	 **/
	public boolean validateEntry(GB entryInfo){
		
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		if(entitySchema == null) return false;
		List<EntityAttr> requiredAttrs = entitySchema.getEntityMeta().getRequiredAttrs();
		List<String> missingattrs = new ArrayList<String>();
		for(EntityAttr attr: requiredAttrs){
			// 
			if(!entryInfo.checkRequired(attr.getEntityName(), attr.getAttrName())){
				
				missingattrs.add(attr.getAttrName());
			}
		}
		
		context.putValue(AccessorContext.KEY_VALID_MSG, missingattrs.toString());
		return missingattrs.size() == 0 ? true:false;
		
	}

}
