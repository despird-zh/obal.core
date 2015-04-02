package com.dcube.accessor.redis;

import com.dcube.core.CoreConstants;
import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.redis.REntityAccessor;
import com.dcube.core.redis.REntryWrapper;
import com.dcube.core.redis.RRawWrapper;
import com.dcube.exception.AccessorException;
import com.dcube.meta.BaseEntity;

public class RCacheAccessor extends REntityAccessor{

	public RCacheAccessor(AccessorContext context) {
		super(CoreConstants.CACHE_ACCESSOR,context);
	}

	@Override
	public REntryWrapper getEntryWrapper() {
		// TODO Auto-generated method stub
		return new RRawWrapper();
	}


}
