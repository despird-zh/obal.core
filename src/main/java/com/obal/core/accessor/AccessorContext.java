package com.obal.core.accessor;

import java.util.HashMap;
import java.util.Map;

import com.obal.core.security.Principal;
import com.obal.meta.BaseEntity;

public class AccessorContext {

	private Principal principal = null;
	
	private BaseEntity entitySchema = null;
	
	private Map<String, Object> values = new HashMap<String, Object>();
	
	public AccessorContext(Principal principal, BaseEntity schema){
		
		this.principal = principal;
		this.entitySchema = schema;
	}
	
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
	
	public void putValue(String key, Object value){
		
		values.put(key, value);
	}
	
	public <K> K getValue(String key){
		
		return (K) values.get(key);
	}
	
	public void clear(){
		
		this.principal = null;
		this.entitySchema = null;
		values.clear();
		this.values = null;
	}
}
