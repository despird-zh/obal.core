package com.doccube.core;

public interface IEntryInfo extends IGenericInfo{

	/**
	 * Get the entry key
	 * 
	 * @return EntryKey the entry key
	 **/
	public EntryKey getEntryKey();
	
	/**
	 * Set entry key to Entry object 
	 **/
	public void setEntryKey(EntryKey entryKey);

}
