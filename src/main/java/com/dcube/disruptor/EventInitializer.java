package com.dcube.disruptor;

import com.dcube.exception.BaseException;
import com.dcube.launcher.CoreInitializer;
import com.dcube.launcher.LifecycleHooker;

public class EventInitializer extends CoreInitializer{

	public EventInitializer() throws BaseException {
		super();
	}

	@Override
	public LifecycleHooker setupHooker() throws BaseException {
		EventDispatcher instance = EventDispatcher.getInstance();
		return instance.getHooker();
	}

}
