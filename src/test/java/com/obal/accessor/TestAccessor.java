package com.obal.accessor;

import com.obal.core.accessor.AccessorContext;
import com.obal.core.accessor.RawEntry;
import com.obal.core.hbase.HEntityAccessor;
import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.hbase.HRawWrapper;
import com.obal.meta.BaseEntity;

public class TestAccessor extends HEntityAccessor<RawEntry>{


	public TestAccessor(AccessorContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HEntryWrapper<RawEntry> getEntryWrapper() {
		HRawWrapper wrapper = new HRawWrapper();

		return wrapper;
	}

}
