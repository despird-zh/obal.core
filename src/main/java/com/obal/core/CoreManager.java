package com.obal.core;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.admin.EntityAdmin;
import com.obal.audit.AuditHooker;
import com.obal.cache.CacheHooker;
import com.obal.cache.CacheManager;
import com.obal.disruptor.EventDispatcher;
import com.obal.exception.BaseException;
import com.obal.meta.EntityManager;

public class CoreManager implements ILifecycle{

	static Logger LOGGER = LoggerFactory.getLogger(CoreManager.class);
	
	private static CoreManager coreInstance;
	
	private State state = State.UNKNOWN;
	
	private EventDispatcher eventDispatcher = null;
	private EntityManager entityManager = null;
	private EntityAdmin entityAdmin = null;
	private CacheManager cacheManager = null;
	
	private ArrayList<LifecycleListener> listeners = new ArrayList<LifecycleListener>();
	
	private CoreManager(){
		
		setup();
	}
	
	private void setup(){
		// initial the eventdispatcher
		this.eventDispatcher = EventDispatcher.getInstance();
		// prepare the meta infor & attr data
		this.entityManager = EntityManager.getInstance();
		// initial the admin instance
		this.entityAdmin = EntityAdmin.getInstance();		
		// initial the cache manager
		this.cacheManager = CacheManager.getInstance();
	}
	
	public void initial() throws BaseException{
		
		if(null == coreInstance)
			coreInstance = new CoreManager();
		
		AuditHooker auditHooker = new AuditHooker();
		coreInstance.eventDispatcher.regEventHooker(auditHooker);
		
		CacheHooker<?> cacheHooker = new CacheHooker<EntryKey>();
		coreInstance.eventDispatcher.regEventHooker(cacheHooker);
		
		coreInstance.entityAdmin.loadEntityMeta();		
		
		coreInstance.state = State.INIT;
	}
	
	public void start() throws BaseException{
		
		coreInstance.eventDispatcher.start();
		coreInstance.state = State.RUNNING;
	}
	
	public void stop()throws BaseException{
		
		coreInstance.eventDispatcher.shutdown();
		coreInstance.state = State.STOP;
	}
	
	public State state() {
		
		return coreInstance.state;
	}

	@Override
	public void regListener(LifecycleListener listener) {

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
	}

	@Override
	public void unregListener(LifecycleListener listener) {
		
		listeners.remove(listener);
	}

	@Override
	public void clearListener() {
		listeners.clear();
	}

}
