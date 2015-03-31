package com.dcube.audit;

import java.util.HashMap;
import java.util.Map;

/**
 * AuditVerb hold the step level detail information
 * 
 * @author despird
 **/
public class AuditVerb {
	
	/** the verb */
	private String verb = null;
	/** the target data EntryKey */
	private String target = null;
	/** predicateMap */
	private Map<String, String> predicateMap = new HashMap<String, String>();

	/**
	 * Constructor with verb 
	 **/
	public AuditVerb(String verb){
		this.verb = verb;
	}

	/**
	 * Constructor with verb and target 
	 **/
	public AuditVerb(String verb, String target){
		this.verb = verb;
		this.target = target;
	}
	
	/**
	 * Get target data 
	 **/
	public String getTarget() {
		return target;
	}

	/**
	 * Set target 
	 **/
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * Get verb 
	 **/
	public String getVerb(){
		
		return this.verb;
	}
	
	/**
	 * Add predicate to map 
	 **/
	public void addPredicate(Predicate predicate) {
		predicateMap.put(predicate.getName(), predicate.getValue());
	}
	
	/**
	 * Remove predicate via verb name
	 **/
	public void removePredicate(String verbname) {
		predicateMap.remove(verbname);
	}
	
	/**
	 * Get predicate map 
	 **/
	public Map<String, String> getPredicateMap() {
		return predicateMap;
	}
}
