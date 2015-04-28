package com.dcube.accessor.hbase;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import com.dcube.accessor.ISecurityGAccessor;
import com.dcube.core.AccessorFactory;
import com.dcube.core.CoreConfigs;
import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.IndexAccessor;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HGenericAccessor;
import com.dcube.core.security.Principal;
import com.dcube.core.security.UserGroup;
import com.dcube.exception.AccessorException;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.GroupEnum;
import com.dcube.meta.EntityConstants.UserEnum;
import com.dcube.util.AccessorUtils;

public class SecurityGAccessor extends HGenericAccessor implements ISecurityGAccessor{

	public SecurityGAccessor() {
		super(EntityConstants.ACCESSOR_GENERIC_SECURITY);
	}

	@Override
	public Principal getPrincipalByAccount(String account)
			throws AccessorException {
		
		Principal rtv = null;
		IndexAccessor idxaccr = null;
		UserInfoEAccessor ueaccr = null;
		Principal principal = CoreConfigs.getAdminPrincipal();
		try{
			idxaccr = (IndexAccessor)AccessorFactory.buildIndexAccessor(principal, EntityConstants.ENTITY_USER);
			
			EntryKey entryKey = idxaccr.doGetEntryKey(UserEnum.Account.attribute, account);
			
			ueaccr = (UserInfoEAccessor)AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_USER);
	
			TraceableEntry traceentry = ueaccr.doGetEntry(entryKey.getKey());
			if(traceentry == null){
				
				return null;
			}			
			else{
				
				IEntryConverter<TraceableEntry, Principal> cvt = ueaccr.getEntryConverter(Principal.class);
				rtv = cvt.toTarget(traceentry);
			}
		}catch(BaseException e){
			
			throw new AccessorException("Error when scan the data",e);
		}finally{
			
			AccessorUtils.closeAccessor(ueaccr, idxaccr);
		}
		return rtv;
	}

	@Override
	public Principal getPrincipalByName(String name) throws AccessorException {

		Principal rtv = null;
		IndexAccessor idxaccr = null;
		UserInfoEAccessor ueaccr = null;
		Principal principal = CoreConfigs.getAdminPrincipal();
		try{
			idxaccr = (IndexAccessor)AccessorFactory.buildIndexAccessor(principal, EntityConstants.ENTITY_USER);
			
			EntryKey entryKey = idxaccr.doGetEntryKey(UserEnum.Name.attribute, name);
			
			ueaccr = (UserInfoEAccessor)AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_USER);
	
			TraceableEntry traceentry = ueaccr.doGetEntry(entryKey.getKey());
			if(traceentry == null){
				
				return null;
			}			
			else{
				
				IEntryConverter<TraceableEntry, Principal> cvt = ueaccr.getEntryConverter(Principal.class);
				rtv = cvt.toTarget(traceentry);
			}
		}catch(BaseException e){
			
			throw new AccessorException("Error when scan the data",e);
		}finally{
			
			AccessorUtils.closeAccessor(ueaccr, idxaccr);
		}
		return rtv;
	}

	@Override
	public UserGroup getGroupByName(String name) throws AccessorException {
		
		UserGroup rtv = null;
		IndexAccessor idxaccr = null;
		GroupInfoEAccessor geaccr = null;
		Principal principal = CoreConfigs.getAdminPrincipal();
		try{
			idxaccr = (IndexAccessor)AccessorFactory.buildIndexAccessor(principal, EntityConstants.ENTITY_USER_GROUP);
			
			EntryKey entryKey = idxaccr.doGetEntryKey(GroupEnum.Name.attribute, name);
			
			geaccr = (GroupInfoEAccessor)AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_USER_GROUP);
	
			TraceableEntry traceentry = geaccr.doGetEntry(entryKey.getKey());
			if(traceentry == null){
				
				return null;
			}			
			else{
				
				IEntryConverter<TraceableEntry, UserGroup> cvt = geaccr.getEntryConverter(UserGroup.class);
				rtv = cvt.toTarget(traceentry);
			}
		}catch(BaseException e){
			
			throw new AccessorException("Error when scan the data",e);
		}finally{
			
			AccessorUtils.closeAccessor(geaccr, idxaccr);
		}
		return rtv;
	}

}
