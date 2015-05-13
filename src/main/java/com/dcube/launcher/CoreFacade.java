package com.dcube.launcher;

import java.util.Date;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.audit.AuditHooker;
import com.dcube.cache.CacheHooker;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.exception.BaseException;
import com.dcube.index.IndexHooker;
import com.dcube.launcher.Lifecycle.LifeState;

/**
 * CoreLauncher is the core start entrance.
 * 
 * @author despird
 * @version 0.1 2014-12-1
 * 
 **/
public class CoreFacade{

	private static CoreDelegator coreDelegator;
	
	// auto fire CoreDelegator initialization.
	static{
		new CoreFacade();
	}
	
	/**
	 * Hide the default constructor 
	 **/
	private CoreFacade(){
		
		if(coreDelegator == null) 
			coreDelegator = new CoreDelegator();
		
		coreDelegator.setup();
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
	public static void startup() throws BaseException{
		
		coreDelegator.startup();
	}
	
	/**
	 * Stop core and fire ILifecycle.State.STOP
	 **/
	public static void shutdown()throws BaseException{
		
		coreDelegator.shutdown();
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
	public static void regLifecycleHooker(LifecycleHooker hooker) {
		
		coreDelegator.regLifecycleHooker(hooker);
	}
	
	/**
	 * Unregister the life cycle listener
	 * @param listener  
	 **/
	public static void unregLifecycleHooker(LifecycleHooker hooker) {

		coreDelegator.unregLifecycleHooker(hooker);
	}

	/**
	 * Clear the life cycle listeners
	 **/
	public static void clearListener() {

		coreDelegator.clearLifecycleHooker();
	}
	
	/**
	 * Delegate class to implements the ILifecycle method support. 
	 **/
	private static class CoreDelegator extends Lifecycle{
		
		static Logger LOGGER = LoggerFactory.getLogger(CoreFacade.class);
		
		/**
		 * Default constructor 
		 **/
		public CoreDelegator(){	}
		
		/**
		 * Trigger the CoreInitializers to setup LifecycleHooker
		 **/
		public void setup(){
			
	        ServiceLoader<CoreInitializer> svcloader = ServiceLoader
	                .load(CoreInitializer.class, ClassLoader.getSystemClassLoader());
	        
	        for (CoreInitializer initializer : svcloader) {
	        	
	        	LOGGER.info("Initializer:{} is loaded.",initializer.hookerName);
	        	regLifecycleHooker(initializer.getLifecycleHooker());
	        }
		}
		
		@Override
		public void initial() throws BaseException{
			
			fireEvent(LifeState.INITIAL);
			
			// register audit event hooker
			AuditHooker auditHooker = new AuditHooker();
			EventDispatcher.getInstance().regEventHooker(auditHooker);
			
			// register cache event hooker
			CacheHooker<? extends EntityEntry> cacheHooker = new CacheHooker<>();
			EventDispatcher.getInstance().regEventHooker(cacheHooker);
			
			// register index event hooker
			IndexHooker indexHooker = new IndexHooker();
			EventDispatcher.getInstance().regEventHooker(indexHooker);
			this.state = LifeState.INITIAL;
		}
		
		@Override
		public void startup() throws BaseException{
			
			state = LifeState.STARTUP;
			fireEvent(LifeState.STARTUP);
			this.state = LifeState.RUNNING;
		}
		
		@Override
		public void shutdown()throws BaseException{
			
			fireEvent(LifeState.SHUTDOWN);
			state = LifeState.SHUTDOWN;
		}
			
	}
	
	/** Inner Message class */
	protected static class LifeCycleMessage {
		Date time = null;
		String message = null;
		boolean errorFlag = false;

		public LifeCycleMessage(Date time, boolean errorFlag, String message) {
			this.time = time;
			this.errorFlag = errorFlag;
			this.message = message;
		}
	}
}
