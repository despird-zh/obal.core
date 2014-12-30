package com.obal.core.security.hbase;

import java.util.List;

import com.obal.core.hbase.HGenericAccessor;
import com.obal.core.security.Principal;
import com.obal.core.security.accessor.IPrincipalAccessor;
import com.obal.exception.AccessorException;

public class PrincipalAccessor extends HGenericAccessor implements IPrincipalAccessor{

	@Override
	public Principal getPrincipalByAccount(String account)
			throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getPrincipalByName(String name) throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Principal> getPrincipalsByGroup(String group)
			throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Principal> getPrincipalsByRole(String role)
			throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

}
