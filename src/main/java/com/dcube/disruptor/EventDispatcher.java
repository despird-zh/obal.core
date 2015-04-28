package com.dcube.disruptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.exception.RingEventException;
import com.dcube.launcher.LifecycleHooker;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

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
	/** the Disruptor instance */
	private Disruptor<RingEvent> disruptor = null;
	/** the event handler */
	private RingEventHandler handler = new RingEventHandler();
	/** the event hooker list */
	private Map<EventType, EventHooker<?>> hookers = new HashMap<EventType, EventHooker<?>>();
	/** single instance */
	private static EventDispatcher instance;
	/** the lifecycle hooker */
	private LifecycleHooker hooker = null;
	/** running flag */
	private boolean running = false;
	/**
	 * default event disptacher
	 **/
	private EventDispatcher() {
		
		hooker = new LifecycleHooker("EventDispatcher", 0){

			@Override
			public void initial() {
				
				instance.initial();
				sendFeedback(false, "EventDispatcher initial done");
			}

			@Override
			public void startup() {
				instance.startup();
				sendFeedback(false, "EventDispatcher startup done");
			}

			@Override
			public void shutdown() {
				instance.shutdown();
				sendFeedback(false, "EventDispatcher shutdown done");
			}

		};
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
	 * Check if the disruptor is running 
	 **/
	public boolean isRunning(){
		
		return this.running;
	}
	
	/**
	 * Start the disruptor
	 **/
	public void startup() {
		
		disruptor.start();
		this.running = true;
	}

	/**
	 * Shutdown the disruptor 
	 **/
	public void shutdown(){
		
		disruptor.shutdown();
		executor.shutdown();
		this.running = false;
	}
	
	/**
	 * Get the LifecycleHooker instance, it make EventDispatcher to 
	 * act according to LifecycleEvent.
	 * 
	 *  @return LifecycleHooker
	 **/
	public LifecycleHooker getLifecycleHooker(){
		return this.hooker;
	}
	
	/**
	 * Set up the disruptor
	 **/
	@SuppressWarnings("unchecked")
	private void initial() {
		// Executor that will be used to construct new threads for consumers
		this.executor = Executors.newCachedThreadPool();
		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024;
		EventFactory<RingEvent> eventbuilder = RingEvent.EVENT_FACTORY;
		// create new Disruptor instance
		disruptor = new Disruptor<RingEvent>(eventbuilder, bufferSize, executor);
		// Connect the handler
		disruptor.handleEventsWith(handler);

	}

	/**
	 * Get EventHooker instance according to EnentType object. this event hooker
	 * provides the event producer to send payload data.
	 * 
	 * @return EventHooker 
	 **/
	public EventHooker<?> getEventHooker(EventType eventType){
		
		return hookers.get(eventType);
	}
	
	/**
	 * dispatch event payload to respective hooker
	 * @param ringevent the event
	 * @param sequece
	 * @param endofBatch
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
	 * Publish event EventPayload to specified EventType
	 * 
	 * @param payload the payload of specified event
	 * @param eventType the type of specified event
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
	 * Register an event hooker
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
	 * Unregister the specified type of event hooker 
	 * 
	 * @param eventType
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
