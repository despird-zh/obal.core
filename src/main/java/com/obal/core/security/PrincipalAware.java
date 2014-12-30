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
package com.obal.core.security;

/**
 * Class implements PrincipalAware can hold principal object.
 * make class do some operation base on principal
 * 
 * 
 * @since 0.1
 * @author G.Obal
 * 
 **/
public interface PrincipalAware {
	
	/**
	 * Set principal object
	 * 
	 * @param principal The principal object
	 **/
	public abstract void setPrincipal(Principal principal);
	
	/**
	 * Get principal object
	 * 
	 * @return Principal The principal object
	 **/
	public abstract Principal getPrincipal();
	
	/**
	 * Clear the principal object
	 **/
	public abstract void clearPrincipal();
}
