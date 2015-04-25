package com.dcube.index;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.dcube.disruptor.EventPayload;
import com.dcube.meta.EntityAttr;

public class IndexPipe implements EventPayload{

	EntityAttr attr ;
	/** the index queue */
	private ConcurrentLinkedQueue<IndexInfo> indexQueue = null;
	
	public IndexPipe(EntityAttr attr){
		this.attr = attr;
		indexQueue = new ConcurrentLinkedQueue<IndexInfo>();
	}
	
	/**
	 * peek a index info out of queue 
	 **/
	public IndexInfo peek(){
		return indexQueue.peek();
	}
	
	/**
	 * poll a index info out of queue 
	 **/
	public IndexInfo poll(){
		return indexQueue.poll();
	}
	
	/**
	 * offer a index info into queue 
	 **/
	public void offer(IndexInfo data){
		this.indexQueue.offer(data);
	}
	
	/**
	 * check if the queue is empty 
	 **/
	public boolean isEmpty(){
		
		return this.indexQueue.isEmpty();
	}
	
	/**
	 * Get the entity attribute 
	 **/
	public EntityAttr getEntityAttr(){
		return this.attr;
	}
}
