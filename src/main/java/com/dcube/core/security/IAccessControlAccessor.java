package com.dcube.core.security;

import java.util.Set;

import com.dcube.core.IEntityAccessor;
import com.dcube.core.IEntityEntry;
import com.dcube.exception.AccessorException;

public interface IAccessControlAccessor<GB extends IEntityEntry> extends IEntityAccessor<GB> {

	public EntryAce getEntryAce(String key, EntryAce.AceType type, String name) throws AccessorException;
	
	public EntryAcl getEntryAcl(String key)throws AccessorException;
	
	public void grantPermissions(String key, EntryAce.AceType type, String name, String ... permissions)throws AccessorException;
	
	public void revokePermissions(String key, EntryAce.AceType type, String name, String ... permissions)throws AccessorException;
	
	public Set<String> getPermissions(String key, EntryAce.AceType type, String name)throws AccessorException;
	
	public boolean promote(String key, EntryAce.AceType type, String name, AclPrivilege privilege)throws AccessorException;
	
	public boolean demote(String key, EntryAce.AceType type, String name, AclPrivilege privilege)throws AccessorException;
	
	public void setPrivilege(String key, EntryAce.AceType type, String name, AclPrivilege privilege)throws AccessorException;
	
	public AclPrivilege getPrivilege(String key, EntryAce.AceType type, String name)throws AccessorException;

}
