package com.dcube.core.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.accessor.EntryParser;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.RoleEnum;
import com.dcube.meta.EntityConstants.UserEnum;

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
public class UserRole extends EntryParser{

	public UserRole(){
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER_ROLE,null);
	}
	
	/**
	 * Constructor 
	 **/
	public UserRole(String role) {
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER_ROLE,null);
		setAttrValue(RoleEnum.Name.attribute, role);
	}

	/**
	 * Constructor 
	 **/
	public UserRole(String role, String key){
		
		super(new TraceableEntry(EntityConstants.ENTITY_USER_ROLE, key));
		setAttrValue(RoleEnum.Name.attribute, role);
	}
	
	/**
	 * get the role name
	 * @return role name 
	 **/
	public String name(){
		
		return getAttrValue(RoleEnum.Name.attribute, String.class);
	}

	public Set<String> getUsers() {
		@SuppressWarnings("unchecked")
		Map<String, String> usermap = (Map<String, String>)this.getAttrValue(RoleEnum.Users.attribute, Map.class);
		
		return usermap.keySet();
	}

	public void setUsers(Set<String> users) {
		if(users != null){
			@SuppressWarnings("unchecked")
			Map<String, String> usermap = (Map<String, String>)this.getAttrValue(RoleEnum.Users.attribute, Map.class);
			Map<String, String> attrMap = (usermap == null)? new HashMap<String, String>():usermap;
			for(String t:users){
				if(!attrMap.containsKey(t))
					attrMap.put(t, EntityConstants.BLANK_VALUE);
			}
			this.setAttrValue(RoleEnum.Users.attribute, attrMap);
		}else
			this.setAttrValue(RoleEnum.Users.attribute, null);
	}

	public Set<String> getGroups() {
		@SuppressWarnings("unchecked")
		Map<String, String> groupmap = (Map<String, String>)this.getAttrValue(RoleEnum.Groups.attribute, Map.class);
		
		return groupmap.keySet();
	}

	public void setGroups(Set<String> groups) {
		if(groups != null){
			@SuppressWarnings("unchecked")
			Map<String, String> groupmap = (Map<String, String>)this.getAttrValue(RoleEnum.Groups.attribute, Map.class);
			Map<String, String> attrMap = (groupmap == null)? new HashMap<String, String>():groupmap;
			for(String t:groups){
				if(!attrMap.containsKey(t))
					attrMap.put(t, EntityConstants.BLANK_VALUE);
			}
			this.setAttrValue(UserEnum.Groups.attribute, attrMap);
		}else
			this.setAttrValue(UserEnum.Groups.attribute, null);
	}

	public String getDescription() {
		return getAttrValue(RoleEnum.Description.attribute, String.class);
	}

	public void setDescription(String description) {
		setAttrValue(RoleEnum.Description.attribute, description);
	}

	/**
	 * check user owns role or not
	 * @return true: own ;false not own 
	 **/
	public boolean hasUser(String account){
		
		return false;
	}

	/** get the attribute value */
	private <K> K getAttrValue(String attribute, Class<K> type){
		EntityEntry temp = (EntityEntry)rawEntry;
		return temp.getAttrValue(attribute, type);
	}
	
	/** set the attribute value */
	private void setAttrValue(String attribute, Object value){
		EntityEntry temp = (EntityEntry)rawEntry;		
		temp.changeAttrValue(attribute, value);
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
		String sname = this.name();
		String tname = that.name();
		return new EqualsBuilder()
			.append(sname, tname).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(name())
				.toHashCode();
	}
	
}
