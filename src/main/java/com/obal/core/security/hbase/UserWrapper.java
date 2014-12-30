package com.obal.core.security.hbase;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import com.obal.core.ITraceable;
import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.security.Principal;
import com.obal.exception.WrapperException;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityManager;

public class UserWrapper extends HEntryWrapper<Principal>{

	@Override
	public Principal wrap(List<EntityAttr> attrs, Result rawEntry)
			throws WrapperException {
		
		Result entry = (Result)rawEntry;
				
		Principal princ = new Principal(new String(entry.getRow()));
		String val = null;
		for(EntityAttr attr: attrs){
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = entry.getValue(column, qualifier);
			if("i_account".equals(attr.getAttrName())){
				val = (String)super.getPrimitiveValue(attr, cell);
				princ.setAccount(val);
				continue;
			}else if("i_name".equals(attr.getAttrName())){
				
				val = (String)super.getPrimitiveValue(attr, cell);
				princ.setName(val);
				continue;
			}else if("i_source".equals(attr.getAttrName())){
				
				val = (String)super.getPrimitiveValue(attr, cell);
				princ.setSource(val);
				continue;
			}else if("i_password".equals(attr.getAttrName())){
				
				val = (String)super.getPrimitiveValue(attr, cell);
				princ.setPassword(val);
				continue;
			}else if("i_groups".equals(attr.getAttrName())){
				
				Map<String,Object> groups = (Map<String,Object>)super.getMapValue(attr, cell);
				princ.setGroups(groups);
				continue;
			}else if("i_roles".equals(attr.getAttrName())){
				
				Map<String,Object> roles = (Map<String,Object>)super.getMapValue(attr, cell);
				princ.setGroups(roles);
				continue;
			}			
		}
		
		wrapTraceable(princ, rawEntry);
		return princ;
	}

	@Override
	public Put parse(List<EntityAttr> attrs, Principal entryInfo)
			throws WrapperException {
		byte[] keybytes = entryInfo.getKeyBytes();
		if(keybytes == null)
			throw new WrapperException("The entrykey's cannot be null");
		
		Put put = new Put(entryInfo.getKeyBytes());

        for(EntityAttr attr:attrs){
			Object val = null;
			if("i_account".equals(attr.getAttrName())){
				val = entryInfo.getAccount();
				super.putPrimitiveValue(put, attr, val);
				continue;
			}else if("i_name".equals(attr.getAttrName())){
				
				val = entryInfo.getName();
				super.putPrimitiveValue(put, attr, val);
				continue;
			}else if("i_source".equals(attr.getAttrName())){
				
				val = entryInfo.getSource();
				super.putPrimitiveValue(put, attr, val);
				continue;
			}else if("i_password".equals(attr.getAttrName())){
				
				val = entryInfo.getPassword();
				super.putPrimitiveValue(put, attr, val);
				continue;
			}else if("i_groups".equals(attr.getAttrName())){
				
				Map<String,Object> mapval = entryInfo.getGroups();
				super.putMapValue(put, attr, mapval);
				continue;
			}else if("i_roles".equals(attr.getAttrName())){
				
				Map<String,Object> mapval = entryInfo.getRoles();
				super.putMapValue(put, attr, mapval);
				continue;
			}
        	
        }
        
        super.parseTraceable(put, (ITraceable)entryInfo);
        return put;
	}

	@Override
	public Principal wrap(String entityName, Result rawEntry)
			throws WrapperException {
		
		List<EntityAttr> attrs = EntityManager.getInstance().getEntityMeta(entityName).getAllAttrs();
		return wrap(attrs, rawEntry);
	}

}
