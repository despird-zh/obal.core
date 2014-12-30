package com.obal.disruptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.obal.exception.RingEventException;

/**
 * EventDisptcher is a singleton pattern object. It holds the necessary objects
 * needed by Disruptor.
 * 
 * @author despird
 * @version 0.1 2014-6-2
 * 
 **/
public class EventDispatcher {

	static Logger LOGGER = LoggerFactory.getLogger(EventDispatcher.class);

	private AtomicInteger hookerIdGenerator = new AtomicInteger(100); 
	
	/** the executor pool */
	private ExecutorService executor = null;

	/** the disruptor instance */
	private Disruptor<RingEvent> disruptor = null;

	/** the event handler */
	private RingEventHandler handler = new RingEventHandler();

	/** the event hooker list */
	private Map<EventType, EventHooker<?>> hookers = new HashMap<EventType, EventHooker<?>>();

	/** single instance */
	private static EventDispatcher instance;

	/**
	 * default event disptacher
	 **/
	private EventDispatcher() {
		setup();
	}

	/**
	 * Get the single instance of event dispatcher
	 * 
	 * @return the single instance
	 **/
	public static EventDispatcher getInstance() {

		if (null == instance)
			instance = new EventDispatcher();

		return instance;
	}

	/**
	 * Start the disruptor
	 **/
	public void start() {
		
		disruptor.start();
	}

	/**
	 * Shutdown the disruptor 
	 **/
	public void shutdown(){
		
		disruptor.shutdown();
		executor.shutdown();
	}
	
	/**
	 * Set up the disruptor
	 **/
	@SuppressWarnings("unchecked")
	private void setup() {
		// Executor that will be used to construct new threads for consumers
		this.executor = Executors.newCachedThreadPool();
		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024;
		EventFactory<RingEvent> auditbuilder = RingEvent.EVENT_FACTORY;
		// Construct the Disruptor
		disruptor = new Disruptor<RingEvent>(auditbuilder, bufferSize, executor);

		// Connect the handler
		disruptor.handleEventsWith(handler);
	}

	public EventHooker<?> getEventHooker(EventType eventType){
		
		return hookers.get(eventType);
	}
	/**
	 * dispatch event payload to respective hooker
	 **/
	private void onRingEvent(RingEvent ringevent, long sequence, boolean endOfBatch) {
		
		// After take payload, it is removed from ring event instance.
		EventType eventType = ringevent.getEventType();
		EventPayload payload = ringevent.takePayload();
		EventHooker<?> eventHooker = hookers.get(eventType);

		if (eventHooker != null && !eventHooker.isBlocked()) {

			try {

				eventHooker.processPayload(payload);

			} catch (RingEventException e) {

				LOGGER.error("Error when processing event[{}] payload", e, eventType);
			}

		}else{
			
			LOGGER.warn("EventHooker not exist or unmatch type:{}", eventType);
		}

	}

	/**
	 * publish event payload
	 **/
	public void sendPayload(EventPayload payload,EventType eventType){
		
		RingBuffer<RingEvent> ringBuffer = disruptor.getRingBuffer();
		long sequence = ringBuffer.next();  // Grab the next sequence
	    try
	    {
	    	RingEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
	        event.setEventType(eventType);
	    	event.setPayload(payload);  
	    }
	    finally
	    {
	        ringBuffer.publish(sequence);// for the sequence
	    }
	}
	
	/**
	 * Register a eventhooker
	 * 
	 * @param eventHooker the hooker of event 
	 **/
	public void regEventHooker(EventHooker<?> eventHooker) {

		if(null == eventHooker.getEventType()){
			
			EventType eventType = new EventType(hookerIdGenerator.incrementAndGet());
			eventHooker.setEventType(eventType);
		}
		hookers.put(eventHooker.getEventType(),eventHooker);
		eventHooker.setRingBuffer(disruptor.getRingBuffer());
	}
	
	/**
	 * Unregister the specified type of hooker 
	 **/
	public void unRegEventHooker(EventType eventType){
		
		EventHooker<?> eventHooker = hookers.remove(eventType);
		eventHooker.setRingBuffer(null);// clear reference to buffer.
		
	}
	
	/**
	 * Block the event hooker
	 * 
	 * @param type the type of hooker 
	 * @param blocked the flag of block or not 
	 **/
	public void blockEventHooker(EventType eventType,boolean blocked){
		
		EventHooker<?> eventHooker = hookers.get(eventType);
		if(null != eventHooker)
			eventHooker.setBlocked(blocked);
	}
	
	/**
	 * Class RingEventHandler to process event payload 
	 **/
	private static class RingEventHandler implements EventHandler<RingEvent> {

		@Override
		public void onEvent(RingEvent ringevent, long sequence,
				boolean endOfBatch) throws Exception {
			instance.onRingEvent(ringevent, sequence, endOfBatch);
		}

	}
}
