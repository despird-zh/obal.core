package com.dcube.accessor.redis;

import com.dcube.core.CoreConstants.ConfigEnum;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.redis.REntityAccessor;

public class RCacheAccessor extends REntityAccessor<EntityEntry>{
	
	/**
	 * Default constructor 
	 **/
	public RCacheAccessor() {
		super(ConfigEnum.CacheAccessor.value,null);
	}
	
	/**
	 * Constructor with context 
	 **/
	public RCacheAccessor(AccessorContext context) {
		super(ConfigEnum.CacheAccessor.value,context);
	}


	/**
	 * get entry wrapper
	 * @return wrapper object 
	 **/
	@Override
	public EntityEntry newEntryObject(){
		
		return new EntityEntry();
	}
	
}
