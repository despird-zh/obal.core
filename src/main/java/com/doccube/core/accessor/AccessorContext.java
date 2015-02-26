package com.doccube.core.accessor;

import java.util.HashMap;
import java.util.Map;

import com.doccube.core.security.Principal;
import com.doccube.meta.BaseEntity;

/**
 * AccessorContext holds information required during interaction with back-end data storage.
 * <p>When AccessorBuilder build new Accessor instance, it create new context object and hand over it.</p>
 * @author despird
 * @version 0.1 2014-3-1
 * 
 * @see EntityAccessor
 * @see GenericAccessor
 * 
 **/
public class AccessorContext {

	// principal
	private Principal principal = null;
	
	// entity schema 
	private BaseEntity entitySchema = null;
	
	// values of extra setting
	private Map<String, Object> values = new HashMap<String, Object>();
	
	/**
	 * Constructor 
	 * @param principal the principal object
	 * @param schema the entity schema
	 **/
	public AccessorContext(Principal principal, BaseEntity schema){
		
		this.principal = principal;
		this.entitySchema = schema;
	}
	
	/**
	 * Constructor 
	 * @param principal the principal object
	 **/
	public AccessorContext(Principal principal){
		
		this.principal = principal;
	}
	
	/**
	 * Set principal object
	 * 
	 * @param principal The principal object
	 **/
	public void setPrincipal(Principal principal){
		
		this.principal = principal;
	}
	
	/**
	 * Get principal object
	 * 
	 * @return Principal The principal object
	 **/
	public Principal getPrincipal(){
		
		return principal;
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
	 * Put K-V pair to context
	 **/
	public void putValue(String key, Object value){
		
		values.put(key, value);
	}
	
	/**
	 * Get value by Key from context 
	 **/
	@SuppressWarnings("unchecked")
	public <K> K getValue(String key){
		
		return (K) values.get(key);
	}
	
	/**
	 * clear the resource bound to context
	 **/
	public void clear(){
		
		this.principal = null;
		this.entitySchema = null;
		values.clear();
		this.values = null;
	}
}
