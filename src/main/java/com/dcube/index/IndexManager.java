package com.dcube.index;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.disruptor.EventDispatcher;
import com.dcube.disruptor.EventType;
import com.dcube.meta.EntityAttr;

/**
 * IndexManager is a singleton pattern instance, it provides methods to build the index data for 
 * All indexable entity attributes.
 * 
 * @author desprid
 * @version 0.1 2014-3-2
 **/
public class IndexManager {

	static Logger LOGGER = LoggerFactory.getLogger(IndexManager.class);
	
	private static IndexManager instance ;
	
	private static Map<EntityAttr, IndexPipe> indexPipeMap = null;
	
	private IndexManager(){
		
		indexPipeMap = new HashMap<EntityAttr, IndexPipe>();
	}
	
	/**
	 * Get the IndexManager singleton instance 
	 **/
	public static IndexManager getInstance(){
		if(instance == null)
			instance = new IndexManager();
		
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
	public void offerIndexQueue(IndexInfo indexInfo){
		
		if(!EventDispatcher.getInstance().isRunning()){
			persistIndexInfo(indexInfo);
			return;
		}
		
		IndexPipe indexPipe = indexPipeMap.get(indexInfo.getEntityAttr());
		if(indexPipe == null){// create new one
			
			indexPipe = new IndexPipe(indexInfo.getEntityAttr());
			indexPipeMap.put(indexInfo.getEntityAttr(), indexPipe);
			indexPipe.offer(indexInfo);
			
			EventDispatcher.getInstance().sendPayload(indexPipe,EventType.INDEX);
			
		}else if(indexPipe.isEmpty()){// since empty let cache hooker to drop it.
			
			indexPipe = new IndexPipe(indexInfo.getEntityAttr());
			indexPipeMap.put(indexInfo.getEntityAttr(), indexPipe);
			indexPipe.offer(indexInfo);
			
			EventDispatcher.getInstance().sendPayload(indexPipe,EventType.INDEX);
			
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
	public boolean dropIndexQueue(IndexPipe indexPipe){
		
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

	public void persistIndexInfo(IndexInfo indexinfo){
		System.out.println("====Persist=====");
	}
	
	public void loadIndexInfo(){
		
	}
}
