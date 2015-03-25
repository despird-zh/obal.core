package com.dcube.core.hbase;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.dcube.core.accessor.AccessControlEntry;
import com.dcube.core.security.AclPrivilege;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;
import com.dcube.exception.WrapperException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

public class HAccessControlWrapper extends HEntryWrapper<AccessControlEntry>{

	@Override
	public AccessControlEntry wrap(List<EntityAttr> attrs, Result rawEntry)
			throws WrapperException {
		
		Result entry = (Result)rawEntry;
		String entityName = attrs.size()>0? (attrs.get(0).getEntityName()):EntityConstants.ENTITY_BLIND;
		if(entityName == null || entityName.length()==0){
			
			entityName = EntityConstants.ENTITY_BLIND;
		}
		AccessControlEntry gei = new AccessControlEntry(entityName,new String(entry.getRow()));
		
		for(EntityAttr attr: attrs){
			
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = entry.getValue(column, qualifier);
			
			switch(attr.mode){
			
				case PRIMITIVE :
				
					Object value =(cell== null)? null: super.getPrimitiveValue(attr, cell);
					gei.setAttrValue(attr, value);	
					break;
					
				case MAP :
					
					Map<String, Object> map = (cell== null)? null: super.getMapValue(attr, cell);				
					gei.setAttrValue(attr, map);
					break;
					
				case LIST :
					
					List<Object> list = (cell== null)? null: super.getListValue(attr, cell);					
					gei.setAttrValue(attr, list);
					break;
					
				case SET :
					
					Set<Object> set = (cell== null)? null: super.getSetValue(attr, cell);					
					gei.setAttrValue(attr, set);
					break;
					
				default:
					break;
				
			}
			
		}
		
		EntryAcl acl = wrapEntryAcl(rawEntry);
		gei.setEntryAcl(acl);
		
		return gei;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Put parse(List<EntityAttr> attrs, AccessControlEntry entryInfo)
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

        return put;
	}

	public EntryAcl wrapEntryAcl(Result rawEntry){
		
		NavigableMap<byte[], byte[]> acemap = rawEntry.getFamilyMap(EntityConstants.ATTR_ACL_COLUMN.getBytes());
		EntryAcl acl = new EntryAcl();
		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
			
			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), ":");
			String value = Bytes.toString(entry.getValue());
			EntryAce ace = null;
			if(parts.length == 2){
				// eg. acl:group:001001 -> WRITE
				//     CF | TYPE| KEY     Privilege
				// here group:001001 is the qualifier name
				AclPrivilege priv = AclPrivilege.valueOf(value);
				ace = new EntryAce(parts[0],parts[1],priv);
				
			}else if(parts.length == 3){
				// eg. acl:group:001001:upgrade -> WRITE
				//     CF | TYPE| KEY  | ACTION   Privilege
				// here group:001001:upgrade is the qualifier name
				ace = new EntryAce(parts[0],parts[1],parts[2]);
				
			}
			
			acl.addEntryAce(ace, true);
		}

		
		return null;
	}
}
