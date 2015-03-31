package com.dcube.core.accessor;

import com.dcube.core.security.Principal;
import com.dcube.meta.BaseEntity;

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

	/** entity schema */
	private BaseEntity entitySchema = null;
	
	/**
	 * Constructor 
	 * @param parent the parent context object
	 * @param schema the schema object
	 **/
	public AccessorContext(GenericContext parent,BaseEntity schema){
		
		super(parent);
		this.entitySchema = schema;
	}
	
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
	 * 	 
	 * @param purge true:clear the values; false:keep values
	 **/
	@Override
	public void clear(){

		this.entitySchema = null;
		super.clear();
	}
}
