package com.obal.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * RingEvent class is used to hold payload
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public class RingEvent {
	
	private EventType eventType;
	
	private EventPayload payload;
	
	public void setEventType(EventType eventType){
		
		this.eventType = eventType;
	}
	/**
	 * The payload of event 
	 **/
	public EventPayload getPayload() {
		return payload;
	}
	
	/**
	 * Fetch the payload and clean RingEvent payload. 
	 **/
	public EventPayload takePayload(){
		
		EventPayload rtv = this.payload;
		this.payload = null;
		this.eventType = EventType.UNKNOWN;
		return rtv;
	}
	/**
	 * the event type 
	 **/
	public EventType getEventType(){
		
		return this.eventType;
	}
	
	/**
	 * Set payload 
	 * 
	 *  @param payload the payload to be passed to event hooker
	 **/
	public void setPayload(EventPayload payload) {
		this.payload = payload;
	}	

	/**
	 * the factory to be used by disruptor to preallocate elements of ringbuffer. 
	 **/
	public final static EventFactory<RingEvent> EVENT_FACTORY = new EventFactory<RingEvent> (){
		
		@Override
		public RingEvent newInstance() {

			return new RingEvent();
		}
	};
}
