package com.obal.core.hbase;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import com.obal.core.ITraceable;
import com.obal.core.accessor.EntryInfo;
import com.obal.core.accessor.TraceableEntry;
import com.obal.core.security.Principal;
import com.obal.exception.WrapperException;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityConstants;
import com.obal.meta.EntityManager;

public class HTraceableEntryWrapper extends HEntryWrapper<TraceableEntry>{

	@Override
	public TraceableEntry wrap(List<EntityAttr> attrs, Result rawEntry)
			throws WrapperException {
		
		Result entry = (Result)rawEntry;
		String entityName = attrs.size()>0? (attrs.get(0).getEntityName()):EntityConstants.ENTITY_BLIND;
		if(entityName == null || entityName.length()==0){
			
			entityName = EntityConstants.ENTITY_BLIND;
		}
		TraceableEntry gei = new TraceableEntry(entityName,new String(entry.getRow()));
		
		for(EntityAttr attr: attrs){
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = entry.getValue(column, qualifier);
			switch(attr.mode){
			
				case PRIMITIVE :
				
					Object value = super.getPrimitiveValue(attr, cell);
					gei.setAttrValue(attr.getAttrName(), value);	
					break;
					
				case MAP :
					
					Map<String, Object> map = super.getMapValue(attr, cell);				
					gei.setAttrValue(attr.getAttrName(), map);
					break;
					
				case LIST :
					
					List<Object> list = super.getListValue(attr, cell);					
					gei.setAttrValue(attr.getAttrName(), list);
					break;
					
				case SET :
					
					Set<Object> set = super.getSetValue(attr, cell);					
					gei.setAttrValue(attr.getAttrName(), set);
					break;
					
				default:
					break;
				
			}
			
		}
		
		wrapTraceable(gei, rawEntry);
		
		return gei;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Put parse(List<EntityAttr> attrs, TraceableEntry entryInfo)
			throws WrapperException {
		
		byte[] keybytes = entryInfo.getEntryKey().getKeyBytes();
		if(keybytes == null)
			throw new WrapperException("The entrykey's cannot be null");
		
		Put put = new Put(entryInfo.getEntryKey().getKey().getBytes());

        for(EntityAttr attr:attrs){

        	Object value = entryInfo.getAttrValue(attr.getAttrName());
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("-=>parsing attr:{} - value:{}",attr.getAttrName(),value);
        	}
        	if(null == value) continue;
        	
        	switch(attr.mode){
        	
        		case PRIMITIVE:
        			super.putPrimitiveValue(put, attr, value);					
        			break;
        		case MAP:
        			super.putMapValue(put, attr, (Map<String,Object>)value);	
        			break;
        		case LIST:
        			super.putListValue(put, attr, (List<Object>)value);	
        			
        			break;
        		case SET:
        			super.putSetValue(put, attr, (Set<Object>)value);				
        			break;
        		default:
        			break;
        	
        	}
        }
        
        super.parseTraceable(put, (ITraceable)entryInfo);
        return put;
	}

	@Override
	public TraceableEntry wrap(String entityName, Result rawEntry)
			throws WrapperException {
		
		List<EntityAttr> attrs = EntityManager.getInstance().getEntityMeta(entityName).getAllAttrs();
		return wrap(attrs, rawEntry);
	}

}
