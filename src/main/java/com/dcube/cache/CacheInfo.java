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
package com.dcube.cache;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.dcube.core.accessor.EntityEntry;
import com.dcube.core.security.Principal;
import com.dcube.disruptor.EventPayload;

/**
 * CacheEvent is the placeholder element in disruptor RingBuffer, it holds the entry information
 * the entry will be processed by handler of disruptor.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * @since 0.1
 **/
public class CacheInfo{
	
	public enum OperEnum{
		
		PutEntry,
		PutAttr,
		DelEntry,
		DelAttr;
	}

	public CacheInfo(Principal principal){
		this.principal = principal;
	}
	
	/** the principal info */
	private Principal principal = null;	
	
	/** operation type */
	private OperEnum operation = OperEnum.PutEntry;

	/** the value */
	private Object value;
		
	/**
	 * Get the value of different events.
	 *  
	 **/
	@SuppressWarnings("unchecked")
	public <K> K value(){
		
		return (K)this.value;
	}
    
	/**
	 * Set the setting of entry attribute put operation.
	 *  
	 **/
	public void setPutAttrData(String key,String attr,Object value){
		
		PutAttrData putattr = new PutAttrData();
		putattr.key = key;
		putattr.attr = attr;
		putattr.value = value;
		
		this.value = putattr;
		this.operation = OperEnum.PutAttr;
	}

	/**
	 * Set the setting of entry attribute put operation.
	 *  
	 **/
	public void setPutEntryData(EntityEntry entryInfo){
		
		PutEntryData putentry = new PutEntryData();
		putentry.entryInfo = entryInfo;
		
		this.value= putentry;
		this.operation = OperEnum.PutEntry;
	}

	/**
	 * Set the setting of entry delete operation.
	 *  
	 **/
	public void setDelEntryData(String key){
		
		DelEntryData delentry = new DelEntryData();
		delentry.key = key;
		this.operation = OperEnum.DelEntry;
	}
	
	/**
	 * Set the setting of entry attribute delete operation.
	 *  
	 **/
	public void setDelAttrData(String key, String attribute){
		
		DelAttrData delattr = new DelAttrData();
		delattr.key = key;
		delattr.attr = attribute;
		
		this.operation = OperEnum.DelAttr;
	}
	
    /**
     * The cache info operation 
     **/
    public OperEnum operation(){

    	return this.operation;
    }
	
    public Principal getPrincipal(){
    	
    	return this.principal;
    }
    
    /**
     * Class to wrap entry attribute data 
     **/
    public static class PutAttrData{
    	
    	public String key = null;
    	public String attr = null;
    	public Object value = null;
    }
    
    /**
     * Class to wrap delete entry data 
     **/
    public static class DelEntryData{
    	
    	public String key = null;
    }
    
    /**
     * Class to wrap delete entry data 
     **/
    public static class DelAttrData{
    	
    	public String key = null;
    	public String attr = null;
    }
    
    /**
     * Class to wrap put entry data 
     **/
    public static class PutEntryData{
    	
    	public EntityEntry entryInfo = null;
    }
    
}	
