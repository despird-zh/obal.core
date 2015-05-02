package com.dcube.core.security;

import java.util.Set;

import com.dcube.core.IEntityAccessor;
import com.dcube.core.IEntityEntry;
import com.dcube.core.security.AclConstants.PrivilegeEnum;
import com.dcube.core.security.AclConstants.TypeEnum;
import com.dcube.exception.AccessorException;

public interface IAccessControlAccessor<GB extends IEntityEntry> extends IEntityAccessor<GB> {

	public EntryAce getEntryAce(String key, TypeEnum type, String name) throws AccessorException;
	
	public EntryAcl getEntryAcl(String key)throws AccessorException;
	
	public void grantPermission(String key, TypeEnum type, String name, String ... permissions)throws AccessorException;
	
	public void revokePermission(String key, TypeEnum type, String name, String ... permissions)throws AccessorException;
	
	public Set<String> getPermissions(String key, TypeEnum type, String name)throws AccessorException;
	
	public boolean promote(String key, TypeEnum type, String name, PrivilegeEnum privilege)throws AccessorException;
	
	public boolean demote(String key, TypeEnum type, String name, PrivilegeEnum privilege)throws AccessorException;
	
	public void setPrivilege(String key, TypeEnum type, String name, PrivilegeEnum privilege)throws AccessorException;
	
	public PrivilegeEnum getPrivilege(String key, TypeEnum type, String name)throws AccessorException;

}
