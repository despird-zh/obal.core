package com.dcube.core.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.CoreConstants;
import com.dcube.core.security.AclConstants.PrivilegeEnum;
import com.dcube.core.security.AclConstants.TypeEnum;

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
	private PrivilegeEnum privilege;	
	/** the entry type */
	private TypeEnum type;	
	/** the permission set */
	private Set<String> permissionSet;
	
	/**
	 * Constructor for user ACE item.
	 * 
	 * @param combinedValue
	 *  
	 **/
	public EntryAce(TypeEnum TypeEnum, String name){
		
		this.type = TypeEnum;
		this.name = name;
		this.privilege = PrivilegeEnum.NONE;

	}
	
	/**
	 * Constructor for user ACE item.
	 * 
	 * @param roleName the role name
	 * @param privilege the access control privilege
	 *  
	 **/
	public EntryAce(String name, PrivilegeEnum privilege){
		
		this.type = TypeEnum.User;
		this.name = name;
		this.privilege = privilege;

	}
	
	/**
	 * Constructor 
	 * 
	 * @param TypeEnum the ace type
	 * @param role the role name
	 * @param privilege the access control privilege
	 *  
	 **/
	public EntryAce(TypeEnum TypeEnum, String name, PrivilegeEnum privilege){
		
		this.type = TypeEnum;
		this.name = name;
		this.privilege = privilege;

	}
	
	/**
	 * Constructor 
	 * 
	 * @param TypeEnum the ace type
	 * @param role the role name
	 * @param privilege the access control privilege
	 *  
	 **/

	public EntryAce(TypeEnum TypeEnum,  String name, PrivilegeEnum privilege, String ... permissions){
		
		this.type = TypeEnum;
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
	
	public EntryAce(TypeEnum TypeEnum,  String name, String ... permissions){
		
		this.type = TypeEnum;
		this.name = name;
		
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
	public TypeEnum getType(){
		
		return this.type;
	}
	
	/**
	 * Get the privilege : none, browse, read, write, delete 
	 **/
	public PrivilegeEnum getPrivilege(){
		
		return this.privilege;
	}
	
	/**
	 * Set the privilege 
	 **/
	public void setPrivilege(PrivilegeEnum privilege){
		
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
