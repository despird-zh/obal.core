package com.dcube.core.accessor;

import java.util.HashMap;
import java.util.Map;

import com.dcube.core.security.Principal;

/**
 * GenericContext holds information required during interaction with back-end data storage.
 * <p>When AccessorBuilder build new Accessor instance, it create new context object and hand over it.</p>
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 * @see GenericAccessor
 * 
 **/
public class GenericContext {

	private boolean embed = false;
	
	// principal
	private Principal principal = null;

	// values of extra setting
	protected Map<String, Object> values = new HashMap<String, Object>();
		
	/**
	 * Constructor 
	 * @param principal the principal object
	 **/
	public GenericContext(Principal principal){
		
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
	 * Put K-V pair to context
	 **/
	public void putValue(String key, Object value){
		
		values.put(key, value);
	}
	
	/**
	 * Get value by Key from context 
	 * 
	 **/
	@SuppressWarnings("unchecked")
	public <K> K getValue(String key){
		
		return (K) values.get(key);
	}
	
	/**
	 * Copy the context data(except schema data) to target context.
	 * 
	 * @param target the target context
	 **/
	public void copy(GenericContext target){
		
		target.setPrincipal(this.getPrincipal());
		target.values = this.values;
	}
	
	/**
	 * clear the resource bound to context
	 * 
	 * @param purge true:clear the values; false:keep values
	 **/
	public void clear(){
		
		this.principal = null;
		if(embed)
			this.values.clear();
		this.values = null;
	}
	
	/**
	 * Get embed flag
	 **/
	public boolean isEmbed(){
		
		return embed;
	}
	
	/**
	 * Set embed flag
	 **/
	public void setEmbed(boolean embed){
		
		this.embed = embed;
	}
}
