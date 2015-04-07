package com.dcube.cache;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.dcube.core.EntryKey;
import com.dcube.disruptor.EventPayload;

public class CacheEntryPipe implements EventPayload{
	
	private EntryKey entryKey = null;
	
	/** the cache queue */
	private ConcurrentLinkedQueue<CacheInfo> cacheQueue = null;
	
	public CacheEntryPipe(EntryKey entryKey){
		this.entryKey = entryKey;
	}
	
	public CacheInfo peek(){
		return cacheQueue.peek();
	}
	
	public CacheInfo poll(){
		return cacheQueue.poll();
	}
	
	public void offer(CacheInfo data){
		this.cacheQueue.offer(data);
	}
	
	public boolean isEmpty(){
		
		return this.cacheQueue.isEmpty();
	}
	
	public EntryKey getEntryKey(){
		
		return this.entryKey;
	}
}
