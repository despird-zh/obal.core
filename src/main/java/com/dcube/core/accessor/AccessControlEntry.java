package com.dcube.core.accessor;

import java.util.Date;

import com.dcube.core.EntryKey;
import com.dcube.core.ITraceable;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;
import com.dcube.core.security.IAccessControl;
import com.dcube.exception.SecurityException;
import com.dcube.meta.EntityConstants.AccessControlTraceInfo;

public class AccessControlEntry extends EntryInfo implements ITraceable ,IAccessControl{

	public AccessControlEntry (String entityName,String key){
		
		super(entityName, key);
	}
	
	public AccessControlEntry (EntryKey entryKey){
		
		super(entryKey);
	}


	public String getCreator() {
		
		return super.getAttrValue(AccessControlTraceInfo.Creator.attribute, String.class);
	}


	public void setCreator(String creator) {
		
		super.setAttrValue(AccessControlTraceInfo.Creator.attribute, creator);
		
	}

	public String getModifier() {
		
		return super.getAttrValue(AccessControlTraceInfo.Modifier.attribute, String.class);
	}

	public void setModifier(String modifier) {
		
		super.setAttrValue(AccessControlTraceInfo.Modifier.attribute, modifier);
	}


	public Date getNewCreate() {
		
		return super.getAttrValue(AccessControlTraceInfo.NewCreate.attribute, Date.class);
	}


	public void setNewCreate(Date newCreate) {

		super.setAttrValue(AccessControlTraceInfo.NewCreate.attribute, newCreate);
	}


	public Date getLastModify() {
		
		return super.getAttrValue(AccessControlTraceInfo.LastModify.attribute, Date.class);
	}


	public void setLastModify(Date lastModify) {

		super.setAttrValue(AccessControlTraceInfo.LastModify.attribute, lastModify);
	}
	
	public EntryAcl getEntryAcl() throws SecurityException{
		
		String aclStr = super.getAttrValue(AccessControlTraceInfo.AccessControl.attribute, String.class);
		EntryAcl acl = null;

		acl = EntryAcl.readJson(aclStr);

		return acl;
	}
	
	public void setEntryAcl(EntryAcl acl) throws SecurityException{
		
		String jsonStr = null;
		jsonStr = EntryAcl.writeJson(acl);
		super.setAttrValue(AccessControlTraceInfo.AccessControl.attribute, jsonStr);
		
	}
	
	public void addEntryAce(EntryAce ace) throws SecurityException{
		
		EntryAcl acl = getEntryAcl();		
		acl.addEntryAce(ace);		
		setEntryAcl(acl);
	}

}
