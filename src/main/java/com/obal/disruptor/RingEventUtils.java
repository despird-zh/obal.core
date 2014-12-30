package com.obal.disruptor;

import com.obal.exception.RingEventException;

public class RingEventUtils {

	public static void sendPayload(EventPayload payload, EventType eventType){
		
		EventDispatcher.getInstance().sendPayload(payload,eventType);
	}
	
	public static void regEventHooker(EventHooker<?> eventHooker){
		
		EventDispatcher.getInstance().regEventHooker(eventHooker);
	}
	
	public static void unRegEventHooker(EventType eventType){
		
		EventDispatcher.getInstance().unRegEventHooker(eventType);
	}
	
	public static void blockEventHooker(EventType eventType, boolean blocked){
		
		EventDispatcher.getInstance().blockEventHooker(eventType, blocked);
	}
	
	public static EventProducer<?> getEventProducer(EventType eventType){
		
		EventHooker<?> hooker = EventDispatcher.getInstance().getEventHooker(eventType);
		try{
		if(null != hooker)
			return hooker.getProducer();
		else
			return null;
		}catch(RingEventException ree){
			
			return null;
		}
	}
}
