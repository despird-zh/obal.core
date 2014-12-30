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
package com.obal.cache.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cacheable indicate whether the method parameter or return value need cache operation
 * <p>
 * Cache operation usually occurs on two kinds of situation: entry or attribute.
 * as per store decide where to r/w cache.
 * </p>
 * <p>Entry value cache: need to specify necessary( entrykey+entity+value / entrykey+value )</p>
 * <p>Attribute value cache: need to specify necessary( entrykey+attr+value )</p>
 * 
 * @author despird
 * @version 0.1 2014-2-1
 * 
 * @See EntryKey
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheablePut {

	/**
	 * the parameter name of EntryKey object or String value of key
	 **/
	String entrykey() default "";
	
	/**
	 * the parameter name indicate the entity name
	 **/
	String entity() default "";
	
	/**
	 * the parameter name of attribute object or string name of attribute 
	 **/
	String attr() default "";
	
	/**
	 * the name of object to be applied cache operation
	 **/
	String value() default "";
}
