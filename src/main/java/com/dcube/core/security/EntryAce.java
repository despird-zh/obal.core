package com.dcube.core.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.CoreConstants;
import com.dcube.core.security.AclConstants.AcePrivilege;
import com.dcube.core.security.AclConstants.AceType;

/**
 * EntryAce is the access control setting for operator, it could be set at three levels.
 * <pre>
 * 1 - Person 
 * 2 - Group
 * </pre> 
 * <p>
 * The setting include privilege and Permission for operation.
 * </p>
 * <pre>
 *   type:user
 *   name:demo_account
 *   privilege: WRITE
 *   permissionSet:MOVE,AUDIT,APPROVE
 * </pre>
 * 
 **/
public class EntryAce implements Comparable<EntryAce> {
		
	/** the group/user name */
	private String name;	
	/** the privilege of role*/
	private AcePrivilege privilege;	
	/** the entry type */
	private AceType type;	
	/** the permission set */
	private Set<String> permissionSet;
	
	/**
	 * Constructor for user ACE item. default privilege is AcePrivilege.WRITE
	 * 
	 * @param type the AceType
	 * @param name the ace subject name, owner name/user name/ group name/ AclConstants.AceType.Other.name()
	 **/
	public EntryAce(AceType type, String name){
		
		this.type = type;
		this.name = name;
		this.privilege = AcePrivilege.WRITE;
	}
	
	/**
	 * Constructor for user ACE item. default AceType.User type
	 * 
	 * @param name the ace subject name, owner name/user name/ group name/ AclConstants.AceType.Other.name()
	 * @param privilege the access control privilege
	 *  
	 **/
	public EntryAce(String name, AcePrivilege privilege){
		
		this.type = AceType.User;
		this.name = name;
		this.privilege = privilege;

	}
	
	/**
	 * Constructor 
	 * 
	 * @param TypeEnum the ace type
	 * @param name the ace subject name, owner name/user name/ group name/ AclConstants.AceType.Other.name()
	 * @param privilege the access control privilege
	 *  
	 **/
	public EntryAce(AceType TypeEnum, String name, AcePrivilege privilege){
		
		this.type = TypeEnum;
		this.name = name;
		this.privilege = privilege;

	}
	
	/**
	 * Constructor 
	 * 
	 * @param type the ace type
	 * @param name the ace subject name, owner name/user name/ group name/ AclConstants.AceType.Other.name()
	 * @param privilege the access control privilege
	 * @param permissions the extend business permission
	 **/
	public EntryAce(AceType type,  String name, AcePrivilege privilege, String ... permissions){
		
		this.type = type;
		this.name = name;
		this.privilege = privilege;
		
		if(permissions == null || permissions.length ==0)
			return;
		else
			permissionSet = new HashSet<String>();
		
		for(String permission:permissions){
			
			permissionSet.add(permission);
		}
	}
	
	/**
	 * Constructor default privilege is AcePrivilege.WRITE
	 * 
	 * @param type the ace type
	 * @param name the ace subject name, owner name/user name/ group name/ AclConstants.AceType.Other.name()
	 * @param permissions the extend business permission
	 **/
	public EntryAce(AceType type,  String name, String ... permissions){
		
		this.type = type;
		this.name = name;
		this.privilege = AcePrivilege.WRITE;
		if(permissions == null || permissions.length ==0)
			return;
		else
			permissionSet = new HashSet<String>();
		
		for(String permission:permissions){
			
			permissionSet.add(permission);
		}
	}
	
	/**
	 * Get the name of user or group
	 **/
	public String getName(){
		
		return this.name;
	}
	
	/**
	 * Set the name of user or group
	 **/
	public void setName(String name){
		
		this.name = name;
	}
	
	/**
	 * Get the type the acl entry: user or group 
	 **/
	public AceType getType(){
		
		return this.type;
	}
	
	/**
	 * Get the privilege : none, browse, read, write, delete 
	 **/
	public AcePrivilege getPrivilege(){
		
		return this.privilege;
	}
	
	/**
	 * Set the privilege 
	 **/
	public void setPrivilege(AcePrivilege privilege){
		
		this.privilege = privilege;
	}
	
	/**
	 * Get the permission set 
	 **/
	public Set<String> getPermissions(){
		
		return this.permissionSet;
	}
	
	/**
	 * Grant permission setting to current ace.
	 * 
	 * @param permission
	 **/
	public void grant(String ... permission){
		for(String p:permission){
			this.permissionSet.add(p);
		}
	}
	
	/**
	 * Revoke permission setting from current ace.
	 * 
	 * @param permission
	 **/
	public void revoke(String ... permission){
		for(String p:permission){
			this.permissionSet.remove(p);
		}
	}
	
	@Override
	public String toString(){
		
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(this.name).append(CoreConstants.VALUE_SEPARATOR);
		sbuf.append(this.type).append(CoreConstants.VALUE_SEPARATOR);
		sbuf.append(this.privilege).append(CoreConstants.VALUE_SEPARATOR);
		
		if(null != permissionSet){
			for(String perm:permissionSet){
				
				sbuf.append(perm).append(CoreConstants.COLLECT_ELM_SEPARATOR);
			}
		}
		
		return sbuf.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof EntryAce)) {
			return false;
		}
		// step 3
		EntryAce that = (EntryAce) other;

		return new EqualsBuilder()
			.append(this.type, that.getType())
			.append(this.name, that.getName()).isEquals();
		
	}

	@Override
	public int hashCode() {
				
		return new HashCodeBuilder(17, 37)
			.append(this.type)
			.append(this.name).toHashCode();
			
	}
	
	@Override
	public int compareTo(EntryAce o) {

	    if(this.type.equals(o.type)){
	    	
	    	return this.name.compareTo(o.name);
	    }else {
	    	
	    	return this.type.priority - o.type.priority;
	    }
	}
}
