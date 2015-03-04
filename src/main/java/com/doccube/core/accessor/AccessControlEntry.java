package com.doccube.core.accessor;

import java.util.Date;
import com.doccube.core.EntryKey;
import com.doccube.core.ITraceable;
import com.doccube.core.security.EntryAce;
import com.doccube.core.security.EntryAcl;
import com.doccube.core.security.IAccessControl;
import com.doccube.exception.SecurityException;

public class AccessControlEntry extends EntryInfo implements ITraceable ,IAccessControl{

	public AccessControlEntry (String entityName,String key){
		
		super(entityName, key);
	}
	
	public AccessControlEntry (EntryKey entryKey){
		
		super(entryKey);
	}

	
	@Override
	public String getCreator() {
		
		return super.getAttrValue(ATTR_CREATOR, String.class);
	}

	@Override
	public void setCreator(String creator) {
		
		super.setAttrValue(ITraceable.ATTR_CREATOR, creator);
		
	}

	@Override
	public String getModifier() {
		
		return super.getAttrValue(ITraceable.ATTR_MODIFIER, String.class);
	}

	@Override
	public void setModifier(String modifier) {
		
		super.setAttrValue(ITraceable.ATTR_MODIFIER, modifier);
	}

	@Override
	public Date getNewCreate() {
		
		return super.getAttrValue(ITraceable.ATTR_NEWCREATE, Date.class);
	}

	@Override
	public void setNewCreate(Date newCreate) {

		super.setAttrValue(ITraceable.ATTR_NEWCREATE, newCreate);
	}

	@Override
	public Date getLastModify() {
		
		return super.getAttrValue(ITraceable.ATTR_LASTMOFIFY, Date.class);
	}

	@Override
	public void setLastModify(Date lastModify) {

		super.setAttrValue(ITraceable.ATTR_LASTMOFIFY, lastModify);
	}
	
	public EntryAcl getEntryAcl() throws SecurityException{
		
		String aclStr = super.getAttrValue(IAccessControl.ATTR_ACL, String.class);
		EntryAcl acl = null;

		acl = EntryAcl.readJson(aclStr);

		return acl;
	}
	
	public void setEntryAcl(EntryAcl acl) throws SecurityException{
		
		String jsonStr = null;
		jsonStr = EntryAcl.writeJson(acl);
		super.setAttrValue(IAccessControl.ATTR_ACL, jsonStr);
		
	}
	
	public void addEntryAce(EntryAce ace) throws SecurityException{
		
		EntryAcl acl = getEntryAcl();		
		acl.addEntryAce(ace);		
		setEntryAcl(acl);
	}

}
