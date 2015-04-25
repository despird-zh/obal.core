package com.dcube.index;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.disruptor.EventDispatcher;
import com.dcube.disruptor.EventType;
import com.dcube.meta.EntityAttr;

public class IndexManager {

	static Logger LOGGER = LoggerFactory.getLogger(IndexManager.class);
	
	private static IndexManager instance ;
	
	private static Map<EntityAttr, IndexPipe> indexPipeMap = null;
	
	private IndexManager(){
		
		indexPipeMap = new HashMap<EntityAttr, IndexPipe>();
	}
	
	public static IndexManager getInstance(){
		return instance;
	}
	
	/**
	 * Add cacheInfo to queue
	 * 
	 * @param entryKey 
	 * @param data 
	 * 
	 * @return the queue contains cache info.
	 **/
	public void offerIndexInfoQueue(IndexInfo indexInfo){
		
		IndexPipe indexPipe = indexPipeMap.get(indexInfo.getEntityAttr());
		if(indexPipe == null){// create new one
			
			indexPipe = new IndexPipe(indexInfo.getEntityAttr());
			indexPipeMap.put(indexInfo.getEntityAttr(), indexPipe);
			indexPipe.offer(indexInfo);
			
			EventDispatcher.getInstance().sendPayload(indexPipe,EventType.CACHE);
			
		}else if(indexPipe.isEmpty()){// since empty let cache hooker to drop it.
			
			indexPipe = new IndexPipe(indexInfo.getEntityAttr());
			indexPipeMap.put(indexInfo.getEntityAttr(), indexPipe);
			indexPipe.offer(indexInfo);
			
			EventDispatcher.getInstance().sendPayload(indexPipe,EventType.CACHE);
			
		}else{// not empty push data to existed queue, let hook digest it.
			
			indexPipe.offer(indexInfo);
		}
	}
	
	/**
	 * Drop CacheInfo queue from queue map. This to be called in CacheHooker to 
	 * clear cache queue.
	 * 
	 * @param entryPipe the queue to be removed
	 * 
	 * @return true: cache queue not exist in map; false: cache queue exist in map
	 **/
	public boolean dropIndexInfoQueue(IndexPipe indexPipe){
		
		IndexPipe newIndexPipe = indexPipeMap.get(indexPipe.getEntityAttr());
		
		if(indexPipe.isEmpty() && indexPipe.equals(newIndexPipe)){
			
			indexPipeMap.remove(newIndexPipe.getEntityAttr());
			return true;
			
		}else if(indexPipe.isEmpty() && !indexPipe.equals(newIndexPipe)){
			
			return true;
		}else{
			// not empty
			return false;
		}
	}
}
