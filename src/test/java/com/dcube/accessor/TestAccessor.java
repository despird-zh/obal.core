package com.dcube.accessor;

import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.meta.BaseEntity;

public class TestAccessor extends HEntityAccessor<EntityEntry>{


	public TestAccessor(AccessorContext context) {
		super("dcube.test",context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EntityEntry newEntityEntryObject() {
		return new EntityEntry();
	}

}
