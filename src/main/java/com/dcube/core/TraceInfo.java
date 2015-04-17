package com.dcube.core;

import java.util.Date;

public class TraceInfo implements ITraceable{

	private String creator;
	private Date newCreate;
	private String modifier;
	private Date lastModify;
	
	public TraceInfo(String creator, Date newCreate, String modifier, Date lastModify){
		
		this.creator = creator;
		this.newCreate = newCreate;
		this.modifier = modifier;
		this.lastModify = lastModify;
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
