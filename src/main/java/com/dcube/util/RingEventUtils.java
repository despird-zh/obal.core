package com.dcube.util;

import com.dcube.disruptor.EventDispatcher;
import com.dcube.disruptor.EventHooker;
import com.dcube.disruptor.EventPayload;
import com.dcube.disruptor.EventProducer;
import com.dcube.disruptor.EventType;
import com.dcube.exception.RingEventException;

public class RingEventUtils {

	/**
	 * Publish the EventPayload without EventProducer
	 * @param payload the payload of event
	 * @param eventType the type of event
	 **/
	public static void sendPayload(EventPayload payload, EventType eventType){
		
		EventDispatcher.getInstance().sendPayload(payload,eventType);
	}
	
	/**
	 * Register the EventHooker, internally a map will keep it with eventType as key 
	 * @param eventHooker it provides EventType as map's entry key.
	 **/
	public static void regEventHooker(EventHooker<?> eventHooker){
		
		EventDispatcher.getInstance().regEventHooker(eventHooker);
	}
	
	/**
	 * Unregister the EventHooker of certain EventType
	 * @param eventType 
	 **/
	public static void unRegEventHooker(EventType eventType){
		
		EventDispatcher.getInstance().unRegEventHooker(eventType);
	}
	
	/**
	 * Block the event hooker processing of certain EventType 
	 * @param eventType the type of event
	 * @param blocked true:to be blocked; false: not to be blocked
	 **/
	public static void blockEventHooker(EventType eventType, boolean blocked){
		
		EventDispatcher.getInstance().blockEventHooker(eventType, blocked);
	}
	
	/**
	 * Get event producer of certain EventType, this is another way to send payload.
	 * 
	 * @param eventType
	 **/
	public static EventProducer<?> getEventProducer(EventType eventType){
		
		EventHooker<?> hooker = EventDispatcher.getInstance().getEventHooker(eventType);
		try{
		if(null != hooker)
			return hooker.getEventProducer();
		else
			return null;
		}catch(RingEventException ree){
			
			return null;
		}
	}
}
