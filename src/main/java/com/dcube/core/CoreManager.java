package com.dcube.core;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.admin.EntityAdmin;
import com.dcube.audit.AuditHooker;
import com.dcube.cache.CacheHooker;
import com.dcube.cache.CacheManager;
import com.dcube.core.accessor.EntryInfo;
import com.dcube.disruptor.EventDispatcher;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityManager;

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
	
	public static CoreManager getInstance(){
		
		if(coreInstance == null)
			coreInstance = new CoreManager();
		
		return coreInstance;
	}
	
	private void setup(){
		// initial the event dispatcher
		this.eventDispatcher = EventDispatcher.getInstance();
		// prepare the meta infor & attr data
		this.entityManager = EntityManager.getInstance();
		// initial the admin instance
		this.entityAdmin = EntityAdmin.getInstance();		
		// initial the cache manager
		this.cacheManager = CacheManager.getInstance();
	}
	
	@Override
	public void initial() throws BaseException{
		
		dispatchEvent(State.BEFORE_INIT);
		AuditHooker auditHooker = new AuditHooker();
		this.eventDispatcher.regEventHooker(auditHooker);
		
		CacheHooker<?> cacheHooker = new CacheHooker<EntryInfo>();
		this.eventDispatcher.regEventHooker(cacheHooker);
		
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

	private void dispatchEvent(State state){
		
		int count = this.listeners.size();
		for(int i = 0 ; i < count ; i++){
			
			listeners.get(i).onEvent(state);
		}
		
	}
}
