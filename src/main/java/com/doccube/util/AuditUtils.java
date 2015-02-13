package com.doccube.util;

import com.doccube.audit.AuditHooker;
import com.doccube.audit.AuditInfo;
import com.doccube.disruptor.EventDispatcher;
import com.doccube.disruptor.EventPayload;
import com.doccube.disruptor.EventType;

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
