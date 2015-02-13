package com.doccube.core.accessor;

import java.util.Date;
import java.util.List;

import com.doccube.core.EntryKey;
import com.doccube.core.ITraceable;
import com.doccube.core.security.EntryAce;
import com.doccube.core.security.EntryAcl;
import com.doccube.core.security.IAccessControl;
import com.doccube.meta.EntityAttr;

public class AccessControlEntry extends EntryInfo implements ITraceable ,IAccessControl{

	private String creator;
	private String modifier;
	private Date newCreate;
	private Date lastModify;
	private EntryAcl entryAcl;
	
	public AccessControlEntry (){
		
		super();
	}

	public AccessControlEntry (String entityName,String key){
		
		super();
		EntryKey entryKey = new EntryKey(entityName,key);
		this.setEntryKey(entryKey);
	}
	
	public AccessControlEntry (EntryKey entryKey){
		
		super();
		this.setEntryKey(entryKey);
	}
	
	public AccessControlEntry(List<EntityAttr> attrs){
		
		super(attrs);
		
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
