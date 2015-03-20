package com.dcube.core.security;

import com.dcube.exception.SecurityException;
/**
 * IAccessControl indicates implementation support Access control
 * 
 * @author despird
 * @version 0.1 2014-2-1
 * 
 **/
public interface IAccessControl {
	
	public static final String ATTR_ACL = "i_acl";
	
	public EntryAcl getEntryAcl() throws SecurityException;
	
	public void setEntryAcl(EntryAcl acl) throws SecurityException;
	
	public void addEntryAce(EntryAce ace) throws SecurityException;
	
}