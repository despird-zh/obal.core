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
import com.dcube.core.security.AclConstants.AcePrivilege;
import com.dcube.core.security.AclConstants.AceType;
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
 * HAccessControlAccessor provides methods to get/set the access control data, 
 * the access control will be store in [acl] column family with following format
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
 * 
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
	
	/**
	 * Get the entry ace of specified subject(user or group).
	 * @param key the entry key
	 * @param etype the type of expected ace
	 * @param name the subject of ace
	 * @return EntryAce 
	 **/
	@Override
	public EntryAce getEntryAce(String entryKey, AceType etype, String name)throws AccessorException{

		HTableInterface table = null;
		EntryAce rtv = new EntryAce(etype, name);
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());    
        	get.addFamily(AclConstants.CF_ACL.getBytes());
        	Result result = table.get(get);        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(AclConstants.CF_ACL.getBytes());
    		String owner = Bytes.toString(acemap.get(AclConstants.QL_OWNRER.getBytes()));
    		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
    		
    			// if no keys separator ignore it.
    			if(!Bytes.contains(entry.getKey(), CoreConstants.KEYS_SEPARATOR.getBytes()))
    				continue;
    			String qualifier = Bytes.toString(entry.getKey());
    			// entry key is the qualifier, here we parse it into string array
    			String[] parts = StringUtils.split( qualifier, CoreConstants.KEYS_SEPARATOR);// separator->[:]
    			String value = Bytes.toString(entry.getValue());
    			
    			if(parts.length == 1 && (etype == AceType.Owner || etype == AceType.Other )){
    				// only owner and other basic privilege match this
    				AcePrivilege priv = AclConstants.convertPrivilege(value);
    				AceType type = AclConstants.convertType(parts[0]);
    				if(type != AceType.Other){
    					
    					rtv.setName(owner);
    				}
    				rtv.setPrivilege(priv);
    			}
    			else if(parts.length == 2){
    				if(qualifier.endsWith(CoreConstants.KEYS_SEPARATOR) && (etype == AceType.User || etype == AceType.Group )){
    					// normal or group basic privilege ,eg. u:usr1: / g:grp1:
    					AcePrivilege priv = AclConstants.convertPrivilege(value);
    					rtv.setPrivilege(priv);
    				}else if(etype == AceType.Owner || etype == AceType.Other ){
    					// owner or other extend permission ,eg. u:download / o:upload    					
    					rtv.grant(parts[1]);
    				}				
    			}else if(parts.length == 3){
    				// normal user and group extend permission
    				// u:usr1:download
    				// g:grp1:upload
    				rtv.grant(parts[1]);    				
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
	
	/**
	 * Get the entry acl
	 * @param key the entry key
	 * @return EntryAcl 
	 **/
	public EntryAcl getEntryAcl(String entryKey) throws AccessorException{

		HTableInterface table = null;
		EntryAcl rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
        	Get get = new Get(entryKey.getBytes());
	        get.addFamily(AclConstants.CF_ACL.getBytes());
        	Result result = table.get(get);
        	// convert result into Acl
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
	
	/**
	 * Grant business permission to specified subject on entry
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param permissions 
	 **/
	public void grantPermission(String entryKey, AceType type, String name, String ... permissions)throws AccessorException{

		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

            Put put = new Put(entryKey.getBytes());
    		byte[] cf = null;
    		for(String perm: permissions){
    			
    			if(perm == null) continue;
    			
    			cf = AclConstants.CF_ACL.getBytes();
    			String permQualifier = null;
    			if(type == AceType.Owner || type == AceType.Other){
    				//owner or other extend permission ,eg. u:download / o:upload    
    				permQualifier = type.abbr
        					+ CoreConstants.KEYS_SEPARATOR
        					+ perm;
    			}else{
    				// normal user and group extend permission
    				// u:usr1:download
    				// g:grp1:upload
    				permQualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name
    					+ CoreConstants.KEYS_SEPARATOR
    					+ perm;
    			}
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
	
	/**
	 * Revoke the permission from specified subject on entry
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param permissions 
	 **/
	public void revokePermissions(String entryKey, AceType type, String name, String ... permissions)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            List<Delete> list = new ArrayList<Delete>();
            Delete del = new Delete(entryKey.getBytes());
    		byte[] cf = null;
    		for(String perm: permissions){
    			
    			cf = AclConstants.CF_ACL.getBytes();

    			String permQualifier = null;
    			if(type == AceType.Owner || type == AceType.Other){
    				//owner or other extend permission ,eg. u:download / o:upload    
    				permQualifier = type.abbr
        					+ CoreConstants.KEYS_SEPARATOR
        					+ perm;
    			}else{
    				// normal user and group extend permission
    				// u:usr1:download
    				// g:grp1:upload
    				permQualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name
    					+ CoreConstants.KEYS_SEPARATOR
    					+ perm;
    			}
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
	
	/**
	 * Get the permissions of specified subject on entry
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 **/
	public Set<String> getPermissions(String entryKey, AceType type, String name)throws AccessorException{

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
    			
    			// if no keys separator ignore it.
    			if(!Bytes.contains(entry.getKey(), CoreConstants.KEYS_SEPARATOR.getBytes()))
    				continue;
    			
    			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), CoreConstants.KEYS_SEPARATOR);
    			String qualifier = Bytes.toString(entry.getKey());
    			AceType typeTemp = AceType.valueOf(parts[0]);
    			if(parts.length == 2 && !qualifier.endsWith(CoreConstants.KEYS_SEPARATOR)){
    				//owner or other extend permission ,eg. u:download / o:upload   
    				if(type == AceType.Owner && typeTemp == AceType.User){
    					rtv.add(parts[1]);
    				}
    				if(typeTemp == AceType.Other){
    					rtv.add(parts[1]);
    				}    				
    			}else if(parts.length == 3 && !qualifier.endsWith(CoreConstants.KEYS_SEPARATOR)){
    				// normal user and group extend permission
    				// u:usr1:download
    				// g:grp1:upload
    				if(typeTemp == AceType.Group && parts[1].equals(name))
    					rtv.add(parts[2]);   
    				if(typeTemp == AceType.User && parts[1].equals(name))
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
	
	/**
	 * Promote the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param privilege the privilege none/browse/read/write/delete
	 **/
	public boolean promote(String entryKey, AceType type, String name, AcePrivilege privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            Get get = new Get(entryKey.getBytes());
            byte[] cf = AclConstants.CF_ACL.getBytes();
        	get.addFamily(cf);
        	Result result = table.get(get);
        	String qualifier ;
        	if(type == AceType.Other || type == AceType.Owner){
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR;
        	}else{
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name
    					+ CoreConstants.KEYS_SEPARATOR;
        	}
        	
        	Cell cell = result.getColumnLatestCell(cf, qualifier.getBytes());
        	String val = Bytes.toString(cell.getValueArray());
        	
        	AcePrivilege current = AclConstants.convertPrivilege(val);
        	
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
	
	/**
	 * Demote the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param privilege the privilege none/browse/read/write/delete
	 **/
	public boolean demote(String entryKey, AceType type, String name, AcePrivilege privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            Get get = new Get(entryKey.getBytes());
            byte[] cf = AclConstants.CF_ACL.getBytes();
        	get.addFamily(cf);
        	Result result = table.get(get);
        	String qualifier ;
        	if(type == AceType.Other || type == AceType.Owner){
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR;
        	}else{
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name
    					+ CoreConstants.KEYS_SEPARATOR;
        	}
        	
        	Cell cell = result.getColumnLatestCell(cf, qualifier.getBytes());
        	String val = Bytes.toString(cell.getValueArray());
        	
        	AcePrivilege current = AclConstants.convertPrivilege(val);
        	
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
	
	/**
	 * Set the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 * @param privilege the privilege none/browse/read/write/delete
	 **/
	public void setPrivilege(String entryKey, AceType type, String name, AcePrivilege privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

            Put put = new Put(entryKey.getBytes());
    		byte[] cf = null;
    		
        	String qualifier ;
        	if(type == AceType.Other || type == AceType.Owner){
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR;
        	}else{
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name
    					+ CoreConstants.KEYS_SEPARATOR;
        	}
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
	
	/**
	 * Get the privilege
	 * @param key the entry key
	 * @param type the type of ace
	 * @param name the subject name
	 **/
	public AcePrivilege getPrivilege(String entryKey, AceType type, String name)throws AccessorException{
		
		HTableInterface table = null;

		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());
        	get.addFamily(AclConstants.CF_ACL.getBytes());
        	Result result = table.get(get);
        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(AclConstants.CF_ACL.getBytes());
        	String qualifier ;
        	if(type == AceType.Other || type == AceType.Owner){
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR;
        	}else{
        		qualifier = type.abbr
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name
    					+ CoreConstants.KEYS_SEPARATOR;
        	}
        	byte[] bprivVal = acemap.get(qualifier.getBytes());
        	        	
    		return AclConstants.convertPrivilege(new String(bprivVal));
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
				AcePrivilege priv = AclConstants.convertPrivilege(value);
				AceType type = AclConstants.convertType(parts[0]);
				if(type == AceType.Other){
					ace = new EntryAce(type,AceType.Other.name(),priv);
				}else{
					ace = new EntryAce(AceType.Owner,owner,priv);
				}
			}
			else if(parts.length == 2){
				if(qualifier.endsWith(CoreConstants.KEYS_SEPARATOR)){
					// normal or group basic privilege ,eg. u:usr1: / g:grp1:
					AcePrivilege priv = AclConstants.convertPrivilege(value);
					AceType type = AclConstants.convertType(parts[0]);
					ace = new EntryAce(type,parts[1],priv);
				}else{
					// owner or other extend permission ,eg. u:download / o:upload
					AceType type = AclConstants.convertType(parts[0]);
					if(type == AceType.Other){
						ace = new EntryAce(type,AceType.Other.name(),parts[1]);
					}else{
						ace = new EntryAce(AceType.Owner,owner,parts[1]);
					}
				}				
			}else if(parts.length == 3){
				// normal user and group extend permission
				// u:usr1:download
				// g:grp1:upload
				AceType type = AclConstants.convertType(parts[0]);
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
			if(AceType.Owner == ace.getType()){
				// owner
				put.add(cf, qualifier.getBytes(), ace.getPrivilege().toString().getBytes());
				// save owner name
				put.add(cf, AclConstants.QL_OWNRER.getBytes(), ace.getName().getBytes()); 
			}else if(AceType.Other == ace.getType()){
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
