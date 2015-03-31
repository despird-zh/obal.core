package com.dcube.util;

import com.dcube.audit.AccessPoint;
import com.dcube.audit.AuditHooker;
import com.dcube.audit.AuditInfo;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.disruptor.EventType;

/**
 * Utility Class help to collect and generate audit information.
 * 
 * @author despird 
 * @version 0.1 2014-2-3
 * 
 **/
public class AuditUtils {

	/**
	 * Register the AuditHooker to EventDispatcher 
	 **/
	public static void regAuditHooker(){
		
		AuditHooker auditHooker = new AuditHooker();
		EventDispatcher.getInstance().regEventHooker(auditHooker);
	}
	
	/**
	 * Unregister the AuditHooker to EventDispatcher 
	 **/
	public static void unRegAuditHooker(){
		
		EventDispatcher.getInstance().unRegEventHooker(EventType.AUDIT);
	}
	
	/**
	 * Send audit information
	 * 
	 * @param auditEvent the audit event payload
	 **/
	public static void doAudit(AuditInfo auditEvent){
		
		EventDispatcher.getInstance().sendPayload(auditEvent,EventType.AUDIT);
	}

	/**
	 * Get the AccessPoint object. 
	 **/
	public static AccessPoint getAccessPoint(String name){
		
		return new AccessPoint(name);
	}
}
