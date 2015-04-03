package com.dcube.redis;

import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.redis.REntityAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.meta.BaseEntity;

public class TestAccessor extends REntityAccessor<EntityEntry>{

	public TestAccessor(AccessorContext context) {
		super("dcube.test",context);
	}

	@Override
	public EntityEntry newEntityEntry() {
		// TODO Auto-generated method stub
		return new EntityEntry();
	}

	@Override
	public boolean isFilterSupported(EntryFilter scanfilter, boolean throwExcep)
			throws AccessorException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EntityEntry doGetEntry(String entryKey, String... attributes)
			throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doDelEntryAttr(String attribute, String... entryKey)
			throws AccessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntryCollection doScanEntry(EntryFilter scanfilter,
			String... attributes) throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

}
