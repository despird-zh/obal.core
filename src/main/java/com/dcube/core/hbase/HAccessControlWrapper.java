package com.dcube.core.hbase;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.CoreConstants;
import com.dcube.core.accessor.AccessControlEntry;
import com.dcube.core.security.AclPrivilege;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAce.AceType;
import com.dcube.core.security.EntryAcl;
import com.dcube.exception.WrapperException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

/**
 * HAccessControlWrapper convert the byte[] data into EntryInfo object.
 * Specially read/write the EntryAcl information
 * 
 * @author despird-zh
 * @version 0.1 2015-3-25
 * 
 **/
public class HAccessControlWrapper extends HEntryWrapper<AccessControlEntry>{

	public static Logger LOGGER = LoggerFactory.getLogger(HAccessControlWrapper.class);
	
	@Override
	public AccessControlEntry wrap(final List<EntityAttr> attrs, Result rawEntry)
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
		
		EntryAcl acl = wrapEntryAcl(rawEntry);
		gei.setEntryAcl(acl);
		
		return gei;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Put parse(final List<EntityAttr> attrs, AccessControlEntry entryInfo)
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

	/**
	 * Wrap the Acl information from acl column family.
	 * 
	 **/
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
				AceType type = AceType.valueOf(parts[0]);
				ace = new EntryAce(type,parts[1],priv);
				
			}else if(parts.length == 3){
				// eg. acl:group:001001:upgrade -> WRITE
				//     CF | TYPE| KEY  | ACTION   Privilege
				// here group:001001:upgrade is the qualifier name
				AceType type = AceType.valueOf(parts[0]);
				ace = new EntryAce(type,parts[1],parts[2]);
				
			}
			
			acl.addEntryAce(ace, true);
		}
		
		return null;
	}
	
	/**
	 * Parse the acl information to Put
	 *  
	 **/
	public void wrapEntryAcl(Put put,  EntryAcl acl){
		
		List<EntryAce> aces = acl.getAllAces();
		byte[] cf = null;
		byte[] enable = "enable".getBytes();
		for(EntryAce ace: aces){
			
			String qualifier = ace.getType().qualifier
					+ CoreConstants.KEYS_SEPARATOR
					+ ace.getName();
			cf = ace.getType().colfamily.getBytes();
			put.add(cf, qualifier.getBytes(), ace.getPrivilege().toString().getBytes());
			
			Set<String> permissionSet = ace.getPermissions();
			for(String permission : permissionSet){
				
				String permQualifier = qualifier 
						+ CoreConstants.KEYS_SEPARATOR
						+ permission;
				put.add(cf, permQualifier.getBytes(), enable);
			}
		}
	}
}
