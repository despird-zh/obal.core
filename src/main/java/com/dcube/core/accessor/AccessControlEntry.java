package com.dcube.core.accessor;

import java.util.Date;

import com.dcube.core.EntryKey;
import com.dcube.core.ITraceable;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;
import com.dcube.core.security.IAccessControl;
import com.dcube.meta.EntityConstants.TraceableEnum;


public class AccessControlEntry extends EntityEntry implements ITraceable ,IAccessControl{

	private EntryAcl acl = null;
	
	/**
	 * Default constructor
	 **/
	public AccessControlEntry (){
		
		super();
	}
	
	public AccessControlEntry (String entityName,String key){
		
		super(entityName, key);
	}
	
	public AccessControlEntry (EntryKey entryKey){
		
		super(entryKey);
	}


	public String getCreator() {
		
		return super.getAttrValue(TraceableEnum.Creator.attribute, String.class);
	}


	public void setCreator(String creator) {
		
		super.setAttrValue(TraceableEnum.Creator.attribute, creator);
		
	}

	public String getModifier() {
		
		return super.getAttrValue(TraceableEnum.Modifier.attribute, String.class);
	}

	public void setModifier(String modifier) {
		
		super.setAttrValue(TraceableEnum.Modifier.attribute, modifier);
	}


	public Date getNewCreate() {
		
		return super.getAttrValue(TraceableEnum.NewCreate.attribute, Date.class);
	}


	public void setNewCreate(Date newCreate) {

		super.setAttrValue(TraceableEnum.NewCreate.attribute, newCreate);
	}


	public Date getLastModify() {
		
		return super.getAttrValue(TraceableEnum.LastModify.attribute, Date.class);
	}


	public void setLastModify(Date lastModify) {

		super.setAttrValue(TraceableEnum.LastModify.attribute, lastModify);
	}
	
	public EntryAcl getEntryAcl() {
		
		return acl;
	}
	
	public void setEntryAcl(EntryAcl acl) {
		
		this.acl = acl;
	}
	
	public void addEntryAce(EntryAce ace, boolean merge) {
			
		acl.addEntryAce(ace,merge);	//merge ace	

	}

}
