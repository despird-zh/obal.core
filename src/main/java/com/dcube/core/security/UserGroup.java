package com.dcube.core.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.EntryKey;
import com.dcube.core.TraceInfo;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.accessor.EntryParser;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.GroupEnum;
import com.dcube.meta.EntityConstants.RoleEnum;
import com.dcube.meta.EntityConstants.UserEnum;

/**
 * UserGroup collects users from same business entity or organization,
 * the user group usually used define the organize hierarchy.
 * The group name is unique amount Group table.
 * 
 * <p>A group include many users
 * </p>
 * <pre>
 * GroupA
 *   |-UsrA1
 *   |-UsrA2
 * ...
 * </pre>
 *  
 * @author despird
 * @version 0.1 2014-3-1
 * @since 0.1
 **/
public class UserGroup extends EntryParser{
	
	public UserGroup(){
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER_GROUP,null);
	}
	/**
	 * the constructor 
	 * @param group the group name
	 * @param key the key  
	 **/
	public UserGroup(String name, String key) {
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER_GROUP,key);
		setAttrValue(GroupEnum.Name.attribute, name);
	}

	/**
	 * the constructor 
	 * @param group the group name
	 **/
	public UserGroup(String name){
		
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER_GROUP,null);
		setAttrValue(GroupEnum.Name.attribute, name);
	}
	
	/**
	 * get the group name 
	 **/
	public String name(){
		
		return getAttrValue(GroupEnum.Name.attribute, String.class);
	}
	
	/**
	 * check user in group or not 
	 **/
	public boolean hasUser(String user){
		
		@SuppressWarnings("unchecked")
		Map<String,String > users = getAttrValue(GroupEnum.Name.attribute, Map.class);
		return users.containsKey(user);
	}
	
	/**
	 * Get the user member set 
	 * @return Set<String>
	 **/
	public Set<String> getUsers(){
		@SuppressWarnings("unchecked")
		Map<String,String > users = getAttrValue(GroupEnum.Name.attribute, Map.class);
		return users.keySet();
	}

	/**
	 * Set the user set
	 * @param users the set of group members
	 **/
	public void setUsers(Set<String> users) {
		if(users != null){
			@SuppressWarnings("unchecked")
			Map<String, String> usermap = (Map<String, String>)this.getAttrValue(GroupEnum.Users.attribute, Map.class);
			Map<String, String> attrMap = (usermap == null)? new HashMap<String, String>():usermap;
			for(String t:users){
				if(!attrMap.containsKey(t))
					attrMap.put(t, EntityConstants.BLANK_VALUE);
			}
			this.setAttrValue(GroupEnum.Users.attribute, attrMap);
		}else
			this.setAttrValue(GroupEnum.Users.attribute, null);
	}
	
	/**
	 * Get the description 
	 * @return the description of user group
	 **/
	public String getDescription() {
		return getAttrValue(GroupEnum.Description.attribute, String.class);
	}

	/**
	 * Set the description
	 * @param description the description of group 
	 **/
	public void setDescription(String description) {
		setAttrValue(GroupEnum.Description.attribute, description);
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
		if (!(other instanceof UserGroup)) {
			return false;
		}
		// step 3
		UserGroup that = (UserGroup) other;
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
