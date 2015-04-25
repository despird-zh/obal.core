package com.dcube.cache;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.dcube.core.EntryKey;
import com.dcube.disruptor.EventPayload;

/**
 * CacheEntryPipe is EventPayload implementation as carrier of CacheInfo,
 * Once it be created, it will be the pipe to transfer next coming caching request.
 * It holds a queue to align the cache request of same EntryKey.
 **/
public class CacheEntryPipe implements EventPayload{
	
	private EntryKey entryKey = null;
	
	/** the cache queue */
	private ConcurrentLinkedQueue<CacheInfo> cacheQueue = null;
	
	/**
	 * Constructor with entryKey 
	 **/
	public CacheEntryPipe(EntryKey entryKey){
		this.entryKey = entryKey;
		this.cacheQueue = new ConcurrentLinkedQueue<CacheInfo>();
	}
	
	/**
	 * peek a cache info out of queue 
	 **/
	public CacheInfo peek(){
		return cacheQueue.peek();
	}
	
	/**
	 * poll a cache info out of queue 
	 **/
	public CacheInfo poll(){
		return cacheQueue.poll();
	}
	
	/**
	 * offer a cache info into queue 
	 **/
	public void offer(CacheInfo data){
		this.cacheQueue.offer(data);
	}
	
	/**
	 * check if the queue is empty 
	 **/
	public boolean isEmpty(){
		
		return this.cacheQueue.isEmpty();
	}
	
	/**
	 * Get the entry key 
	 **/
	public EntryKey getEntryKey(){
		
		return this.entryKey;
	}
}
