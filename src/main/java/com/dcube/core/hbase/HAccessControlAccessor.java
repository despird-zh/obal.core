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
import com.dcube.core.security.AclPrivilege;
import com.dcube.core.security.EntryAce;
import com.dcube.core.security.EntryAcl;
import com.dcube.core.security.IAccessControlAccessor;
import com.dcube.core.security.EntryAce.AceType;
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
				.setTarget(entitySchema.getKey(entryKey).toString());
			
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
				.setTarget(entitySchema.getKey(entryKey).toString());
			audit.addPredicate(AUDIT_OPER_GET_ATTR, Predicate.KEY_PARAM, attributes);
			context.auditEnd();
		}
		return rtv;
	}
	

	@Override
	public EntryKey doPutEntry(GB entryInfo) throws AccessorException {
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
	public EntryAce getEntryAce(String entryKey, EntryAce.AceType type, String name)throws AccessorException{

		HTableInterface table = null;
		EntryAce rtv = new EntryAce(type, name);
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());           
        	Result result = table.get(get);
        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(EntityConstants.ATTR_ACL_COLUMN.getBytes());
    		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
    			
    			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), ":");
    			String value = Bytes.toString(entry.getValue());
    			
    			AceType typeTemp = AceType.valueOf(parts[0]);
    			if(parts.length == 2 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:group:001001 -> WRITE
    				//     CF | TYPE| KEY     Privilege
    				// here group:001001 is the qualifier name
    				AclPrivilege priv = AclPrivilege.valueOf(value);
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
	
	public void grantPermissions(String entryKey, EntryAce.AceType type, String name, String ... permissions)throws AccessorException{

		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

            Put put = new Put(entryKey.getBytes());
    		byte[] cf = null;
    		byte[] enable = "enable".getBytes();
    		for(String perm: permissions){
    			
    			String qualifier = type.qualifier
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
    			cf = type.colfamily.getBytes();

    			String permQualifier = qualifier 
    					+ CoreConstants.KEYS_SEPARATOR
    					+ perm;
    			put.add(cf, permQualifier.getBytes(), enable);
    			
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
	
	public void revokePermissions(String entryKey, EntryAce.AceType type, String name, String ... permissions)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            List<Delete> list = new ArrayList<Delete>();
            Delete del = new Delete(entryKey.getBytes());
    		byte[] cf = null;
    		for(String perm: permissions){
    			
    			String qualifier = type.qualifier
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
    			cf = type.colfamily.getBytes();

    			String permQualifier = qualifier 
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
		
	public Set<String> getPermissions(String entryKey, EntryAce.AceType type, String name)throws AccessorException{

		HTableInterface table = null;
		Set<String> rtv = new HashSet<String>();
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());           
        	Result result = table.get(get);
        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(EntityConstants.ATTR_ACL_COLUMN.getBytes());
    		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
    			
    			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), ":");

    			AceType typeTemp = AceType.valueOf(parts[0]);
    			if(parts.length == 2 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:group:001001 -> WRITE
    				//     CF | TYPE| KEY     Privilege
    				// here group:001001 is the qualifier name
    				// ignore
    			}else if(parts.length == 3 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:group:001001:upgrade -> WRITE
    				//     CF | TYPE| KEY  | ACTION   Privilege
    				// here group:001001:upgrade is the qualifier name
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
	
	public boolean promote(String entryKey, EntryAce.AceType type, String name, AclPrivilege privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            Get get = new Get(entryKey.getBytes());
        	get.addFamily(EntityConstants.ATTR_ACL_COLUMN.getBytes());
        	Result result = table.get(get);
        	String qualifier = type.qualifier
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
        	byte[] cf = EntityConstants.ATTR_ACL_COLUMN.getBytes();
        	Cell cell = result.getColumnLatestCell(cf, qualifier.getBytes());
        	String val = Bytes.toString(cell.getValueArray());
        	
        	AclPrivilege current = AclPrivilege.valueOf(val);
        	
        	if(current.priority() > privilege.priority())// 
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
	
	public boolean demote(String entryKey, EntryAce.AceType type, String name, AclPrivilege privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            Get get = new Get(entryKey.getBytes());
        	get.addFamily(EntityConstants.ATTR_ACL_COLUMN.getBytes());
        	Result result = table.get(get);
        	String qualifier = type.qualifier
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
        	byte[] cf = EntityConstants.ATTR_ACL_COLUMN.getBytes();
        	Cell cell = result.getColumnLatestCell(cf, qualifier.getBytes());
        	String val = Bytes.toString(cell.getValueArray());
        	
        	AclPrivilege current = AclPrivilege.valueOf(val);
        	
        	if(current.priority() < privilege.priority())// 
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
	
	public void setPrivilege(String entryKey, EntryAce.AceType type, String name, AclPrivilege privilege)throws AccessorException{
		HTableInterface table = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

            Put put = new Put(entryKey.getBytes());
    		byte[] cf = null;

    		String qualifier = type.qualifier
    					+ CoreConstants.KEYS_SEPARATOR
    					+ name;
    		cf = type.colfamily.getBytes();

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
	
	public AclPrivilege getPrivilege(String entryKey, EntryAce.AceType type, String name)throws AccessorException{
		
		HTableInterface table = null;

		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());
        	get.addFamily(EntityConstants.ATTR_ACL_COLUMN.getBytes());
        	Result result = table.get(get);
        	
    		NavigableMap<byte[], byte[]> acemap = result.getFamilyMap(EntityConstants.ATTR_ACL_COLUMN.getBytes());
    		for(Map.Entry<byte[], byte[]> entry: acemap.entrySet()){
    			
    			String[] parts = StringUtils.split( Bytes.toString(entry.getKey()), ":");

    			AceType typeTemp = AceType.valueOf(parts[0]);
    			if(parts.length == 3 && type == typeTemp && parts[1].equals(name)){
    				// eg. acl:group:001001:upgrade -> WRITE
    				//     CF | TYPE| KEY  | ACTION   Privilege
    				// here group:001001:upgrade is the qualifier name
    				AclPrivilege priv = AclPrivilege.valueOf(parts[2]);
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
	 * Wrap the Acl information from acl column family.
	 * 
	 **/
	private EntryAcl wrapEntryAcl(Result rawEntry){
		
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
		
		return acl;
	}
	
	/**
	 * Parse the acl information to Put
	 *  
	 **/
	private void parseEntryAcl(Put put,  EntryAcl acl){
		
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
