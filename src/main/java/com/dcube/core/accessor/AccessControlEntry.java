package com.dcube.core.accessor;

import java.util.Date;

import com.dcube.core.EntryKey;
import com.dcube.core.ITraceable;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;
import com.dcube.core.security.IAccessControl;
import com.dcube.meta.EntityConstants.TraceableEnum;

/**
 * AccessControlEntry support access control setting
 * 
 * @author despird
 * @version 0.1 2014-3-2
 **/
public class AccessControlEntry extends EntityEntry implements ITraceable ,IAccessControl{

	private EntryAcl acl = new EntryAcl();
	
	/**
	 * Default constructor
	 **/
	public AccessControlEntry (){
		
		super();
		setNewCreate(new Date(System.currentTimeMillis()));
		setLastModify(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * Constructor with entity name and entry key
	 * @param entityName
	 * @param key 
	 **/
	public AccessControlEntry (String entityName,String key){
		
		super(entityName, key);
		setNewCreate(new Date(System.currentTimeMillis()));
		setLastModify(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * Constructor with entry key 
	 **/
	public AccessControlEntry (EntryKey entryKey){
		
		super(entryKey);
		setNewCreate(new Date(System.currentTimeMillis()));
		setLastModify(new Date(System.currentTimeMillis()));
	}

	/**
	 * Get the creator 
	 **/
	public String getCreator() {
		
		return super.getAttrValue(TraceableEnum.Creator.attribute, String.class);
	}

	/**
	 * Set creator 
	 **/
	public void setCreator(String creator) {
		
		super.setAttrValue(TraceableEnum.Creator.attribute, creator);
		
	}

	/**
	 * Get modifier
	 **/
	public String getModifier() {
		
		return super.getAttrValue(TraceableEnum.Modifier.attribute, String.class);
	}

	/**
	 * Set modifier 
	 **/
	public void setModifier(String modifier) {
		
		super.setAttrValue(TraceableEnum.Modifier.attribute, modifier);
	}

	/**
	 * Get new create date time 
	 **/
	public Date getNewCreate() {
		
		return super.getAttrValue(TraceableEnum.NewCreate.attribute, Date.class);
	}

	/**
	 * Set new create date time 
	 **/
	public void setNewCreate(Date newCreate) {

		super.setAttrValue(TraceableEnum.NewCreate.attribute, newCreate);
	}

	/**
	 * Get last modify time 
	 **/
	public Date getLastModify() {
		
		return super.getAttrValue(TraceableEnum.LastModify.attribute, Date.class);
	}

	/**
	 * Set last modify time 
	 **/
	public void setLastModify(Date lastModify) {

		super.setAttrValue(TraceableEnum.LastModify.attribute, lastModify);
	}
	
	/**
	 * Get Entry Acl setting 
	 **/
	public EntryAcl getEntryAcl() {
		
		return acl;
	}
	
	/**
	 * Set Entry Acl setting 
	 **/
	public void setEntryAcl(EntryAcl acl) {
		
		this.acl = acl;
	}
	
	/**
	 * Add EntryAce to acl setting 
	 **/
	public void addEntryAce(EntryAce ace, boolean merge) {
			
		acl.addEntryAce(ace,merge);	//merge ace	

	}

}
