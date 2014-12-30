package com.obal.redis;

import com.obal.core.EntryFilter;
import com.obal.core.EntryKey;
import com.obal.core.redis.REntityAccessor;
import com.obal.core.redis.REntryWrapper;
import com.obal.core.redis.RRawWrapper;
import com.obal.exception.AccessorException;
import com.obal.meta.BaseEntity;

public class TestAccessor extends REntityAccessor{

	public TestAccessor(BaseEntity entitySchema) {
		super(entitySchema);
		// TODO Auto-generated constructor stub
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
