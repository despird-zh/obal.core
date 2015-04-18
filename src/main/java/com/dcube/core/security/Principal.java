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

import com.dcube.core.EntryKey;
import com.dcube.core.TraceInfo;
import com.dcube.meta.EntityConstants;

/**
 * Store the principal info of user
 * 
 * @author despird
 * @version 1.0 2014-01-01
 * @see com.dcube.core.security.Profile
 **/
public class Principal extends EntryKey{
		
	/** tracing information */
	private TraceInfo traceInfo = null;
	/** the account information */
	private String account = "";
	/** the name  */
	private String name = "";
	/** the password */
	private String password = "";
	/** the source of principal information */
	private String source = "";
	/** the salt to hash password */
	private String salt = "";
	/** the user profile info holder */
	private Profile profile = null;
	/** the groups */
	private Set<String> groups;
	/** the roles */
	private Set<String> roles;
	
	/**
	 * Constructor for new Principal
	 * 
	 * @param key the key information
	 * 
	 **/
	public Principal(String key){
		
		super(EntityConstants.ENTITY_USER,key);
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
		super(null,null);
		this.account = account;
		this.name = name;
		this.password = password;
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
		super(null,null);
		this.account = account;
		this.name = name;
		this.password = password;
		this.source = source;
	}
	
	/**
	 * Get Account information 
	 **/
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account){
		
		this.account = account;
	}
	
	public String getSalt(){
		
		return this.salt;
	}
	
	public void setSalt(String salt){
		
		this.salt = salt;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name){
		
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password){
		
		this.password = password;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source){
		
		this.source = source;
	}
	
	public Profile getProfile() {
		
		return profile;
	}
	
	public void setProfile(Profile profile) {
		
		this.profile = profile;
	}	
	
	public Map<String, Object> getProfileSettings(){
		
		return this.profile == null? new HashMap<String, Object>():this.profile.getSettings();
	}
	
	public void setProfileSettings(Map<String, Object> settings){
		
		this.profile = new Profile();
		this.profile.setSettings(settings);;
	}
	
	public boolean inGroup(String group){
		
		return false;
	}

	public boolean inRole(String role){
		
		return false;
	}
		
	public void setGroups(Set<String> groups){
		
		this.groups = groups;
	}
	
	public Set<String> getGroups(){
		
		return this.groups;
	}
		
	public void setRoles(Set<String> roles){
		this.roles = roles;
	}
	
	public Set<String> getRoles(){
		
		return this.roles;
	}
	
	public TraceInfo getTraceInfo(){
		
		return this.traceInfo;
	}
	
	public void setTraceInfo(TraceInfo traceInfo){
		
		this.traceInfo = traceInfo;
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
		return new EqualsBuilder()
			.append(this.source, that.source)
			.append(this.account, that.account).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.source)
				.append(this.account).toHashCode();
	}

}
