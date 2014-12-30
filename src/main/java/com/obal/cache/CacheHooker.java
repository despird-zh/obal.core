package com.obal.cache;

import com.obal.core.AccessorFactory;
import com.obal.core.CoreConstants;
import com.obal.core.EntryKey;
import com.obal.core.IEntityAccessor;
import com.obal.core.security.Principal;
import com.obal.disruptor.EventHooker;
import com.obal.disruptor.EventPayload;
import com.obal.disruptor.EventType;
import com.obal.exception.AccessorException;
import com.obal.exception.EntityException;
import com.obal.exception.RingEventException;

public class CacheHooker<K extends EntryKey>  extends EventHooker<CacheInfo>{

	public CacheHooker() {
		super(EventType.CACHE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processPayload(EventPayload payload) throws RingEventException {
		
		CacheInfo operData = (CacheInfo)payload;
		 
		if(CacheInfo.OP_PUT.equals(operData.operation(null))){
			
			CacheInfo.PutEntryData data = operData.value();
			doCachePut((K)data.entryInfo);
		}
		else if(CacheInfo.OP_PUT_ATTR.equals(operData.operation(null))){
			
			CacheInfo.PutAttrData data = operData.value();
			doCachePutAttr(data.key,data.entity,data.attr,data.value);
		}
		else if(CacheInfo.OP_DEL.equals(operData.operation(null))){
			
			CacheInfo.DelEntryData data = operData.value();
			doCacheDel(data.entity,data.keys);
		}
		
	}

	/**
	 * Put the entry data into cache
	 * 
	 * @param entry the entry data
	 **/
	public void doCachePut(K cacheData){
		
		Principal principal = null;		
		IEntityAccessor<K> eaccessor = null;
		try {
			eaccessor = 
				AccessorFactory.getInstance().buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						cacheData.getEntityName());	
				

			eaccessor.doPutEntry(cacheData);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			
			eaccessor.release();
		}
	}

	/**
	 * Put the entry attribute data into cache
	 * 
	 * @param key the entry key
	 * @param entity the entity name
	 * @param attrName the attribute name
	 * @param value the attribute value
	 * 
	 **/
	public void doCachePutAttr(String key, String entity, String attrName, Object value) {
		Principal principal = null;		
		IEntityAccessor<K> eaccessor = null;
		try {
			eaccessor = 
				AccessorFactory.getInstance().buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						entity);	
				

			eaccessor.doPutEntryAttr(key, attrName, value);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			
			eaccessor.release();
		}
	}
	
	/**
	 * Delete the entry from cache
	 * 
	 * @param entityName the entity name
	 * @param keys the array of key
	 **/
	public void doCacheDel(String entityName, String... keys) {
		Principal principal = null;		
		IEntityAccessor<K> eaccessor = null;
		try {
			eaccessor = 
				AccessorFactory.getInstance().buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						entityName);	
				
			eaccessor.doDelEntry(keys);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			
			eaccessor.release();
		}

	}
}
