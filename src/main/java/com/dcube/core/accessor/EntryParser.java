package com.dcube.core.accessor;

/**
 * EntryParser provides the methods to read/write data of GenericEntry object.
 * It let subclass easily to interact with GenericEntry. 
 **/
public abstract class EntryParser {
	
	/** the generic entry */
	protected GenericEntry gEntry = null;
	
	/**
	 * the default constructor, which take entry as data source 
	 **/
	public EntryParser(GenericEntry gEntry){
		
		this.gEntry = gEntry;
	}
	
	/**
	 * Get the GenericEntry object
	 **/
	public GenericEntry getGenericEntry(){
		return this.gEntry;
	}
	
	
}
