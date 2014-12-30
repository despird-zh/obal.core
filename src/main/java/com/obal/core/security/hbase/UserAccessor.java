package com.obal.core.security.hbase;

import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.hbase.HEntityAccessor;
import com.obal.core.security.Principal;
import com.obal.meta.BaseEntity;

public class UserAccessor extends HEntityAccessor<Principal> {

	public UserAccessor(BaseEntity schema) {
		super(schema);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HEntryWrapper<Principal> getEntryWrapper() {
		
		return new UserWrapper();
	}

}
