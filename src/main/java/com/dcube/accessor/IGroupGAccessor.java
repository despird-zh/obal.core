package com.dcube.accessor;

import java.util.List;

import com.dcube.core.IBaseAccessor;
import com.dcube.core.security.Principal;
import com.dcube.core.security.UserGroup;
import com.dcube.exception.AccessorException;

public interface IGroupGAccessor extends IBaseAccessor{
	
	public void addUser(String group, String user)throws AccessorException;

	public void removeUser(String group, String user)throws AccessorException;

	public void addGroup(String parent, String group)throws AccessorException;

	public void removeGroup(String parent, String group)throws AccessorException;
	
	public List<Principal> getPrincipalsByGroup(String group)throws AccessorException;

	public List<String> getGroupChain(String group)throws AccessorException;
	
	public UserGroup getGroupByName(String name) throws AccessorException;
}
