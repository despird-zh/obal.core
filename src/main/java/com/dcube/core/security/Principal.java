/*
 * Licensed to the G.Obal under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  G.Obal licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 */
package com.dcube.core.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.accessor.EntryParser;
import com.dcube.core.accessor.GenericEntry;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.UserEnum;

/**
 * Keep the principal info of user, this class is not defined like normal bean
 * It uses EntityEntry to R/W the attributes. 
 * <p>The EntryParser be used to communicate with EntityEntry</p>
 * 
 * @author despird
 * @version 1.0 2014-01-01
 *
 **/
public class Principal extends EntryParser{

	/**
	 * Constructor for new Principal
	 * 
	 * @param key the key information
	 * 
	 **/
	public Principal(String key){
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER,key);
	}
	
	/**
	 * Constructor for new Principal
	 * 
	 * @param account the account information
	 * @param password the account password
	 **/
	public Principal(String account, String password){
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER,null);
	}
	
	/**
	 * Constructor for new Principal
	 * 
	 * @param account the logon account 
	 * @param name the user name
	 * @param password the password
	 * 
	 **/
	public Principal(String account, String name, String password) {
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER,null);
		this.setAccount(account);
		this.setName(name);
		this.setPassword(password);
	}

	/**
	 * Constructor for new Principal
	 * 
	 * @param account the logon account 
	 * @param name the user name
	 * @param password the password
	 * @param source the account source
	 **/	
	public Principal(String account, String name,  String password,  String source) {
		super();
		rawEntry = new TraceableEntry(EntityConstants.ENTITY_USER,null);
		this.setAccount(account);
		this.setName(name);
		this.setPassword(password);
		this.setSource(source);
	}
	
	/**
	 * Get Account information 
	 **/
	public String getAccount() {
		
		return getAttrValue(UserEnum.Account.attribute, String.class);
	}
	
	public void setAccount(String account){
		
		this.setAttrValue(UserEnum.Account.attribute, account);
	}
	
	public String getSalt(){
		
		return getAttrValue(UserEnum.Salt.attribute, String.class);
	}
	
	public void setSalt(String salt){
		
		this.setAttrValue(UserEnum.Salt.attribute, salt);
	}
	
	public String getName() {
		return getAttrValue(UserEnum.Name.attribute, String.class);
	}

	public void setName(String name){
		
		this.setAttrValue(UserEnum.Name.attribute, name);
	}
	
	public String getPassword() {
		return getAttrValue(UserEnum.Password.attribute, String.class);
	}

	public void setPassword(String password){
		
		this.setAttrValue(UserEnum.Password.attribute, password);
	}
	
	public String getSource() {
		return getAttrValue(UserEnum.Source.attribute, String.class);
	}
	
	public void setSource(String source){
		
		this.setAttrValue(UserEnum.Source.attribute, source);
	}
	
	public Map<String, String> getProfile(){
		@SuppressWarnings("unchecked")
		Map<String, String> psetting = (Map<String, String>)this.getAttrValue(UserEnum.Profile.attribute, Map.class);
		return psetting;
	}
	
	public void setProfile(Map<String, String> settings){
		
		this.setAttrValue(UserEnum.Profile.attribute, settings);
	}
	
	public boolean inGroup(String group){
		
		@SuppressWarnings("unchecked")
		Map<String, String> groupmap = (Map<String, String>)this.getAttrValue(UserEnum.Groups.attribute, Map.class);
		if(groupmap == null)
			return false;
		else{
			
			return groupmap.containsKey(group);
		}
	}

	public boolean inRole(String role){
		@SuppressWarnings("unchecked")
		Map<String, String> rolemap = (Map<String, String>)this.getAttrValue(UserEnum.Roles.attribute, Map.class);
		if(rolemap == null)
			return false;
		else{
			
			return rolemap.containsKey(role);
		}
	}
		
	public void setGroups(Set<String> groups){
		if(groups != null){
			@SuppressWarnings("unchecked")
			Map<String, String> groupmap = (Map<String, String>)this.getAttrValue(UserEnum.Groups.attribute, Map.class);
			Map<String, String> attrMap = (groupmap == null)? new HashMap<String, String>():groupmap;
			for(String t:groups){
				if(!attrMap.containsKey(t))
					attrMap.put(t, EntityConstants.BLANK_VALUE);
			}
			this.setAttrValue(UserEnum.Groups.attribute, attrMap);
		}else
			this.setAttrValue(UserEnum.Groups.attribute, null);
	}
	
	public Set<String> getGroups(){
		
		@SuppressWarnings("unchecked")
		Map<String, String> groupmap = (Map<String, String>)this.getAttrValue(UserEnum.Groups.attribute, Map.class);
		
		return groupmap == null? null:groupmap.keySet();
	}
		
	public void setRoles(Set<String> roles){
		if(roles != null){
			@SuppressWarnings("unchecked")
			Map<String, String> rolemap = (Map<String, String>)this.getAttrValue(UserEnum.Roles.attribute, Map.class);
			Map<String, String> attrMap = (rolemap == null)? new HashMap<String, String>():rolemap;
			for(String t:roles){
				if(!attrMap.containsKey(t))
					attrMap.put(t, EntityConstants.BLANK_VALUE);
			}
			this.setAttrValue(UserEnum.Roles.attribute, attrMap);
		}else
			this.setAttrValue(UserEnum.Roles.attribute, null);
	}
	
	public Set<String> getRoles(){
		
		@SuppressWarnings("unchecked")
		Map<String, String> rolemap = (Map<String, String>)this.getAttrValue(UserEnum.Roles.attribute, Map.class);
		
		return rolemap == null? null:rolemap.keySet();
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
	
	/**
	 * Set raw generic entry 
	 **/
	@Override	
	public void setGenericEntry(GenericEntry rawEntry){
		EntityEntry temp = null;
		if(!(rawEntry instanceof EntityEntry)){
			throw new UnsupportedOperationException("Only EntityEntry is supported."); 
		}
		temp = (EntityEntry)rawEntry;
		if(!EntityConstants.ENTITY_USER.equals(temp.getEntityName())){
			throw new UnsupportedOperationException("Only User entiry EntityEntry is accepted."); 
		}
		super.setGenericEntry(rawEntry);
	}
	
	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof Principal)) {
			return false;
		}
		// step 3
		Principal that = (Principal) other;
		// step 4
		String source = this.getSource();
		String account = this.getAccount();
		String osrc = that.getSource();
		String oacct = this.getAccount();
		return new EqualsBuilder()
			.append(source, osrc)
			.append(account, oacct).isEquals();
	}

	@Override
	public int hashCode() {
		String source = this.getSource();
		String account = this.getAccount();
		return new HashCodeBuilder(17, 37).append(source)
				.append(account).toHashCode();
	}

}
