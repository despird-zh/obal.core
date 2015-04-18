package com.dcube.accessor;

import com.dcube.core.IBaseAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;

public interface IPrincipalGAccessor extends IBaseAccessor{
	
	public Principal getPrincipalByAccount(String account)throws AccessorException;

	public Principal getPrincipalByName(String name)throws AccessorException;

}
