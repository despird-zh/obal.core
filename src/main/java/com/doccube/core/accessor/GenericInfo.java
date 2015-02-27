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
package com.doccube.core.accessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doccube.core.IGenericInfo;
import com.doccube.meta.EntityAttr;

/**
 * EntryInfo is the base class for all classes that be used to wrap the entry row
 * It only provide methods to access the entry key. User will extends it as need.
 * It not indicate the Traceable or AccessControllable feature.
 * 
 * @author despird
 * @version 0.1 2014-2-1 
 * 
 * @see RawEntry
 * @see RawAccessControlEntry
 * @see RawTraceableEntry
 * 
 **/
public class GenericInfo implements IGenericInfo{

	private Map<String, Object> values = null;
	private Map<String, EntityAttr> attrs = null;
	
	public GenericInfo (){
		
		values = new HashMap<String,Object> ();
		attrs = new HashMap<String, EntityAttr> ();
	}
	
	public GenericInfo(List<EntityAttr> attrs){
		
		for(EntityAttr attr:attrs){
			
			this.attrs.put(attr.getAttrName(), attr);
		}
		
	}
	
	@Override
	public EntityAttr getAttr(String attrname) {
		
		return attrs.get(attrname);
	}

	@Override
	public Collection<EntityAttr> getAttrs() {
		
		return attrs.values();
	}

	@Override
	public <K> K getAttrValue(String attrName, Class<K> targetType) {

		Object value = values.get(attrName);
		EntityAttr attr = attrs.get(attrName);
		if(targetType.isAssignableFrom(value.getClass())){
			
			return (K) value;
		}else {
			
			//throw exception
		}
		return null;
	}

	@Override 
	public Object getAttrValue(String attrName){
		
		return values.get(attrName);
	}
	
	@Override
	public void setAttrValue(String attrName, Object value) {
		
		values.put(attrName, value);
	}
	
}
