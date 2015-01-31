package com.obal.accessor;

import com.obal.core.accessor.AccessorContext;
import com.obal.core.accessor.EntryInfo;
import com.obal.core.hbase.HEntityAccessor;
import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.hbase.HRawWrapper;
import com.obal.meta.BaseEntity;

public class TestAccessor extends HEntityAccessor<EntryInfo>{


	public TestAccessor(AccessorContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HEntryWrapper<EntryInfo> getEntryWrapper() {
		HRawWrapper wrapper = new HRawWrapper();

		return wrapper;
	}

}
