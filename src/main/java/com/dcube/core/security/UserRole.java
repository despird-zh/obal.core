package com.dcube.core.security;

import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.EntryKey;
import com.dcube.core.TraceInfo;
import com.dcube.meta.EntityConstants;

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
	/** tracing information */
	private TraceInfo traceInfo = null;
	/** the users under this group */
	private Set<String> users;
	/** the subgroups */
	private Set<String> groups;
	/** description */
	private String description = null;
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
	public String name(){
		
		return role;
	}
	
	public TraceInfo getTraceInfo() {
		return traceInfo;
	}

	public void setTraceInfo(TraceInfo traceInfo) {
		this.traceInfo = traceInfo;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
			.append(this.role, that.name()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.role)
				.toHashCode();
	}
	
}
