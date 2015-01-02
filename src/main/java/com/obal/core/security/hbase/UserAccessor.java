package com.obal.core.security.hbase;

import com.obal.core.accessor.AccessorContext;
import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.hbase.HEntityAccessor;
import com.obal.core.security.Principal;
import com.obal.meta.BaseEntity;

public class UserAccessor extends HEntityAccessor<Principal> {

	public UserAccessor(AccessorContext context) {
		super(context);
	}

	@Override
	public HEntryWrapper<Principal> getEntryWrapper() {
		
		return new UserWrapper();
	}

}
