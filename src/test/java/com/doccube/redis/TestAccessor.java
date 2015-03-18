package com.doccube.redis;

import com.doccube.core.EntryFilter;
import com.doccube.core.EntryKey;
import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.redis.REntityAccessor;
import com.doccube.core.redis.REntryWrapper;
import com.doccube.core.redis.RRawWrapper;
import com.doccube.exception.AccessorException;
import com.doccube.meta.BaseEntity;

public class TestAccessor extends REntityAccessor{

	public TestAccessor(AccessorContext context) {
		super("dcube.test",context);
	}

	@Override
	public EntryKey newKey() throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public REntryWrapper getEntryWrapper() {
		// TODO Auto-generated method stub
		return new RRawWrapper();
	}

	@Override
	public boolean isFilterSupported(EntryFilter scanfilter, boolean throwExcep)
			throws AccessorException {
		// TODO Auto-generated method stub
		return false;
	}

}
