package com.dcube.accessor;

import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.hbase.HEntryWrapper;
import com.dcube.core.hbase.HRawWrapper;
import com.dcube.meta.BaseEntity;

public class TestAccessor extends HEntityAccessor<EntityEntry>{


	public TestAccessor(AccessorContext context) {
		super("dcube.test",context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HEntryWrapper<EntityEntry> getEntryWrapper() {
		HRawWrapper wrapper = new HRawWrapper();

		return wrapper;
	}

}
