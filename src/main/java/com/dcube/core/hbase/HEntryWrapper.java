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
package com.dcube.core.hbase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.IEntryInfo;
import com.dcube.exception.WrapperException;
import com.dcube.meta.EntityAttr;


/**
 * HEntryWrapper wrap or parse the java bean object.
 * 
 * @author despird-zh
 * @version 0.1 2014-3-2
 **/
public abstract class HEntryWrapper<GB extends IEntryInfo> {

	/**
	 * Wrap the rawentry into bean object
	 * 
	 * @param attrs the attributes of rawEntry
	 * @param rawEntry the entry information
	 * @return GB the bean object. 
	 **/
	public abstract GB wrap(List<EntityAttr> attrs, final Result rawEntry) throws WrapperException;
	
	/**
	 * Parse bean object into raw Object
	 * 
	 * @param attrs the attributes of target entity
	 * @param entryInfo the entry information bean
	 * @return Object the raw object. 
	 **/	
	public abstract Put parse(List<EntityAttr> attrs, final GB entryInfo)throws WrapperException;
		
}
