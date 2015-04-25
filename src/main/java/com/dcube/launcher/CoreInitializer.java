package com.dcube.launcher;

import com.dcube.exception.BaseException;

/**
 * CoreInitializer help to initial the component of Core instance 
 **/
public abstract class CoreInitializer {

	public String hookerName = null;
	
	/**
	 * Default constructor, here the hooker will be bind to CoreLauncher
	 **/
	public CoreInitializer() throws BaseException{
		
		LifecycleHooker hooker = setupLifecycleHooker();
		this.hookerName = hooker.name();
		CoreFacade.regLifecycleHooker(hooker);
	}
	
	/**
	 * Get the lifecycle event hooker for lifecycle operation 
	 **/
	public abstract LifecycleHooker setupLifecycleHooker() throws BaseException;
	
}
