package com.dcube.disruptor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.exception.RingEventException;
import com.lmax.disruptor.RingBuffer;

/**
 * Basic class for certain type event handler, it holds RingBuffer to
 * provide event producer that to be used for event publishing. 
 *  
 **/
public abstract class EventHooker<T extends EventPayload> {
	/** the event blocked or not */
	private boolean blocked = false; 
	/** the event type */
	private EventType eventType;
	/** the ring buffer */
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
	
	/**
	 * Set event type
	 * @param eventType 
	 **/
	public void setEventType(EventType eventType){
		
		this.eventType = eventType;
	}
	
	/**
	 * Set the RingBuffer 
	 * @param ringBuffer 
	 **/
	protected void setRingBuffer(RingBuffer<RingEvent> ringBuffer){
		
		this.ringBuffer = ringBuffer;		
	}
	
	/**
	 * Get the event producer
	 * @return EventProducer<T> 
	 **/
	public EventProducer<T> getEventProducer() throws RingEventException{
		
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
