package com.obal.core.security.hbase;

import com.obal.core.accessor.AccessorContext;
import com.obal.core.accessor.EntryInfo;
import com.obal.core.accessor.TraceableEntry;
import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.hbase.HEntityAccessor;
import com.obal.core.hbase.HRawWrapper;
import com.obal.core.hbase.HTraceableEntryWrapper;
import com.obal.core.security.Principal;
import com.obal.meta.BaseEntity;

public class UserAccessor extends HEntityAccessor<TraceableEntry> {

	public UserAccessor(AccessorContext context) {
		super(context);
	}

	@Override
	public HEntryWrapper<TraceableEntry> getEntryWrapper() {
		
		HTraceableEntryWrapper wrapper = new HTraceableEntryWrapper();
		
		return wrapper;
	}

}
