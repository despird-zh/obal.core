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
package com.dcube.util;

import com.dcube.core.AccessorFactory;
import com.dcube.core.IBaseAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;

/**
 * Utility tool class for Accessor acquire.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 * @see AccessorFactory
 **/
public class AccessorUtils {

	/**
	 * Get Entry Accessor
	 * 
	 * @param builderName the builder name
	 * @param principal the user principal
	 * @param entityName the entity name
	 * 
	 * @return K the instance of accessor 
	 **/
	public static <K> K getEntityAccessor(String builderName, Principal principal,String entityName)throws AccessorException{
		
		return AccessorFactory.buildEntityAccessor(builderName,principal, entityName);
	}	
	
	/**
	 * Get Entry Accessor
	 * @param principal the user principal
	 * @param entityName
	 * 
	 * @return K the instance of accessor 
	 **/
	public static <K> K getEntityAccessor(Principal principal,String entityName)throws AccessorException{
		
		return AccessorFactory.buildEntityAccessor(principal, entityName);
	}

	/**
	 * Get GenericAccessor
	 * 
	 * @param builderName the builder name
	 * @param principal the user principal
	 * @param accessorName
	 * 
	 * @return K the instance of accessor 
	 **/
	public static <K> K getGenericAccessor(String builderName, Principal principal,String accessorName)throws AccessorException{
		
		return AccessorFactory.buildGenericAccessor(builderName, principal, accessorName);
	}
	
	/**
	 * Get General Accessor
	 * @param principal the user principal
	 * @param accessorName
	 * 
	 * @return K the instance of accessor 
	 **/
	public static <K> K getGenericAccessor(Principal principal,String accessorName)throws AccessorException{
		
		return AccessorFactory.buildGenericAccessor(principal, accessorName);
	}


	/**
	 * Get embed General Accessor by mock-up accessor
	 * 
	 * @param mockupAccessor the mock-up instance 
	 * @param entryName accessorname
	 * 
	 * @return K the instance of accessor 
	 **/
	public static <K> K getGenericAccessor(IBaseAccessor mockupAccessor, String accessorName)throws AccessorException{
		
		return AccessorFactory.buildGenericAccessor(mockupAccessor, accessorName);
	}
	
	/**
	 * Get embed EntityAccessor by assigned mock-up accessor.
	 * 
	 * @param mockupAccessor the mock-up instance 
	 * @param entryName
	 * 
	 * @return K the instance of accessor 
	 **/
	public static <K> K getEntityAccessor(IBaseAccessor mockupAccessor, String entryName)throws AccessorException{
		
		return AccessorFactory.buildEntityAccessor(mockupAccessor, entryName);
	}
	
	/**
	 * Release the resource of accessor array
	 * 
	 * @param bAccessors The accessor array of IBaseAccessor object. 
	 * 
	 **/
	public static void closeAccessor(IBaseAccessor ... bAccessors){
		
		for(IBaseAccessor bAccessor:bAccessors){
			if(bAccessor != null)
				try {
					bAccessor.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
