package com.dcube.core.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.CoreConstants;
import com.dcube.meta.EntityConstants;

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
	
	public static enum PrivilegeEnum {

		NONE("n",0),
		BROWSE("b",1),
		READ("r",2),
		WRITE("w",3),
		DELETE("d",4);
		
		public final String abbr;
		public final int priority;
		
		/**
		 * Hide Rtype default constructor 
		 **/
		private PrivilegeEnum(String abbr,int priority){  
			this.abbr = abbr;
			this.priority = priority;
	    }
				
		@Override
		public String toString(){
			return this.abbr;
		}
		
	}
	
	/**
	 * The Acl info enumerator 
	 **/
	public static enum TypeEnum{

		User( "u",3),
		Group( "g",1);

		public final String abbr;
		public final int priority;
		/**
		 * Hide default constructor 
		 **/
		private TypeEnum( String abbr, int priority){  
			this.abbr = abbr;
			this.priority = priority;
	    }
		
	}
	
	/** the role name */
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
	
	public String getName(){
		
		return this.name;
	}
	
	public TypeEnum getType(){
		
		return this.type;
	}
	
	public PrivilegeEnum getPrivilege(){
		
		return this.privilege;
	}
	
	public void setPrivilege(PrivilegeEnum privilege){
		
		this.privilege = privilege;
	}
	
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
