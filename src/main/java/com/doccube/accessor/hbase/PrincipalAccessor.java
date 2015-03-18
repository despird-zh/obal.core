package com.doccube.accessor.hbase;

import java.util.List;

import com.doccube.accessor.IPrincipalAccessor;
import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.hbase.HGenericAccessor;
import com.doccube.core.security.Principal;
import com.doccube.exception.AccessorException;
import com.doccube.meta.EntityConstants;

public class PrincipalAccessor extends HGenericAccessor implements IPrincipalAccessor{

	public PrincipalAccessor(AccessorContext context) {
		super(EntityConstants.ACCESSOR_GENERIC_USER,context);
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
		
		return null;
	}

}
