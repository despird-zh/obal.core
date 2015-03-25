package com.dcube.core.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import com.dcube.core.CoreConstants;

/**
 * EntryAce is the access control setting for operator, it could be set at three levels.
 * <pre>
 * 1 - Person 
 * 2 - Group
 * 3 - Role
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
 * group name:g1/g2/g3
 * 
 **/
public class EntryAce implements Comparable<EntryAce> {
	
	/** the role name */
	private String name;
	
	/** the privilege of role*/
	private AclPrivilege privilege;
	
	/** the entry type */
	private String type;
	
	/** the permission set */
	private Set<String> permissionSet;
	
	private int typePriority = -1;
	
	/**
	 * Constructor for user ACE item.
	 * 
	 * @param combinedValue
	 *  
	 **/
	public EntryAce(String aceType, String name){
		
		this.type = aceType;
		this.name = name;
		this.privilege = AclPrivilege.NONE;
		this.setTypePriotiry();
	}
	
	/**
	 * Constructor for user ACE item.
	 * 
	 * @param roleName the role name
	 * @param privilege the access control privilege
	 *  
	 **/
	public EntryAce(String name, AclPrivilege privilege){
		
		this.type = CoreConstants.ACE_TYPE_USER;
		this.name = name;
		this.privilege = privilege;
		this.setTypePriotiry();
	}
	
	/**
	 * Constructor 
	 * 
	 * @param aceType the ace type
	 * @param role the role name
	 * @param privilege the access control privilege
	 *  
	 **/
	public EntryAce(String aceType, String name, AclPrivilege privilege){
		
		this.type = aceType;
		this.name = name;
		this.privilege = privilege;
		this.setTypePriotiry();
	}
	
	//
	private void setTypePriotiry(){
		
		if(CoreConstants.ACE_TYPE_USER.equals(this.type))
			this.typePriority = 3;
		if(CoreConstants.ACE_TYPE_GROUP.equals(this.type))
			this.typePriority = 2;
		if(CoreConstants.ACE_TYPE_ROLE.equals(this.type))
			this.typePriority = 1;
	}
	/**
	 * Constructor 
	 * 
	 * @param aceType the ace type
	 * @param role the role name
	 * @param privilege the access control privilege
	 *  
	 **/

	public EntryAce(String aceType,  String name, AclPrivilege privilege, String ... permissions){
		
		this.type = aceType;
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
	
	public EntryAce(String aceType,  String name, String ... permissions){
		
		this.type = aceType;
		this.name = name;
		
		if(permissions == null || permissions.length ==0)
			return;
		else
			permissionSet = new HashSet<String>();
		
		for(String permission:permissions){
			
			permissionSet.add(permission);
		}
	}
	
	public String name(){
		
		return this.name;
	}
	
	public String type(){
		
		return this.type;
	}
	
	public AclPrivilege privilege(){
		
		return this.privilege;
	}
	
	public void setPrivilege(AclPrivilege privilege){
		
		this.privilege = privilege;
	}
	
	public Set<String> permissions(){
		
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
			.append(this.type, that.type())
			.append(this.name, that.name()).isEquals();
		
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
	    	
	    	return this.typePriority - o.typePriority;
	    }
	}
}
