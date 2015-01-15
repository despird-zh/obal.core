package com.obal.core;

import com.obal.exception.BaseException;

public interface ILifecycle {
	
	public static enum State{
		
		UNKNOWN,
		INIT,
		START,
		RUNNING,
		STOP
	}
	
	public State state();
	
	public void regListener(LifecycleListener listener);
	
	public void unregListener(LifecycleListener listener);
	
	public void clearListener();
	
	public void initial() throws BaseException;
	
	public void start() throws BaseException;
	
	public void stop() throws BaseException;
}
