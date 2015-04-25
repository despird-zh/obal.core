package com.dcube.admin;

import com.dcube.exception.BaseException;
import com.dcube.launcher.CoreInitializer;
import com.dcube.launcher.LifecycleHooker;

public class EntityInitializer extends CoreInitializer{

	public EntityInitializer() throws BaseException {
		super();
	}

	@Override
	public LifecycleHooker setupLifecycleHooker() throws BaseException {

		EntityAdmin instance = EntityAdmin.getInstance();
		return instance.getHooker();
	}

}
