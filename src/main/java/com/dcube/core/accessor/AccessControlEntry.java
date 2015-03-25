package com.dcube.core.accessor;

import java.util.Date;

import com.dcube.core.EntryKey;
import com.dcube.core.ITraceable;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;
import com.dcube.core.security.IAccessControl;
import com.dcube.exception.SecurityException;
import com.dcube.meta.EntityConstants.TraceableInfo;

@Deprecated
public class AccessControlEntry extends EntryInfo implements ITraceable ,IAccessControl{

	private EntryAcl acl = null;
	
	public AccessControlEntry (String entityName,String key){
		
		super(entityName, key);
	}
	
	public AccessControlEntry (EntryKey entryKey){
		
		super(entryKey);
	}


	public String getCreator() {
		
		return super.getAttrValue(TraceableInfo.Creator.attribute, String.class);
	}


	public void setCreator(String creator) {
		
		super.setAttrValue(TraceableInfo.Creator.attribute, creator);
		
	}

	public String getModifier() {
		
		return super.getAttrValue(TraceableInfo.Modifier.attribute, String.class);
	}

	public void setModifier(String modifier) {
		
		super.setAttrValue(TraceableInfo.Modifier.attribute, modifier);
	}


	public Date getNewCreate() {
		
		return super.getAttrValue(TraceableInfo.NewCreate.attribute, Date.class);
	}


	public void setNewCreate(Date newCreate) {

		super.setAttrValue(TraceableInfo.NewCreate.attribute, newCreate);
	}


	public Date getLastModify() {
		
		return super.getAttrValue(TraceableInfo.LastModify.attribute, Date.class);
	}


	public void setLastModify(Date lastModify) {

		super.setAttrValue(TraceableInfo.LastModify.attribute, lastModify);
	}
	
	public EntryAcl getEntryAcl() throws SecurityException{
		
		return acl;
	}
	
	public void setEntryAcl(EntryAcl acl) throws SecurityException{
		
		this.acl = acl;
	}
	
	public void addEntryAce(EntryAce ace, boolean merge) throws SecurityException{
			
		acl.addEntryAce(ace,merge);	//merge ace	

	}

}
