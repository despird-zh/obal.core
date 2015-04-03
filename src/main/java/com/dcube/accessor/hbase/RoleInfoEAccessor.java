package com.dcube.accessor.hbase;

import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;

public class RoleInfoEAccessor extends HEntityAccessor<TraceableEntry>{

	public RoleInfoEAccessor(String accessorName) {
		super(accessorName);
	}

	@Override
	public TraceableEntry newEntityEntry() {
		
		return new TraceableEntry();
	}

}
