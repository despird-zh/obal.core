package com.dcube.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.cache.CacheEntryPipe;
import com.dcube.core.AccessorFactory;
import com.dcube.core.accessor.IndexAccessor;
import com.dcube.core.security.Principal;
import com.dcube.disruptor.EventHooker;
import com.dcube.disruptor.EventPayload;
import com.dcube.disruptor.EventType;
import com.dcube.exception.BaseException;
import com.dcube.exception.RingEventException;
import com.dcube.util.AccessorUtils;

/**
 * IndexHooker will try to update the index information 
 **/
public class IndexHooker extends EventHooker<CacheEntryPipe>{

	IndexAccessor iaccr = null ;
	Logger LOGGER = LoggerFactory.getLogger(IndexHooker.class);
	public IndexHooker() {
		super(EventType.INDEX);
	}

	@Override
	public void processPayload(EventPayload payload) throws RingEventException {
		IndexPipe indexqueue = (IndexPipe)payload;
		Principal princ = new Principal("admin","demouser1","adminpwd","demosrc");
		try{
			while(!indexqueue.isEmpty()){
				IndexInfo indexinfo = indexqueue.poll();
				if(iaccr == null)
					iaccr = (IndexAccessor)AccessorFactory.buildIndexAccessor(princ, indexinfo.getEntityName());
				
				iaccr.doChangeEntryKey(indexinfo.getEntityAttr().getAttrName(), 
						indexinfo.oldValue, 
						indexinfo.newValue, 
						indexinfo.getEntryKey().getKey());
				
			}
		}catch(BaseException be){
			LOGGER.error("Fail to create index");
		}finally{
			
			AccessorUtils.closeAccessor(iaccr);
			iaccr = null;
		}
	}

}
