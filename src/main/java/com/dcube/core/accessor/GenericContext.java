package com.dcube.core.accessor;

import java.util.HashMap;
import java.util.Map;

import com.dcube.audit.AuditInfo;
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

	/** the embed flag */
	private boolean embed = false;
	
	/** principal */
	private Principal principal = null;

	/** values of extra setting */
	protected Map<String, Object> values = null;
	
	/** the context parent */
	private GenericContext parent = null;
	
	/** the audit information */
	private AuditInfo auditInfo = null;
	
	/**
	 * Constructor 
	 * @param parent the parent context
	 **/
	public GenericContext(GenericContext parent){
		
		this.parent = parent;
		this.embed = true;
	}
	
	/**
	 * Constructor 
	 * @param principal the principal object
	 **/
	public GenericContext(Principal principal){
		
		this.principal = principal;
		this.values = new HashMap<String, Object>();
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
		
		if(embed && parent != null){
			return parent.getPrincipal();
			
		}else{
			return principal;
		}
	}
		
	/**
	 * Put K-V pair to context
	 **/
	public void putValue(String key, Object value){
		
		if(embed && parent != null){
			
			parent.putValue(key, value);
		}else{
			values.put(key, value);
		}
	}
	
	/**
	 * Get value by Key from context 
	 * 
	 **/
	@SuppressWarnings("unchecked")
	public <K> K getValue(String key){
		
		if(embed && parent != null){
			return (K) parent.values.get(key);
		}else {
			return (K) values.get(key);
		}
	}
		
	/**
	 * clear the resource bound to context
	 * 
	 * @param purge true:clear the values; false:keep values
	 **/
	public void clear(){
		
		if(embed){ // embed context, only release the parent reference.
			parent = null;
		}else{
			principal = null;
			values.clear();
			if(auditInfo != null)
				auditInfo.reset();
		}
	}
	
	/**
	 * Get embed flag
	 **/
	public boolean isEmbed(){
		
		return embed;
	}

	/**
	 * Audit on with specified operation
	 **/
	public void auditOn(String operation){
		
		if(embed){
			// embed context hand over to parent context.
			parent.auditOn(operation);
			return;
		}else if(auditInfo == null){
			auditInfo = new AuditInfo(operation);
		}else{
			auditInfo.reset();
			auditInfo.setOperation(operation);
		}
		// set audit subject
		auditInfo.setSubject(principal.getAccount());
	}
	
	/**
	 * Get AuditInfo object 
	 **/
	public AuditInfo getAuditInfo(){
		if(embed){
			return parent.getAuditInfo();
		}else{
			return this.auditInfo;
		}
	}
}
