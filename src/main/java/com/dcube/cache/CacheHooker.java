package com.dcube.cache;

import com.dcube.core.AccessorFactory;
import com.dcube.core.CoreConstants;
import com.dcube.core.IEntityAccessor;
import com.dcube.core.accessor.EntryInfo;
import com.dcube.core.security.Principal;
import com.dcube.disruptor.EventHooker;
import com.dcube.disruptor.EventPayload;
import com.dcube.disruptor.EventType;
import com.dcube.exception.AccessorException;
import com.dcube.exception.EntityException;
import com.dcube.exception.RingEventException;
import com.dcube.util.AccessorUtils;

public class CacheHooker<K extends EntryInfo>  extends EventHooker<CacheInfo>{

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
				AccessorFactory.buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						cacheData.getEntryKey().getEntityName());	
				

			eaccessor.doPutEntry(cacheData);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			AccessorUtils.closeAccessor(eaccessor);
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
				AccessorFactory.buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						entity);	
				

			eaccessor.doPutEntryAttr(key, attrName, value);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			
			AccessorUtils.closeAccessor(eaccessor);
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
				AccessorFactory.buildEntityAccessor(CoreConstants.BUILDER_REDIS, 
						principal, 
						entityName);	
				
			eaccessor.doDelEntry(keys);
			
		} catch (AccessorException e) {
			
			e.printStackTrace();
		} catch (EntityException e) {
			
			e.printStackTrace();
		}finally{
			
			AccessorUtils.closeAccessor(eaccessor);
		}

	}
}
