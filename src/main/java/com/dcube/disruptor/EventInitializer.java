package com.dcube.disruptor;

import com.dcube.exception.BaseException;
import com.dcube.launcher.CoreInitializer;
import com.dcube.launcher.LifecycleHooker;

/**
 * The event initializer
 * 
 * @author despird
 * @version 0.1 2015-4-21
 **/
public class EventInitializer extends CoreInitializer{

	/**
	 * Default constructor 
	 **/
	public EventInitializer() throws BaseException {
		super();
	}

	@Override
	public LifecycleHooker setupLifecycleHooker() throws BaseException {
		EventDispatcher instance = EventDispatcher.getInstance();
		return instance.getLifecycleHooker();
	}

}
