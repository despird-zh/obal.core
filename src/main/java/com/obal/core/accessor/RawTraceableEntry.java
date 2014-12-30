package com.obal.core.accessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.obal.core.EntryKey;

public class RawTraceableEntry extends TraceableEntry{

	private static final long serialVersionUID = 1L;

	Map<String, Object> kvmap = null;
	
	public RawTraceableEntry(EntryKey key) {
		super(key);
		// TODO Auto-generated constructor stub
	}
	/**
	 * constructor
	 * @param entityName the entry name
	 * @param key the entry key 
	 **/
	public RawTraceableEntry(String entityName, String key) {
		super(entityName, key);
	}
	
	
	/**
	 * store key-value pair
	 * 
	 * @param key the entry attribute name
	 * @param value the entry attribute value
	 *  
	 **/
	public void put(String key,Object value){
		
		if(kvmap == null){
			
			kvmap = new HashMap<String,Object>();			
		}
		kvmap.put(key, value);
	}
	
	/**
	 * get value by key
	 * 
	 * @param key
	 * 
	 * @return object value
	 **/
	public Object get(String key){
		
		return kvmap == null? null:kvmap.get(key);
	}
	
	/**
	 * get key set
	 * 
	 * @return set of key 
	 **/
	public Set<String> getKeySet(){
		
		return kvmap.keySet();
	}
}
