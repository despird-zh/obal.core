package com.dcube.launcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.admin.EntityAdmin;
import com.dcube.audit.AuditHooker;
import com.dcube.cache.CacheHooker;
import com.dcube.cache.CacheManager;
import com.dcube.core.AccessorFactory;
import com.dcube.core.accessor.EntryInfo;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.exception.BaseException;
import com.dcube.launcher.ILifecycle.LifeState;
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
		
		if(coreDelegator == null) 
			coreDelegator = new CoreDelegator();
		
        ServiceLoader<CoreInitializer> svcloader = ServiceLoader
                .load(CoreInitializer.class, ClassLoader.getSystemClassLoader());
        
        for (CoreInitializer initializer : svcloader) {
        	
        	coreDelegator.LOGGER.info("Initializer:{} is loaded.",initializer.hookerName);
        }
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
	public static LifeState state() {
		
		return coreDelegator.state;
	}

	/**
	 * Register the life cycle listener
	 * @param listener  
	 **/
	public static void regHooker(LifecycleHooker hooker) {
		
		coreDelegator.regHooker(hooker);
	}
	
	/**
	 * Unregister the life cycle listener
	 * @param listener  
	 **/
	public static void unregHooker(LifecycleHooker hooker) {

		coreDelegator.unregHooker(hooker);
	}

	/**
	 * Clear the life cycle listeners
	 **/
	public static void clearListener() {

		coreDelegator.clearHooker();
	}
	
	/**
	 * Delegate class to implements the ILifecycle method support. 
	 **/
	private static class CoreDelegator implements ILifecycle{
		
		@SuppressWarnings("unused")
		static Logger LOGGER = LoggerFactory.getLogger(CoreLauncher.class);
		// current state
		private LifeState state = LifeState.UNKNOWN;
		// event dispatcher
		private EventDispatcher eventDispatcher = null;
		// entity admin
		private EntityAdmin entityAdmin = null;
		// entrant lock
		private ReentrantLock lock = new ReentrantLock(); // lock
		// hooker list
		private ArrayList<LifecycleHooker> hookers = new ArrayList<LifecycleHooker>();
		// message list
		private List<LifeCycleMessage> messageList = new ArrayList<LifeCycleMessage>();
		
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
			
			dispatchEvent(LifeState.BEFORE_INIT);
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
			
			dispatchEvent(LifeState.AFTER_INIT);
			this.state = LifeState.INIT;
		}
		
		@Override
		public void start() throws BaseException{
			
			dispatchEvent(LifeState.BEFORE_START);
			this.state = LifeState.START;
			this.eventDispatcher.start();
			this.state = LifeState.RUNNING;
			dispatchEvent(LifeState.AFTER_START);
		}
		
		@Override
		public void stop()throws BaseException{
			
			dispatchEvent(LifeState.BEFORE_STOP);
			this.eventDispatcher.shutdown();
			dispatchEvent(LifeState.AFTER_STOP);
			this.state = LifeState.STOP;
		}
		
		@Override
		public LifeState state() {
			
			return this.state;
		}

		@Override
		public void regHooker(LifecycleHooker hooker) {
			
			lock.lock();
			int count = hookers.size()-1;
			if(count < 0){
				hookers.add( hooker);
				hooker.setLauncher(this);
			}else{
				while(count >=0){
					
					if( hookers.get(count).priority() == hooker.priority() )
						hookers.add(count, hooker);
					
					else if( hookers.get(count).priority() > hooker.priority() )
						hookers.add( hooker);
					
					else if(count == 0){
						hookers.add(count, hooker);
					}
					
					count--;
				}
				hooker.setLauncher(this);
			}
			lock.unlock();
		}

		@Override
		public void unregHooker(LifecycleHooker listener) {
			lock.lock();
			hookers.remove(listener);
			lock.unlock();
		}

		@Override
		public void clearHooker() {
			lock.lock();
			hookers.clear();
			lock.unlock();
		}

		/**
		 * Send event to different listener. 
		 **/
		private void dispatchEvent(LifeState state){
			lock.lock();
			int count = this.hookers.size();
			for(int i = 0 ; i < count ; i++){
				
				hookers.get(i).onEvent(state);
			}
			lock.unlock();
		}

		@Override
		public void receiveFeedback(String hookerName, boolean errorFlag,
				Date time, String message) {
			LifeCycleMessage msg = new LifeCycleMessage(time, errorFlag, message);
			messageList.add(msg);
		}
		
		//////====== Inner Message class ====/////
		protected class LifeCycleMessage{
			Date time = null;
			String message = null;
			boolean errorFlag = false;
			public LifeCycleMessage(Date time,boolean errorFlag, String message){
				this.time = time;
				this.errorFlag = errorFlag;
				this.message = message;
			}
		}
	}
	
	
}
