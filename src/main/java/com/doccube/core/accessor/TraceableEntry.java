package com.doccube.core.accessor;

import java.util.Date;
import java.util.List;

import com.doccube.core.EntryKey;
import com.doccube.core.ITraceable;
import com.doccube.meta.EntityAttr;

public class TraceableEntry extends EntryInfo implements ITraceable{

	public TraceableEntry (){
		
		super();
	}

	public TraceableEntry (String entityName,String key){
		
		super();
		EntryKey entryKey = new EntryKey(entityName,key);
		this.setEntryKey(entryKey);
	}
	
	public TraceableEntry (EntryKey entryKey){
		
		super();
		this.setEntryKey(entryKey);
	}
	
	public TraceableEntry(List<EntityAttr> attrs){
		
		super(attrs);
		
	}
	
	private String creator;
	private String modifier;
	private Date newCreate;
	private Date lastModify;
	
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
