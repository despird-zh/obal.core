package com.dcube.accessor.hbase;

import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.hbase.HEntryWrapper;
import com.dcube.core.hbase.HTraceableEntryWrapper;

public class RoleInfoEAccessor extends HEntityAccessor<TraceableEntry>{

	public RoleInfoEAccessor(String accessorName) {
		super(accessorName);
	}

	@Override
	public HEntryWrapper<TraceableEntry> getEntryWrapper() {
		HTraceableEntryWrapper wrapper = new HTraceableEntryWrapper();		
		return wrapper;
	}

}
