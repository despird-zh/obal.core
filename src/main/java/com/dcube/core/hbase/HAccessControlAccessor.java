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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.audit.AuditInfo;
import com.dcube.audit.Predicate;
import com.dcube.core.CoreConstants;
import com.dcube.core.EntryKey;
import com.dcube.core.accessor.AccessControlEntry;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityAccessor;
import com.dcube.core.security.AclConstants;
import com.dcube.core.security.AclConstants.PrivilegeEnum;
import com.dcube.core.security.AclConstants.TypeEnum;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;
import com.dcube.core.security.IAccessControlAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.exception.MetaException;
import com.dcube.exception.WrapperException;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

/**
 * @author despird
 * @version 0.1 2014-5-2
 * 
 * @see EntityAccessor
 **/
public abstract class HAccessControlAccessor<GB extends AccessControlEntry> extends HEntityAccessor<GB> implements IAccessControlAccessor<GB> {

	Logger LOGGER = LoggerFactory.getLogger(HAccessControlAccessor.class);
	
	/**
	 * Constructor with EntityAccessor name and context.
	 * @param accessorName the name of EntityAccessor
	 **/	
	public HAccessControlAccessor(String accessorName) {
		super(accessorName);
	}
	
	/**
	 * Constructor with EntityAccessor name and context.
	 * @param accessorName the name of EntityAccessor
	 * @param context the context during operation  
	 **/
	public HAccessControlAccessor(String accessorName,AccessorContext context) {
		super(accessorName,context);
	}

	@Override
	public GB doGetEntry(String entryKey) throws AccessorException {
		
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_GET_ENTRY);
		HTableInterface table = null;
		GB rtv = newEntryObject();
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());           
        	Result result = table.get(get);           
        	wrap(entitySchema.getEntityMeta().getAllAttrs(),result, rtv);
        	// extract the acl information
        	if(entitySchema.getEntityMeta().getAccessControllable()){
        		
        		EntryAcl acl = wrapEntryAcl(result);
        		((AccessControlEntry)rtv).setEntryAcl(acl);
        	}
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (WrapperException e) {
        	 throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		} catch (MetaException e) {
			throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_GET_ENTRY)
				.setTarget(entitySchema.getEntryKey(entryKey).toString());
			
			context.auditEnd();
        }
		return rtv;
	}
	
	@Override
	public GB doGetEntry(String entryKey, String... attributes)throws AccessorException{
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_GET_ENTRY);
		
		HTableInterface table = null;
		GB rtv = newEntryObject();
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {

        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
        	List<EntityAttr> attrs = entitySchema.getEntityMeta().getAttrs(attributes);
        	
        	Get get = new Get(entryKey.getBytes());
        	for(EntityAttr attr: attrs){
        		get.addColumn(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
        	}
        	Result result = table.get(get);
        	
        	wrap(attrs, result, rtv);
        	// extract the acl information
        	if(entitySchema.getEntityMeta().getAccessControllable()){
        		
        		EntryAcl acl = wrapEntryAcl(result);
        		((AccessControlEntry)rtv).setEntryAcl(acl);
        	}
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (WrapperException e) {
        	 throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		} catch (MetaException e) {
			throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}     
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_GET_ENTRY)
				.setTarget(entitySchema.getEntryKey(entryKey).toString());
			audit.addPredicate(AUDIT_OPER_GET_ATTR, Predicate.KEY_PARAM, attributes);
			context.auditEnd();
		}
		return rtv;
	}
	

	@Override
	public EntryKey doPutEntry(GB entryInfo, boolean changedOnly) throws AccessorException {
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_PUT_ENTRY);
		
		HTableInterface table = null;
		EntryKey rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
        	EntryKey key = entryInfo.getEntryKey();
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),key.getKey()));

            Put put = parse(entitySchema.getEntityMeta().getAllAttrs(),entryInfo);
        	// store the acl information
        	if(entitySchema.getEntityMeta().getAccessControllable()){
        		
        		EntryAcl acl = ((AccessControlEntry)entryInfo).getEntryAcl();
        		parseEntryAcl(put, acl);
        		
        	}
            table.put(put);
        	table.flushCommits();
        	rtv = entryInfo.getEntryKey();
        	
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryInfo.getEntryKey().toString());
        } catch (WrapperException e) {
        	throw new AccessorException("Error put entry row,key:{}",e,entryInfo.getEntryKey().toString());
		} catch (MetaException e) {
			throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryInfo.getEntryKey().toString());
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_PUT_ENTRY)
				.setTarget(entryInfo.getEntryKey().toString());
			Map<String,Object> predicates = entryInfo.getAuditPredicates();
			audit.addPredicates(AUDIT_OPER_PUT_ENTRY, predicates);
			context.auditEnd();
        }
		return rtv;
	}
	
	@Override
	public EntryAce getEntryAce(String entryKey, TypeEnum type, String name)throws AccessorException{

		HTableInterface table = null;
		EntryAce rtv = new EntryAce(type, name);
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());           
        	Result result = table.get(get);
        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(AclConstants.CF_ACL.getBytes());
    		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
    			
    			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), ":");
    			String value = Bytes.toString(entry.getValue());
    			
    			TypeEnum typeTemp = AclConstants.convertType(parts[0]);
    			if(parts.length == 2 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:group:001001 -> WRITE
    				//     CF | TYPE| KEY     Privilege
    				// here group:001001 is the qualifier name
    				PrivilegeEnum priv = PrivilegeEnum.valueOf(value);
    				rtv.setPrivilege(priv);
    				
    			}else if(parts.length == 3 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:group:001001:upgrade -> WRITE
    				//     CF | TYPE| KEY  | ACTION   Privilege
    				// here group:001001:upgrade is the qualifier name
    				rtv.grant(parts[2]);
    				
    			}

    		}
        	
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
		return rtv;
		
	}
	
	public EntryAcl getEntryAcl(String entryKey) throws AccessorException{

		HTableInterface table = null;
		EntryAcl rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());           
        	Result result = table.get(get);
        	
        	rtv = wrapEntryAcl(result);
        	
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
		return rtv;
	}
	
	public void grantPermission(String entryKey, TypeEnum type, String name, String ... permissions)throws AccessorException{

		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

            Put put = new Put(entryKey.getBytes());
    		byte[] cf = null;
    		for(String perm: permissions){
    			
    			if(perm == null) continue;
    			
    			cf = AclConstants.CF_ACL.getBytes();

    			String permQualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name
    					+ CoreConstants.KEYS_SEPARATOR
    					+ perm;
    			put.add(cf, permQualifier.getBytes(), EntityConstants.BLANK_VALUE.getBytes());
    			
    		}
            table.put(put);
        	table.flushCommits();
        	
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

        }

	}
	
	public void revokePermissions(String entryKey, TypeEnum type, String name, String ... permissions)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            List<Delete> list = new ArrayList<Delete>();
            Delete del = new Delete(entryKey.getBytes());
    		byte[] cf = null;
    		for(String perm: permissions){
    			
    			cf = AclConstants.CF_ACL.getBytes();

    			String permQualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name 
    					+ CoreConstants.KEYS_SEPARATOR
    					+ perm;
    			del.deleteColumns(cf, permQualifier.getBytes());
    			list.add(del);
    		}
            table.delete(list);
        	table.flushCommits();
        	
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

        }
	}
		
	public Set<String> getPermissions(String entryKey, TypeEnum type, String name)throws AccessorException{

		HTableInterface table = null;
		Set<String> rtv = new HashSet<String>();
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());   
        	get.addFamily(AclConstants.CF_ACL.getBytes());
        	Result result = table.get(get);
        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(AclConstants.CF_ACL.getBytes());
    		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
    			
    			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), ":");

    			TypeEnum typeTemp = TypeEnum.valueOf(parts[0]);
    			if(parts.length == 2 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:g:001001 -> WRITE
    				//     CF | TYPE| KEY     Privilege
    				// here g:001001 is the qualifier name
    				// ignore
    			}else if(parts.length == 3 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:g:001001:upgrade -> WRITE
    				//     CF | TYPE| KEY  | ACTION   Privilege
    				// here g:001001:upgrade is the qualifier name
    				rtv.add(parts[2]);
    				
    			}

    		}
        	
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
        
		return rtv;

	}
	
	public boolean promote(String entryKey, TypeEnum type, String name, PrivilegeEnum privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            Get get = new Get(entryKey.getBytes());
        	get.addFamily(AclConstants.CF_ACL.getBytes());
        	Result result = table.get(get);
        	String qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
        	byte[] cf = AclConstants.CF_ACL.getBytes();
        	Cell cell = result.getColumnLatestCell(cf, qualifier.getBytes());
        	String val = Bytes.toString(cell.getValueArray());
        	
        	PrivilegeEnum current = AclConstants.convertPrivilege(val);
        	
        	if(current.priority > privilege.priority)// 
        		return false;
        	
            Put put = new Put(entryKey.getBytes());
    		put.add(cf, qualifier.getBytes(), privilege.toString().getBytes());
    		
            table.put(put);
        	table.flushCommits();
        	return true;
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

        }	
	}
	
	public boolean demote(String entryKey, TypeEnum type, String name, PrivilegeEnum privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            Get get = new Get(entryKey.getBytes());
        	get.addFamily(AclConstants.CF_ACL.getBytes());
        	Result result = table.get(get);
        	String qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
        	byte[] cf = AclConstants.CF_ACL.getBytes();
        	Cell cell = result.getColumnLatestCell(cf, qualifier.getBytes());
        	String val = Bytes.toString(cell.getValueArray());
        	
        	PrivilegeEnum current = AclConstants.convertPrivilege(val);
        	
        	if(current.priority < privilege.priority)// 
        		return false;
        	
            Put put = new Put(entryKey.getBytes());
    		put.add(cf, qualifier.getBytes(), privilege.toString().getBytes());
    		
            table.put(put);
        	table.flushCommits();
        	return true;
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

        }
	}
	
	public void setPrivilege(String entryKey, TypeEnum type, String name, PrivilegeEnum privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

            Put put = new Put(entryKey.getBytes());
    		byte[] cf = null;

    		String qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
    		cf = AclConstants.CF_ACL.getBytes();

    		put.add(cf, qualifier.getBytes(), privilege.toString().getBytes());
            table.put(put);
        	table.flushCommits();
        	
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

        }
	}
	
	public PrivilegeEnum getPrivilege(String entryKey, TypeEnum type, String name)throws AccessorException{
		
		HTableInterface table = null;

		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());
        	get.addFamily(AclConstants.CF_ACL.getBytes());
        	Result result = table.get(get);
        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(AclConstants.CF_ACL.getBytes());
    		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
    			
    			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), ":");

    			TypeEnum typeTemp = TypeEnum.valueOf(parts[0]);
    			if(parts.length == 3 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:group:001001:upgrade -> WRITE
    				//     CF | TYPE| KEY  | ACTION   Privilege
    				// here group:001001:upgrade is the qualifier name
    				PrivilegeEnum priv = PrivilegeEnum.valueOf(parts[2]);
    				return priv;
    			}

    		}
        	
    		return null;
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (MetaException e) {
			throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
		
	}
	
	/**
	 * Wrap the Access control list information from acl column family.
	 * <pre>
	 * column family -> acl
	 * 
	 * qualifier -> owner             value: demouser
	 * qualifier -> u:                value: b          // owner basic privilege
	 * qualifier -> u:download        value: foo value  // owner extend business operation
	 * qualifier -> u:upload          value: foo value  // owner extend business operation
	 * 
	 * qualifier -> u:usr1:           value: b          // normal user privilege
	 * qualifier -> u:usr1:download   value: foo value  // normal user extend business operation
	 * 
	 * qualifier -> g:grp1:           value: r          // group privilege
	 * qualifier -> g:grp1:download   value: foo value  // group extend business operation
	 * qualifier -> g:grp1:upload     value: foo value  // group extend business operation
	 * 
	 * qualifier -> o:                value: b          // other basic privilege
	 * qualifier -> o:download        value: foo value  // other extend business operation
	 * qualifier -> o:upload          value: foo value  // other extend business operation
	 * </pre>
	 **/
	private EntryAcl wrapEntryAcl(Result rawEntry){
		
		NavigableMap<byte[], byte[]> acemap = rawEntry.getFamilyMap(AclConstants.CF_ACL.getBytes());
		EntryAcl acl = new EntryAcl();
		String owner = Bytes.toString(acemap.get(AclConstants.QL_OWNRER.getBytes()));
		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
			// if no keys separator ignore it.
			if(!Bytes.contains(entry.getKey(), CoreConstants.KEYS_SEPARATOR.getBytes()))
				continue;
			String qualifier = Bytes.toString(entry.getKey());
			// entry key is the qualifier, here we parse it into string array
			String[] parts = StringUtils.split( qualifier, CoreConstants.KEYS_SEPARATOR);// separator->[:]
			String value = Bytes.toString(entry.getValue());
			EntryAce ace = null;
			
			if(parts.length == 1){
				// only owner and other basic privilege match this
				PrivilegeEnum priv = AclConstants.convertPrivilege(value);
				TypeEnum type = AclConstants.convertType(parts[0]);
				if(type == TypeEnum.Other){
					ace = new EntryAce(type,TypeEnum.Other.name(),priv);
				}else{
					ace = new EntryAce(TypeEnum.Owner,owner,priv);
				}
			}
			else if(parts.length == 2){
				if(qualifier.endsWith(CoreConstants.KEYS_SEPARATOR)){
					// normal or group basic privilege ,eg. u:usr1: / g:grp1:
					PrivilegeEnum priv = AclConstants.convertPrivilege(value);
					TypeEnum type = AclConstants.convertType(parts[0]);
					ace = new EntryAce(type,parts[1],priv);
				}else{
					// owner or other extend permission ,eg. u:download / o:upload
					TypeEnum type = AclConstants.convertType(parts[0]);
					if(type == TypeEnum.Other){
						ace = new EntryAce(type,TypeEnum.Other.name(),parts[1]);
					}else{
						ace = new EntryAce(TypeEnum.Owner,owner,parts[1]);
					}
				}				
			}else if(parts.length == 3){
				// normal user and group extend permission
				// u:usr1:download
				// g:grp1:upload
				TypeEnum type = AclConstants.convertType(parts[0]);
				ace = new EntryAce(type,parts[1],parts[2]);
				
			}
			
			acl.addEntryAce(ace, true);
		}
		
		return acl;
	}
	
	/**
	 * Parse the acl information to Put  
	 **/
	private void parseEntryAcl(Put put,  EntryAcl acl){
		
		List<EntryAce> aces = acl.getAllAces();
		byte[] cf = null;
		byte[] enable = "enable".getBytes();
		for(EntryAce ace: aces){
			
			String qualifier = ace.getType().abbr
					+ CoreConstants.KEYS_SEPARATOR;
			cf = AclConstants.CF_ACL.getBytes();
			if(TypeEnum.Owner == ace.getType()){
				// owner
				put.add(cf, qualifier.getBytes(), ace.getPrivilege().toString().getBytes());
				// save owner name
				put.add(cf, AclConstants.QL_OWNRER.getBytes(), ace.getName().getBytes()); 
			}else if(TypeEnum.Other == ace.getType()){
				// owner
				put.add(cf, qualifier.getBytes(), ace.getPrivilege().toString().getBytes());				
			}else {
				// normal user and group
				qualifier += ace.getName();
				put.add(cf, qualifier.getBytes(), ace.getPrivilege().toString().getBytes());
			}
			
			Set<String> permissionSet = ace.getPermissions();
			for(String permission : permissionSet){
				
				String permQualifier = qualifier 
						+ (qualifier.endsWith(CoreConstants.KEYS_SEPARATOR)? "":CoreConstants.KEYS_SEPARATOR)
						+ permission;
				put.add(cf, permQualifier.getBytes(), enable);
			}
		}
	}
	
	/** test only */
	public static void main(String[] arg){
		
		String[] r = StringUtils.split("u", ":");
		for(int i = 0; i< r.length; i++)
			System.out.println(i+" = "+ r[i]);
		
		String[] r1 = StringUtils.split("u:m", ":");
		for(int i = 0; i< r1.length; i++)
			System.out.println(i+" = "+ r1[i]);
		
		String[] r2 = StringUtils.split("u:m:", ":");
		for(int i = 0; i< r2.length; i++)
			System.out.println(i+" = "+ r2[i]);
	}
}
