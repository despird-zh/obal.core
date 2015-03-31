/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 2006-2011, QOS.ch. All rights reserved.
 * 
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *  
 *   or (per the licensee's choosing)
 *  
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package com.dcube.audit;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.disruptor.EventPayload;
import com.lmax.disruptor.EventFactory;

/**
 * AuditInfo hold the audit data 
 * One object is the operation data. operation is business level definition
 * it holds many verbs.
 * A verb is action on table, eg. CRUD etc.
 * 
 * @author despird
 * @version 0.1 2014-3-2
 **/
public class AuditInfo implements EventPayload{

	public static final String DUMMY_OPERATION = "_dummy";
	/** the time stamp */
	private Date timestamp;
	/** the subject - principal or user account */
	String subject;
	/** the business object - business trigger data */
	String object;
	/** the business operation */
	String operation;
	/** access point */
	AccessPoint accessPoint;
	/** verb map key : verb name, value : verb object */
	Map<String, AuditVerb> verbMap = new HashMap<String, AuditVerb>();

	/**
	 * Constructor with operation 
	 **/
	public AuditInfo( String operation) {
		this.operation = operation;
		timestamp = new Date(System.currentTimeMillis());
	}
	
	/**
	 * Get audit time stamp 
	 **/
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Set audit time stamp
	 **/
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Get business object 
	 **/
	public String getObject() {
		return object;
	}

	/**
	 * Set business object 
	 **/
	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * Get subject 
	 **/
	public String getSubject() {
		return subject;
	}

	/**
	 * Set subject - principal 
	 **/
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Get verb object via verb name 
	 **/
	public AuditVerb getVerb(String name) {
		return verbMap.get(name);
	}

	/**
	 * Get all verb objects 
	 **/
	public Collection<AuditVerb> getVerbs(){
		
		return verbMap.values();
	}
	
	/**
	 * Pub verb object 
	 **/
	public void putVerb(String verb, String target) {
		AuditVerb averb = new AuditVerb(verb, target);
		verbMap.put(verb,averb);
	}

	/**
	 * Pub verb object 
	 **/
	public void putVerb(AuditVerb verb) {

		verbMap.put(verb.getVerb(),verb);
	}
	
	/**
	 * Add verb predicate 
	 * @param verb the audit verb
	 * @param predicate the predicate
	 **/
	public void addPredicate(String verb, Predicate predicate) {
		AuditVerb averb = verbMap.get(verb);
		averb.addPredicate(predicate);
	}

	/**
	 * Add verb predicate 
	 **/
	public void addPredicate(String verb, Map<String,String> predicateMap) {
		AuditVerb averb = verbMap.get(verb);
		for(Map.Entry<String, String> entry: predicateMap.entrySet())
			averb.addPredicate(entry.getKey(),entry.getValue());
	}
	
	/**
	 * Get predicate map
	 **/
	public Map<String, String> getPredicateMap(String verb) {
		return verbMap.get(verb).getPredicateMap();
	}

	/**
	 * Get Access Point 
	 **/
	public AccessPoint getAccessPoint() {
		return accessPoint;
	}

	/**
	 * Set Access Point 
	 **/
	public void setAccessPoint(AccessPoint accessPoint) {
		this.accessPoint = accessPoint;
	}
	
	/**
	 * Get business operation 
	 **/
	public String getOperation() {
		return operation;
	}

	/**
	 * Set business operation 
	 **/
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@Override
	public int hashCode() {
		
		HashCodeBuilder hashcb = new HashCodeBuilder(17, 37);
		
		return hashcb.append(timestamp)
		.append(subject)
		.append(operation)
		.append(accessPoint).hashCode();
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AuditInfo other = (AuditInfo) obj;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.timestamp, other.timestamp)
		.append(this.subject, other.subject)
		.append(this.operation, other.operation)
		.append(this.accessPoint, other.accessPoint);
		
		return eb.isEquals();
		
	}

	@Override
	public String toString() {

		String retValue = "";

		retValue = "AuditEvent( timestamp=" + this.timestamp
				+ ", subject=" + this.subject + ", operation=" + this.operation
				+ ", object=" + this.object 
				+ ")";

		return retValue;
	}
	
	/**
	 * Copy information from the parameter event
	 * 
	 * @param fromOne the source object.
	 * 
	 **/
	public void copy(AuditInfo fromOne){
		
		this.setTimestamp(fromOne.getTimestamp());
		this.setOperation(fromOne.getOperation());
		this.setSubject(fromOne.getSubject());
		this.setObject(fromOne.getObject());
		this.setAccessPoint(fromOne.getAccessPoint());
		
		Collection<AuditVerb> avcoll = fromOne.getVerbs();
		for(AuditVerb av : avcoll){
			
			this.putVerb(av);
		}
	}

}
