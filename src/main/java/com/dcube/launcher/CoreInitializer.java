package com.dcube.launcher;

import com.dcube.exception.BaseException;

/**
 * CoreInitializer help to initial the component of Core instance 
 **/
public abstract class CoreInitializer {

	/**
	 * Default constructor, here the hooker will be bind to CoreLauncher
	 **/
	public CoreInitializer() throws BaseException{
		
		LifecycleHooker hooker = initial();
		CoreLauncher.regHooker(hooker);
	}
	
	public abstract LifecycleHooker initial() throws BaseException;
	
}
