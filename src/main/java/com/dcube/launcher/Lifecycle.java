package com.dcube.launcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.exception.BaseException;

public abstract class Lifecycle {

	private static Logger logger = LoggerFactory.getLogger(CoreFacade.class);
	/**
	 * The life cycle state enumeration items.
	 **/
	public static enum LifeState{
		
		UNKNOWN,
		INITIAL,
		STARTUP,
		RUNNING,
		SHUTDOWN
	}
	
	/** the state of life cycle */
	protected LifeState state;
	/** reentrant lock */
	private ReentrantLock lock = new ReentrantLock(); // lock
	/** hooker list*/
	private List<LifecycleHooker> hookers = new ArrayList<LifecycleHooker>();
	
	private List<LifeCycleMessage> messageList = new ArrayList<LifeCycleMessage>();
	
	/**
	 * Get the state of Life cycle instance 
	 **/
	public LifeState state(){
		return state;
	}
	
	/**
	 * Register the life cycle listener  
	 **/
	public void regLifecycleHooker(LifecycleHooker hooker){
		
		lock.lock();
		int count = hookers.size()-1;
		
		if(count < 0){
			hookers.add( hooker);
			hooker.setLauncher(this);
		}else{
			while(count >=0){
				
				if( hookers.get(count).priority() == hooker.priority() ){
					hookers.add(count, hooker);
					break;
				}					
				else if( hookers.get(count).priority() > hooker.priority() ){
					hookers.add(count +1, hooker);
					break;
				}					
				else if(count == 0){
					hookers.add(count, hooker);
					break;
				}
				count--;
			}
			hooker.setLauncher(this);
		}
		lock.unlock();
	}
	
	/**
	 * Unregister the life cycle listener 
	 **/
	public void unregLifecycleHooker(LifecycleHooker hooker){
		lock.lock();
		hookers.remove(hooker);
		lock.unlock();
	}
	
	/**
	 * Clear the listener 
	 **/
	public void clearLifecycleHooker(){
		lock.lock();
		hookers.clear();
		lock.unlock();
	}
	
	/**
	 * Initial processing 
	 **/
	public abstract void initial() throws BaseException;
	
	/**
	 * start processing 
	 **/
	public abstract void startup() throws BaseException;
	
	/**
	 * stop processing 
	 **/
	public abstract void shutdown() throws BaseException;
	
	/**
	 * Receive the feedback message from Hooker
	 **/
	public void feedback(String hookerName, boolean errorFlag,
			Date time, String message) {
		LifeCycleMessage msg = new LifeCycleMessage(hookerName, time, errorFlag, message);
		logger.debug("Feedback -> {} - {} - {}", new Object[]{time,errorFlag?"ERROR":"NORMAL",message});
		messageList.add(msg);
	}
	
	/**
	 * Send event to different listener. 
	 **/
	public void fireEvent(LifeState state){
		lock.lock();
		int count = this.hookers.size();
		for(int i = 0 ; i < count ; i++){
			
			hookers.get(i).onEvent(state);
		}
		lock.unlock();
	}
	
	/** Inner Message class */
	protected static class LifeCycleMessage {
		String hooker = null;
		Date time = null;
		String message = null;
		boolean errorFlag = false;

		public LifeCycleMessage(String hooker, Date time, boolean errorFlag, String message) {
			this.time = time;
			this.errorFlag = errorFlag;
			this.message = message;
			this.hooker = hooker;
		}
	}
}
