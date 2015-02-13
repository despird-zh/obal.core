package com.doccube.core.security.hbase;

import java.util.List;

import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.hbase.HGenericAccessor;
import com.doccube.core.security.Principal;
import com.doccube.core.security.accessor.IPrincipalAccessor;
import com.doccube.exception.AccessorException;

public class PrincipalAccessor extends HGenericAccessor implements IPrincipalAccessor{

	public PrincipalAccessor(AccessorContext context) {
		super(context);
	}

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
