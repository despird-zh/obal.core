package com.dcube.core.security;

import java.util.Set;

import com.dcube.core.IEntityAccessor;
import com.dcube.core.IEntityEntry;
import com.dcube.core.security.AclConstants.AcePrivilege;
import com.dcube.core.security.AclConstants.AceType;
import com.dcube.exception.AccessorException;

/**
 * IAccessControlAccessor indicates the methods of Access controllable entry Accessor.
 **/
public interface IAccessControlAccessor<GB extends IEntityEntry> extends IEntityAccessor<GB> {

	/**
	 * Get the entry ace of specified subject(user or group).
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject of ace
	 * @return EntryAce 
	 **/
	public EntryAce getEntryAce(String key, AceType type, String name) throws AccessorException;
	
	/**
	 * Get the entry acl
	 * @param key the entry key
	 * @return EntryAcl 
	 **/
	public EntryAcl getEntryAcl(String key)throws AccessorException;
	
	/**
	 * Grant business permission to specified subject on entry
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param permissions 
	 **/
	public void grantPermission(String key, AceType type, String name, String ... permissions)throws AccessorException;
	
	/**
	 * Revoke the permission from specified subject on entry
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param permissions 
	 **/
	public void revokePermission(String key, AceType type, String name, String ... permissions)throws AccessorException;
	
	/**
	 * Get the permissions of specified subject on entry
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 **/
	public Set<String> getPermissions(String key, AceType type, String name)throws AccessorException;
	
	/**
	 * Promote the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param privilege the privilege none/browse/read/write/delete
	 **/
	public boolean promote(String key, AceType type, String name, AcePrivilege privilege)throws AccessorException;
	
	/**
	 * Demote the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param privilege the privilege none/browse/read/write/delete
	 **/
	public boolean demote(String key, AceType type, String name, AcePrivilege privilege)throws AccessorException;
	
	/**
	 * Set the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param privilege the privilege none/browse/read/write/delete
	 **/
	public void setPrivilege(String key, AceType type, String name, AcePrivilege privilege)throws AccessorException;
	
	/**
	 * Get the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 **/
	public AcePrivilege getPrivilege(String key, AceType type, String name)throws AccessorException;
	
}
