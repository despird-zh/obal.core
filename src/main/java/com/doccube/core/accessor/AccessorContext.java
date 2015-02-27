package com.doccube.core.accessor;

import com.doccube.core.security.Principal;
import com.doccube.meta.BaseEntity;

/**
 * AccessorContext holds information required during interaction with back-end data storage.
 * <p>When AccessorBuilder build new Accessor instance, it create new context object and hand over it.</p>
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 * @see EntityAccessor
 * @see GenericAccessor
 * 
 **/
public class AccessorContext extends GenericContext{

	// entity schema 
	private BaseEntity entitySchema = null;

	/**
	 * Constructor 
	 * @param principal the principal object
	 * @param schema the entity schema
	 **/
	public AccessorContext(Principal principal, BaseEntity schema){
		
		super(principal);
		this.entitySchema = schema;
	}
	
	/**
	 * Constructor 
	 * @param principal the principal object
	 **/
	public AccessorContext(Principal principal){
		
		super(principal);
	}
		
	/**
	 * Get the entity schema  
	 * 
	 * @return entity schema
	 **/
	public BaseEntity getEntitySchema(){
		
		return this.entitySchema;
	}
	
	/**
	 * Set the entity schema 
	 **/
	public void setEntitySchema(BaseEntity entitySchema){
		
		this.entitySchema = entitySchema;
	}
	
	/**
	 * clear the resource bound to context
	 **/
	public void clear(){

		this.entitySchema = null;
		super.clear();
	}
}
