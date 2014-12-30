package com.obal.core.security;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.obal.core.EntryKey;
import com.obal.meta.EntityConstants;

/**
 * User Role Collect users from different organizations but has same authority
 * e.g Administrator, Manager etc.
 * 
 * <p>Role only include user, no groups</p>
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public class UserRole extends EntryKey{

	/** role name */
	private String role = null;
	
	/**
	 * Constructor 
	 **/
	public UserRole(String role) {
		super(EntityConstants.ENTITY_USER_ROLE, null);
		this.role = role;
	}

	/**
	 * Constructor 
	 **/
	public UserRole(String role, String key){
		
		super(EntityConstants.ENTITY_USER_ROLE, key);
		this.role = role;
	}
	
	/**
	 * get the role name
	 * @return role name 
	 **/
	public String roleName(){
		
		return role;
	}
	
	/**
	 * check user owns role or not
	 * @return true: own ;false not own 
	 **/
	public boolean hasUser(String account){
		
		return false;
	}

	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof UserRole)) {
			return false;
		}
		// step 3
		UserRole that = (UserRole) other;
		// step 4
		return new EqualsBuilder()
			.append(this.role, that.roleName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.role)
				.toHashCode();
	}
	
}
