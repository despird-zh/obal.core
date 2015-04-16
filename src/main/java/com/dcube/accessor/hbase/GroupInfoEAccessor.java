package com.dcube.accessor.hbase;

import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.meta.EntityConstants;

public class GroupInfoEAccessor extends HEntityAccessor<TraceableEntry>{

	public GroupInfoEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_GROUP);
	}

	@Override
	public TraceableEntry newEntryObject() {
		
		return new TraceableEntry();
	}

}
