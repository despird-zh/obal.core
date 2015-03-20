package com.dcube.accessor.hbase;

import java.util.List;

import com.dcube.accessor.IPrincipalAccessor;
import com.dcube.core.hbase.HGenericAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityConstants;

public class PrincipalAccessor extends HGenericAccessor implements IPrincipalAccessor{

	public PrincipalAccessor() {
		super(EntityConstants.ACCESSOR_GENERIC_USER);
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
