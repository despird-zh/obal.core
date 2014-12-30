package com.obal.core.security;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.obal.core.EntryKey;
import com.obal.meta.EntityConstants;

/**
 * UserGroup collects users from same business entity or organization,
 * the user group usually used define the organize hierarchy.
 * <p>A group include many subgroups, but has only one parent group
 * </p>
 * <pre>
 * GroupA
 *   |-GrpupA1
 *   |  |-GroupA11
 *   |  |-GroupA12
 *   |     |-User1
 *   |     |- ...
 *   |-GrpupA2
 * ...
 * </pre>
 *  
 * @author despird
 * @version 0.1 2014-3-1
 * @since 0.1
 **/
public class UserGroup extends EntryKey{

	/** the group */
	private String group = null;
	
	private Set<String> users = null;
	
	/**
	 * the constructor 
	 * @param group the group name
	 * @param key the key  
	 **/
	public UserGroup(String group, String key) {
		super(EntityConstants.ENTITY_USER_GROUP, key);
		this.group = group;
	}

	/**
	 * the constructor 
	 * @param group the group name
	 **/
	public UserGroup(String group){
		
		super(EntityConstants.ENTITY_USER_GROUP, null);
		this.group = group;
	}
	
	/**
	 * get the group name 
	 **/
	public String groupName(){
		
		return group;
	}
	
	/**
	 * check user in group or not 
	 **/
	public boolean hasUser(String account){
		
		return false;
	}
	
	/**
	 * check group is included or not 
	 **/
	public boolean hasGroup(String group){
		
		return false;
	}
	
	/**
	 * get list of sub groups 
	 **/
	public List<UserGroup> getGroups(){
		
		return null;
	}
	
	public Set<String> getUsers(){
		
		return users;
	}
	
	public void getUsers(Set<String> users){
		
		this.users = users;
	}
	
	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof UserGroup)) {
			return false;
		}
		// step 3
		UserGroup that = (UserGroup) other;
		// step 4
		return new EqualsBuilder()
			.append(this.group, that.groupName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.group)
				.toHashCode();
	}
}
