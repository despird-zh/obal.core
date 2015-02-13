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
package com.doccube.core;

import com.doccube.core.accessor.AccessorContext;

/**
 * Base interface for all Accessor class, it provides methods to 
 * release resource and indicate embedded one or not.
 * <p>If accessor is embedded one, it's resource is copied from other accssor.
 * normally release method will not touch shared resources.
 * </p> 
 * 
 * @author despird
 **/
public interface IBaseAccessor {

	public void setAccessorContext(AccessorContext context);
	
	public  AccessorContext getAccessorContext();
	/**
	 * get embed flag
	 * @return flag 
	 **/
	public boolean isEmbed();
	
	/**
	 * set embed flag 
	 * @param embed the flag
	 **/
	public void setEmbed(boolean embed);
	
	/**
	 * Release the resources post operation. 
	 **/
	public void release();
}
