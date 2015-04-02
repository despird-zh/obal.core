package com.dcube.core.hbase;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.accessor.TraceableEntry;
import com.dcube.exception.WrapperException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

public class HTraceableEntryWrapper extends HEntryWrapper<TraceableEntry>{

	public static Logger LOGGER = LoggerFactory.getLogger(HTraceableEntryWrapper.class);
	
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
				
					Object value =(cell== null)? null: HEntryWrapperUtils.getPrimitiveValue(attr, cell);
					gei.setAttrValue(attr, value);	
					break;
					
				case JMAP :
					
					Map<String, Object> map = (cell== null)? null: HEntryWrapperUtils.getJMapValue(attr, cell);				
					gei.setAttrValue(attr, map);
					break;
					
				case JLIST :
					
					List<Object> list = (cell== null)? null: HEntryWrapperUtils.getJListValue(attr, cell);					
					gei.setAttrValue(attr, list);
					break;
					
				case JSET :
					
					Set<Object> set = (cell== null)? null: HEntryWrapperUtils.getJSetValue(attr, cell);					
					gei.setAttrValue(attr, set);
					break;
					
				default:
					break;
				
			}
			
		}

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
        		LOGGER.debug("Put -> attribute:{} - value:{}",attr.getAttrName(),value);
        	}
        	if(null == value) continue;
        	
        	switch(attr.mode){
        	
        		case PRIMITIVE:
        			HEntryWrapperUtils.putPrimitiveValue(put, attr, value);					
        			break;
        		case JMAP:
        			HEntryWrapperUtils.putJMapValue(put, attr, (Map<String,Object>)value);	
        			break;
        		case JLIST:
        			HEntryWrapperUtils.putJListValue(put, attr, (List<Object>)value);	
        			
        			break;
        		case JSET:
        			HEntryWrapperUtils.putJSetValue(put, attr, (Set<Object>)value);				
        			break;
        		default:
        			break;
        	
        	}
        }

        return put;
	}

}
