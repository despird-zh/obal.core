package com.dcube.core.security;

/**
 * IAccessControl indicates implementation support Access control Entry
 * 
 * @author despird
 * @version 0.1 2014-2-1
 * 
 **/
public interface IAccessControl {

	/**
	 * Get the entry acl 
	 **/
	public EntryAcl getEntryAcl() ;
	
	/**
	 * Set the entry acl 
	 **/
	public void setEntryAcl(EntryAcl acl) ;
	
	/**
	 * Add the entry ace as per merge flag 
	 **/
	public void addEntryAce(EntryAce ace, boolean merge) ;
	
}
