package com.dcube.core;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.admin.EntityAdmin;
import com.dcube.audit.AuditHooker;
import com.dcube.cache.CacheHooker;
import com.dcube.cache.CacheManager;
import com.dcube.core.ILifecycle.State;
import com.dcube.core.accessor.EntryInfo;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityManager;

/**
 * CoreLauncher is the core start entrance.
 * 
 * @author despird-zh
 * @version 0.1 2015-2-1
 * 
 **/
public class CoreLauncher{

	private static CoreDelegator coreDelegator;
	
	// auto fire CoreDelegator initialization.
	static{
		new CoreLauncher();
	}
	
	/**
	 * Hide the default constructor 
	 **/
	private CoreLauncher(){
		
		coreDelegator = new CoreDelegator();
	}
	
	/**
	 * Initial the core, during initial phase necessary resource and classes are loaded
	 * so some LifecycleListener will be registered in CoreDelegator instance.
	 **/
	public static void initial() throws BaseException{
		
		coreDelegator.initial();
	}
	
	/**
	 * Start core and fire ILifecycle.State.START
	 **/
	public static void start() throws BaseException{
		
		coreDelegator.start();
	}
	
	/**
	 * Stop core and fire ILifecycle.State.STOP
	 **/
	public static void stop()throws BaseException{
		
		coreDelegator.stop();
	}
	
	/**
	 * State of core 
	 **/
	public static State state() {
		
		return coreDelegator.state;
	}

	/**
	 * Register the life cycle listener
	 * @param listener  
	 **/
	public static void regListener(LifecycleListener listener) {
		
		coreDelegator.regListener(listener);
	}
	
	/**
	 * Unregister the life cycle listener
	 * @param listener  
	 **/
	public static void unregListener(LifecycleListener listener) {

		coreDelegator.unregListener(listener);
	}

	/**
	 * Clear the life cycle listeners
	 **/
	public static void clearListener() {

		coreDelegator.clearListener();
	}
	
	/**
	 * Delegate class to implements the ILifecycle method support. 
	 **/
	private static class CoreDelegator implements ILifecycle{
		
		static Logger LOGGER = LoggerFactory.getLogger(CoreLauncher.class);
				
		private State state = State.UNKNOWN;
		
		private EventDispatcher eventDispatcher = null;
		private EntityAdmin entityAdmin = null;
		
		private ReentrantLock lock = new ReentrantLock(); // lock
		private ArrayList<LifecycleListener> listeners = new ArrayList<LifecycleListener>();
		
		public CoreDelegator(){
			
			setup();
		}
		
		private void setup(){
			// initial the event dispatcher
			this.eventDispatcher = EventDispatcher.getInstance();
			// prepare the meta infor & attr data
			EntityManager.getInstance();
			// initial the admin instance
			this.entityAdmin = EntityAdmin.getInstance();		
			// initial the cache manager
			CacheManager.getInstance();
		}
		
		@Override
		public void initial() throws BaseException{
			
			dispatchEvent(State.BEFORE_INIT);
			// build accessor builder and detect all the accessor classes
			AccessorFactory.getDefaultBuilder();
			
			// register audit event hooker
			AuditHooker auditHooker = new AuditHooker();
			this.eventDispatcher.regEventHooker(auditHooker);
			
			// register cache event hooker
			CacheHooker<?> cacheHooker = new CacheHooker<EntryInfo>();
			this.eventDispatcher.regEventHooker(cacheHooker);
			
			// load the entity meta
			this.entityAdmin.loadEntityMeta();
			
			dispatchEvent(State.AFTER_INIT);
			this.state = State.INIT;
		}
		
		@Override
		public void start() throws BaseException{
			
			dispatchEvent(State.BEFORE_START);
			this.state = State.START;
			this.eventDispatcher.start();
			this.state = State.RUNNING;
			dispatchEvent(State.AFTER_START);
		}
		
		@Override
		public void stop()throws BaseException{
			
			dispatchEvent(State.BEFORE_STOP);
			this.eventDispatcher.shutdown();
			dispatchEvent(State.AFTER_STOP);
			this.state = State.STOP;
		}
		
		@Override
		public State state() {
			
			return this.state;
		}

		@Override
		public void regListener(LifecycleListener listener) {
			
			lock.lock();
			int count = listeners.size()-1;
			while(count >=0){
				
				if( listeners.get(count).priority() == listener.priority() )
					listeners.add(count, listener);
				
				else if( listeners.get(count).priority() > listener.priority() )
					listeners.add( listener);
				
				else if(count == 0){
					listeners.add(count, listener);
				}
				
				count--;
			}
			lock.unlock();
		}

		@Override
		public void unregListener(LifecycleListener listener) {
			lock.lock();
			listeners.remove(listener);
			lock.unlock();
		}

		@Override
		public void clearListener() {
			lock.lock();
			listeners.clear();
			lock.unlock();
		}

		/**
		 * Send event to different listener. 
		 **/
		private void dispatchEvent(State state){
			lock.lock();
			int count = this.listeners.size();
			for(int i = 0 ; i < count ; i++){
				
				listeners.get(i).onEvent(state);
			}
			lock.unlock();
		}
	}
}
