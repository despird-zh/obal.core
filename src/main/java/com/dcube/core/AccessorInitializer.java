package com.dcube.core;

import com.dcube.exception.BaseException;
import com.dcube.launcher.CoreInitializer;
import com.dcube.launcher.LifecycleHooker;

public class AccessorInitializer extends CoreInitializer {

	public AccessorInitializer() throws BaseException {
		super();
	}

	@Override
	public LifecycleHooker setupLifecycleHooker() throws BaseException {
		
		return AccessorFactory.getHooker();
	}

}
