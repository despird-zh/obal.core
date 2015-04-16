package com.dcube.accessor.hbase;

import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.meta.EntityConstants;

public class RoleInfoEAccessor extends HEntityAccessor<TraceableEntry>{

	public RoleInfoEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_ROLE);
	}

	@Override
	public TraceableEntry newEntryObject() {
		
		return new TraceableEntry();
	}

}
