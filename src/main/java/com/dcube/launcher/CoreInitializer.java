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
		
		LifecycleHooker hooker = setupHooker();
		this.hookerName = hooker.name();
		CoreLauncher.regHooker(hooker);
	}
	
	public abstract LifecycleHooker setupHooker() throws BaseException;
	
}
