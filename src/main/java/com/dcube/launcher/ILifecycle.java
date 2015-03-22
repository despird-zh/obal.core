package com.dcube.launcher;

import java.util.Date;

import com.dcube.exception.BaseException;

public interface ILifecycle {
	
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
	
	/**
	 * Get the state of Life cycle instance 
	 **/
	public LifeState state();
	
	/**
	 * Register the life cycle listener  
	 **/
	public void regHooker(LifecycleHooker hooker);
	
	/**
	 * Unregister the life cycle listener 
	 **/
	public void unregHooker(LifecycleHooker hooker);
	
	/**
	 * Clear the listener 
	 **/
	public void clearHooker();
	
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
	
	/**
	 * Receive the feedback message from Hooker
	 **/
	public void receiveFeedback(String hookerName,boolean errorFlag , Date time, String message);
}
