package com.obal.core.security.accessor;

import java.util.List;

import com.obal.core.IBaseAccessor;
import com.obal.core.security.Principal;
import com.obal.exception.AccessorException;

public interface IPrincipalAccessor extends IBaseAccessor{
	
	public Principal getPrincipalByAccount(String account)throws AccessorException;

	public Principal getPrincipalByName(String name)throws AccessorException;

	public List<Principal> getPrincipalsByGroup(String group)throws AccessorException;

	public List<Principal> getPrincipalsByRole(String role)throws AccessorException;
}
