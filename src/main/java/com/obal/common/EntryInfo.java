package com.obal.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.obal.core.EntryKey;
import com.obal.meta.EntityAttr;

public class EntryInfo implements IEntryInfo{

	private EntryKey entryKey = null;
	private Map<String, Object> values = null;
	private Map<String, EntityAttr> attrs = null;
	
	public EntryInfo (){
		
		values = new HashMap<String,Object> ();
		attrs = new HashMap<String, EntityAttr> ();
	}
	
	public EntryInfo(List<EntityAttr> attrs){
		
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
	public EntryKey getEntryKey() {
		
		return this.entryKey;
	}

	@Override
	public void setEntryKey(EntryKey entryKey) {
		
		this.entryKey = entryKey;
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
	public void setAttrValue(String attrName, Object value) {
		
		values.put(attrName, value);
	}
	
}
