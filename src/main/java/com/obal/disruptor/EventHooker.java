package com.obal.disruptor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.lmax.disruptor.RingBuffer;
import com.obal.exception.RingEventException;

public abstract class EventHooker<T extends EventPayload> {

	private boolean blocked = false; 
	
	private EventType eventType;
	
	private RingBuffer<RingEvent> ringBuffer = null;
	
	/**
	 * Constructor:specify the eventype supported 
	 **/
	public EventHooker(EventType eventType){
		
		this.eventType = eventType;
	}
	
	/**
	 * The hooker is blocked or not, if hooker is blocked it ignore any event. 
	 **/
	public boolean isBlocked(){
		
		return this.blocked;
	}
	
	/**
	 * Set the block switch flag
	 * 
	 * @param blocked true:Hooker won't process payload; false:Hooker process payload. 
	 **/
	public void setBlocked(boolean blocked){
		
		this.blocked = blocked;
	}
	
	/**
	 * Get the supported EventType 
	 **/
	public EventType getEventType(){
		
		return eventType;
	}
	
	public void setEventType(EventType eventType){
		
		this.eventType = eventType;
	}
	
	protected void setRingBuffer(RingBuffer<RingEvent> ringBuffer){
		
		this.ringBuffer = ringBuffer;		
	}
	
	public EventProducer<T> getProducer() throws RingEventException{
		
		if(null == ringBuffer)
			throw new RingEventException("The RingBuffer not initialized yet.");
		
		return new EventProducer<T>(ringBuffer, eventType);
	}
	
	/**
	 * Process the payload of event.
	 * 
	 * @param  payload the payload of ring event
	 * @exception RingEventException
	 * 
	 **/
	public abstract void processPayload(EventPayload payload) throws RingEventException;
	
	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof EventHooker)) {
			return false;
		}
		// step 3
		EventHooker<?> that = (EventHooker<?>) other;
		// step 4
		return new EqualsBuilder()
			.append(this.eventType, that.eventType).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.eventType)
				.toHashCode();
	}

}
