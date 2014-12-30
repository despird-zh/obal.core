package com.obal.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.admin.EntityAdmin;
import com.obal.audit.AuditHooker;
import com.obal.cache.CacheHooker;
import com.obal.cache.CacheManager;
import com.obal.disruptor.EventDispatcher;
import com.obal.exception.BaseException;
import com.obal.meta.EntityManager;

public class CoreManager {

	static Logger LOGGER = LoggerFactory.getLogger(CoreManager.class);
	
	private static CoreManager coreInstance;
	
	private int state = -1;
	
	public static final int READY = 101;	
	public static final int STARTING = 102;
	public static final int STARTED = 103;
	public static final int STOPED = 104;
	
	private EventDispatcher eventDispatcher = null;
	private EntityManager entityManager = null;
	private EntityAdmin entityAdmin = null;
	private CacheManager cacheManager = null;
	
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
	
	public static void initial() throws BaseException{
		
		if(null == coreInstance)
			coreInstance = new CoreManager();
		
		AuditHooker auditHooker = new AuditHooker();
		coreInstance.eventDispatcher.regEventHooker(auditHooker);
		
		CacheHooker<?> cacheHooker = new CacheHooker<EntryKey>();
		coreInstance.eventDispatcher.regEventHooker(cacheHooker);
		
		coreInstance.entityAdmin.loadEntityMeta();		
		
		coreInstance.state = READY;
	}
	
	public static void start() throws BaseException{
		
		coreInstance.eventDispatcher.start();
		coreInstance.state = STARTED;
	}
	
	public static void stop()throws BaseException{
		
		coreInstance.eventDispatcher.shutdown();
		coreInstance.state = STOPED;
	}
	
	public static int state() throws BaseException{
		
		initial();
		return coreInstance.state;
	}
}
