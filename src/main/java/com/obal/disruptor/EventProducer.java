package com.obal.disruptor;

import com.lmax.disruptor.RingBuffer;

public class EventProducer<T extends EventPayload> {
	
	RingBuffer<RingEvent> ringBuffer = null;
	EventType eventType = null;
	
	public EventProducer(RingBuffer<RingEvent> ringBuffer,EventType eventType){
		this.eventType = eventType;
		this.ringBuffer = ringBuffer;
	}
	
	public void setRingBuffer(RingBuffer<RingEvent> ringBuffer){
		
		this.ringBuffer = ringBuffer;
	}
	
	public void setEventType(EventType eventType){
		
		this.eventType = eventType;
	}
	
	public void produce(T payload)
    {
        long sequence = ringBuffer.next();  // Grab the next sequence
        try
        {
            RingEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
           	event.setEventType(eventType);                                 // for the sequence
            event.setPayload(payload);  // Fill with data
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
    }
}
