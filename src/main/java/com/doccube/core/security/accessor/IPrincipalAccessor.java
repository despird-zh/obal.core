package com.doccube.core.security.accessor;

import java.util.List;

import com.doccube.core.IBaseAccessor;
import com.doccube.core.security.Principal;
import com.doccube.exception.AccessorException;

public interface IPrincipalAccessor extends IBaseAccessor{
	
	public Principal getPrincipalByAccount(String account)throws AccessorException;

	public Principal getPrincipalByName(String name)throws AccessorException;

	public List<Principal> getPrincipalsByGroup(String group)throws AccessorException;

	public List<Principal> getPrincipalsByRole(String role)throws AccessorException;
}
