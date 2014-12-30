package com.obal.core.accessor;

import java.util.Date;

import com.obal.core.EntryInfo;
import com.obal.core.EntryKey;
import com.obal.core.ITraceable;

public abstract class TraceableEntry extends EntryInfo implements ITraceable{
	
	private static final long serialVersionUID = 1L;

	private String creator;
	private String modifier;
	private Date newCreate;
	private Date lastModify;
	
	public TraceableEntry(EntryKey key) {
		super(key);
	}

	public TraceableEntry(String entityName, String key) {
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
}
