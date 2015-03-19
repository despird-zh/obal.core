package com.dcube.core;

import com.dcube.exception.BaseException;

public interface ILifecycle {
	
	/**
	 * The life cycle state enumeration items.
	 **/
	public static enum State{
		
		UNKNOWN,
		BEFORE_INIT,
		INIT,
		AFTER_INIT,
		BEFORE_START,
		START,
		AFTER_START,
		RUNNING,
		BEFORE_STOP,
		STOP,
		AFTER_STOP
	}
	
	/**
	 * Get the state of Life cycle instance 
	 **/
	public State state();
	
	/**
	 * Register the life cycle listener  
	 **/
	public void regListener(LifecycleListener listener);
	
	/**
	 * Unregister the life cycle listener 
	 **/
	public void unregListener(LifecycleListener listener);
	
	/**
	 * Clear the listener 
	 **/
	public void clearListener();
	
	/**
	 * Initial processing 
	 **/
	public void initial() throws BaseException;
	
	/**
	 * start processing 
	 **/
	public void start() throws BaseException;
	
	/**
	 * stop processing 
	 **/
	public void stop() throws BaseException;
}
