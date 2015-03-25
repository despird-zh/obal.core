package com.dcube.core.security;
/**
 * IAccessControl indicates implementation support Access control
 * 
 * @author despird
 * @version 0.1 2014-2-1
 * 
 **/
public interface IAccessControl {

	public EntryAcl getEntryAcl() ;
	
	public void setEntryAcl(EntryAcl acl) ;
	
	public void addEntryAce(EntryAce ace, boolean merge) ;
	
}
