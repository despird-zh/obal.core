package com.doccube.accessor;

import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.accessor.EntryInfo;
import com.doccube.core.hbase.HEntityAccessor;
import com.doccube.core.hbase.HEntryWrapper;
import com.doccube.core.hbase.HRawWrapper;
import com.doccube.meta.BaseEntity;

public class TestAccessor extends HEntityAccessor<EntryInfo>{


	public TestAccessor(AccessorContext context) {
		super("dcube.test",context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HEntryWrapper<EntryInfo> getEntryWrapper() {
		HRawWrapper wrapper = new HRawWrapper();

		return wrapper;
	}

}
