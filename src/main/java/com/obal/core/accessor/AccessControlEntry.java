package com.obal.core.accessor;

import java.util.Date;

import com.obal.core.EntryInfo;
import com.obal.core.EntryKey;
import com.obal.core.ITraceable;
import com.obal.core.security.EntryAce;
import com.obal.core.security.EntryAcl;
import com.obal.core.security.IAccessControl;

public abstract class AccessControlEntry extends EntryInfo implements ITraceable ,IAccessControl{

	private static final long serialVersionUID = 1L;

	private String creator;
	private String modifier;
	private Date newCreate;
	private Date lastModify;
	private EntryAcl entryAcl;
	
	public AccessControlEntry(EntryKey key) {
		super(key);
	}

	public AccessControlEntry(String entityName, String key) {
		super(entityName, key);
	}
	
	@Override
	public String getCreator() {
		
		return this.creator;
	}

	@Override
	public void setCreator(String creator) {
		
		this.creator = creator;
		
	}

	@Override
	public String getModifier() {
		
		return this.modifier;
	}

	@Override
	public void setModifier(String modifier) {
		
		this.modifier = modifier;
	}

	@Override
	public Date getNewCreate() {
		
		return this.newCreate;
	}

	@Override
	public void setNewCreate(Date newCreate) {
		this.newCreate = newCreate;
	}

	@Override
	public Date getLastModify() {
		
		return this.lastModify;
	}

	@Override
	public void setLastModify(Date lastModify) {
		this.lastModify = lastModify;
	}
	
	public EntryAcl getEntryAcl() throws SecurityException{
		
		return entryAcl;
	}
	
	public void setEntryAcl(EntryAcl acl) throws SecurityException{
		
		this.entryAcl = acl;
	}
	
	public void addEntryAce(EntryAce ace) throws SecurityException{
		
		if( null == entryAcl)
			this.entryAcl = new EntryAcl("defaultAcl");
		
		this.entryAcl.addEntryAce(ace);
	}
	
}
