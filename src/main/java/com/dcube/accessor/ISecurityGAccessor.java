package com.dcube.accessor;

import com.dcube.core.IBaseAccessor;
import com.dcube.core.security.Principal;
import com.dcube.core.security.UserGroup;
import com.dcube.exception.AccessorException;

public interface ISecurityGAccessor extends IBaseAccessor{

	/**
	 * Get Principal by user account
	 * @param account
	 * @return Principal the principal object 
	 **/
	public Principal getPrincipalByAccount(String account)throws AccessorException;

	/**
	 * Get Principal by user name 
	 * @param name 
	 * @return Principal the principal object
	 **/
	public Principal getPrincipalByName(String name)throws AccessorException;

	/**
	 * Get Group by name
	 * @param name
	 * @return UserGroup the group account 
	 **/
	public UserGroup getGroupByName(String name) throws AccessorException;
}
