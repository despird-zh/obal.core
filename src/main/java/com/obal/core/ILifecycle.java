package com.obal.core;

import com.obal.exception.BaseException;

public interface ILifecycle {
	
	/**
	 * The life cycle state enumeration items.
	 **/
	public static enum State{
		
		UNKNOWN,
		INIT,
		START,
		RUNNING,
		STOP
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
