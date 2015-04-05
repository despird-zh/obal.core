package com.dcube.accessor.redis;

import com.dcube.core.AccessorFactory;
import com.dcube.core.CoreConstants;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.redis.RAccessorBuilder;
import com.dcube.core.redis.REntityAccessor;

public class RCacheAccessor extends REntityAccessor<EntityEntry>{
	
	/**
	 * Default constructor 
	 **/
	public RCacheAccessor() {
		super(CoreConstants.CACHE_ACCESSOR,null);
	}
	
	/**
	 * Constructor with context 
	 **/
	public RCacheAccessor(AccessorContext context) {
		super(CoreConstants.CACHE_ACCESSOR,context);
	}


	/**
	 * get entry wrapper
	 * @return wrapper object 
	 **/
	public EntityEntry newEntityEntryObject(){
		
		return new EntityEntry();
	}
	
	/**
	 * Not call context.clear, make sure not affect the normal Hbase operation. 
	 **/
	@Override
	public void close(){
		try {
			// embed means share connection, close it directly affect other accessors using this conn.
			if (getJedis() != null && !isEmbed()){
				
				RAccessorBuilder builder = (RAccessorBuilder)AccessorFactory.getAccessorBuilder(CoreConstants.BUILDER_REDIS);
				builder.returnJedis(getJedis());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
