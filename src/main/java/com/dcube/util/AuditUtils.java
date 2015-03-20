package com.dcube.util;

import com.dcube.audit.AuditHooker;
import com.dcube.audit.AuditInfo;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.disruptor.EventPayload;
import com.dcube.disruptor.EventType;

public class AuditUtils {

	public static void regAuditHooker(){
		
		AuditHooker auditHooker = new AuditHooker();
		EventDispatcher.getInstance().regEventHooker(auditHooker);
	}
	
	public static void unRegAuditHooker(){
		
		EventDispatcher.getInstance().unRegEventHooker(EventType.AUDIT);
	}
	
	public static void doAudit(AuditInfo auditevent){
		
		EventDispatcher.getInstance().sendPayload(auditevent,EventType.AUDIT);
	}

}
