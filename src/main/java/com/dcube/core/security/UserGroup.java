package com.dcube.core.security;

import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.EntryKey;
import com.dcube.core.TraceInfo;
import com.dcube.meta.EntityConstants;

/**
 * UserGroup collects users from same business entity or organization,
 * the user group usually used define the organize hierarchy.
 * The group name is unique amount Group table.
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
	private String name = null;
	/** tracing information */
	private TraceInfo traceInfo = null;
	/** the users under this group */
	private Set<String> users;
	/** the subgroups */
	private Set<String> groups;
	/** the parent group */
	private String parent = null;
	/** description */
	private String description = null;
	/**
	 * the constructor 
	 * @param group the group name
	 * @param key the key  
	 **/
	public UserGroup(String name, String key) {
		super(EntityConstants.ENTITY_USER_GROUP, key);
		this.name = name;
	}

	/**
	 * the constructor 
	 * @param group the group name
	 **/
	public UserGroup(String name){
		
		super(EntityConstants.ENTITY_USER_GROUP, null);
		this.name = name;
	}
	
	/**
	 * get the group name 
	 **/
	public String name(){
		
		return name;
	}
	
	/**
	 * check user in group or not 
	 **/
	public boolean hasUser(String user){
		
		return users.contains(user);
	}
	
	/**
	 * check group is included or not 
	 **/
	public boolean hasGroup(String group){
		
		return groups.contains(group);
	}
	
	/**
	 * get list of sub groups 
	 **/
	public Set<String> getGroups(){
		
		return this.groups;
	}
	
	public Set<String> getUsers(){
		
		return this.users;
	}
	
	public void addUser(String user){
		
		this.users.add(user);
	}
	
	public void removeUser(String user){
		
		this.users.remove(user);
	}
	
	public void setGroups(Set<String> groups){
		
		this.groups = groups;
		
	}
	
	public void setUsers(Set<String> users){
		
		this.users = users;
		
	}
	
	public TraceInfo getTraceInfo(){
		
		return this.traceInfo;
	}
	
	public void setTraceInfo(TraceInfo traceInfo){
		
		this.traceInfo = traceInfo;
	}
		
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
			.append(this.name, that.name()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.name)
				.toHashCode();
	}
}
