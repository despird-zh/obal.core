package com.dcube.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntityAccessor;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.disruptor.EventHooker;
import com.dcube.disruptor.EventPayload;
import com.dcube.disruptor.EventType;
import com.dcube.exception.AccessorException;
import com.dcube.exception.RingEventException;
import com.dcube.util.AccessorUtils;

public class CacheHooker<K extends EntityEntry>  extends EventHooker<CacheEntryPipe>{

	static Logger LOGGER = LoggerFactory.getLogger(CacheHooker.class);
	
	public CacheHooker() {
		super(EventType.CACHE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processPayload(EventPayload payload) throws RingEventException {
		
		CacheEntryPipe cachePipe = (CacheEntryPipe)payload;
		
		EntryKey key = cachePipe.getEntryKey();
		IEntityAccessor<K> eaccessor = null;
		while(!cachePipe.isEmpty()){// start of loop
			
			CacheInfo operData = cachePipe.peek();
			try{
				eaccessor = AccessorFactory.buildCacheAccessor(operData.getPrincipal(), key.getEntityName());
			
				if(CacheInfo.OperEnum.PutEntry == operData.operation()){
				
					CacheInfo.PutEntryData data = operData.value();
					eaccessor.doPutEntry((K)data.entryInfo,true);
					
				}
				else if(CacheInfo.OperEnum.PutAttr == operData.operation()){
					
					CacheInfo.PutAttrData data = operData.value();
					eaccessor.doPutEntryAttr(data.key,data.attr,data.value);
					
				}
				else if(CacheInfo.OperEnum.DelEntry == operData.operation()){
					
					CacheInfo.DelEntryData data = operData.value();
					eaccessor.doRemoveEntry(data.key);
					
				}
				else if(CacheInfo.OperEnum.DelAttr == operData.operation()){
					
					CacheInfo.DelAttrData data = operData.value();
					eaccessor.doRemoveEntryAttr(data.attr, data.key);
					
				}
				
				cachePipe.poll();// remove from queue
				
			}catch (AccessorException e) {
				
				LOGGER.error("Error when put data to cache:{}","",e);
				
			} finally{
				AccessorUtils.closeAccessor(eaccessor);
			}
		}
		// remove from cache manager
		CacheManager.getInstance().dropCacheQueue(cachePipe);
	}
}
