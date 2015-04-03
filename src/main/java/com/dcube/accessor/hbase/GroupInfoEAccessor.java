package com.dcube.accessor.hbase;

import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;

public class GroupInfoEAccessor extends HEntityAccessor<TraceableEntry>{

	public GroupInfoEAccessor(String accessorName) {
		super(accessorName);
	}

	@Override
	public TraceableEntry newEntityEntry() {
		
		return new TraceableEntry();
	}

}
