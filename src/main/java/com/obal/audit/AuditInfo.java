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
package com.obal.audit;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.lmax.disruptor.EventFactory;
import com.obal.core.EntryInfo;
import com.obal.disruptor.EventPayload;

public class AuditInfo extends EntryInfo implements EventPayload{

	private static final long serialVersionUID = 1L;

	public static final String ENTRY_TYPE_AUDIT="_ENTRY_AUDIT";

	private Date timestamp;
	String subject;
	String verb;
	String object;

	Map<String, String> predicateMap = new HashMap<String, String>();

	AccessPoint accessPoint;
	
	public AuditInfo(String entryType, String key) {
		super(entryType, key);
		timestamp = new Date(System.currentTimeMillis());
	}

	public AuditInfo( String key) {
		super(ENTRY_TYPE_AUDIT, key);
		timestamp = new Date(System.currentTimeMillis());
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public void addPredicate(Predicate predicate) {
		predicateMap.put(predicate.getName(), predicate.getValue());
	}

	public void setPredicateMap(Map<String, String> predicateMap) {
		this.predicateMap = predicateMap;
	}

	public Map<String, String> getPredicateMap() {
		return predicateMap;
	}

	public AccessPoint getAccessPoint() {
		return accessPoint;
	}

	public void setAccessPoint(AccessPoint accessPoint) {
		this.accessPoint = accessPoint;
	}

	@Override
	public int hashCode() {
		
		HashCodeBuilder hashcb = new HashCodeBuilder(17, 37);
		
		return hashcb.append(timestamp)
		.append(subject)
		.append(verb)
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
		.append(this.verb, other.verb)
		.append(this.accessPoint, other.accessPoint)
		.append(this.predicateMap, other.predicateMap);
		
		return eb.isEquals();
		
	}

	@Override
	public String toString() {

		String retValue = "";

		retValue = "AuditEvent(key=" + this.getKey() + ", timestamp=" + this.timestamp
				+ ", subject=" + this.subject + ", verb=" + this.verb
				+ ", object=" + this.object + ", predicateMap = " + this.predicateMap 
				+ ")";

		return retValue;
	}
	
	/**
	 * Copy information from the parameter event
	 * 
	 * @param fromOne the event object.
	 * 
	 **/
	public void copy(AuditInfo fromOne){
		
		this.setKey(fromOne.getKey());
		this.setTimestamp(fromOne.getTimestamp());
		this.setEntityName(fromOne.getEntityName());
		this.setVerb(fromOne.getVerb());
		this.setSubject(fromOne.getSubject());
		this.setObject(fromOne.getObject());
		this.setAccessPoint(fromOne.getAccessPoint());
		this.setPredicateMap(fromOne.getPredicateMap());
	}

	public final static EventFactory<AuditInfo> EVENT_FACTORY = new EventFactory<AuditInfo> (){
		
		@Override
		public AuditInfo newInstance() {

			return new AuditInfo("k-001010");
		}
	};
}
