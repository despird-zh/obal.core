package com.obal.core.security.hbase;

import java.util.Map;

import com.obal.core.EntryKey;
import com.obal.core.IEntryConverter;
import com.obal.core.accessor.AccessorContext;
import com.obal.core.accessor.EntryInfo;
import com.obal.core.accessor.TraceableEntry;
import com.obal.core.hbase.HEntryWrapper;
import com.obal.core.hbase.HEntityAccessor;
import com.obal.core.hbase.HRawWrapper;
import com.obal.core.hbase.HTraceableEntryWrapper;
import com.obal.core.security.Principal;
import com.obal.exception.BaseException;
import com.obal.meta.BaseEntity;
import com.obal.meta.EntityConstants;

public class UserAccessor extends HEntityAccessor<TraceableEntry> {

	public UserAccessor(AccessorContext context) {
		super(context);
	}

	@Override
	public HEntryWrapper<TraceableEntry> getEntryWrapper() {
		
		HTraceableEntryWrapper wrapper = new HTraceableEntryWrapper();
		
		return wrapper;
	}

	public static IEntryConverter<TraceableEntry,Principal> toPrincipal = new IEntryConverter<TraceableEntry,Principal>(){

		@Override
		public Principal convert(TraceableEntry fromObject)
				throws BaseException {
			
			EntryKey key = fromObject.getEntryKey();
			Principal principal = new Principal(key.getKey());
			principal.setAccount(fromObject.getAttrValue("i_account", String.class));
			principal.setName(fromObject.getAttrValue("i_name", String.class));
			principal.setSource(fromObject.getAttrValue("i_source", String.class));
			principal.setPassword(fromObject.getAttrValue("i_password", String.class));
			principal.setGroups(fromObject.getAttrValue("i_groups", Map.class));
			principal.setRoles(fromObject.getAttrValue("i_roles", Map.class));
			
			return principal;
		}};
	
	public static IEntryConverter<Principal,TraceableEntry> toEntryInfo = new IEntryConverter<Principal,TraceableEntry>(){

		@Override
		public TraceableEntry convert(Principal fromObject)
				throws BaseException {
			String id = fromObject.getId();
			
			TraceableEntry entry = new TraceableEntry(EntityConstants.ENTITY_PRINCIPAL,id);
			
			entry.setAttrValue("i_account", fromObject.getAccount());
			entry.setAttrValue("i_name", fromObject.getName());
			entry.setAttrValue("i_source", fromObject.getSource());
			entry.setAttrValue("i_password", fromObject.getPassword());
			entry.setAttrValue("i_groups", fromObject.getGroups());
			entry.setAttrValue("i_roles", fromObject.getRoles());
			
			return entry;
		}};
}
