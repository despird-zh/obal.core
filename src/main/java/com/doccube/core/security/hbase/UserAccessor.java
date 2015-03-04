package com.doccube.core.security.hbase;

import java.util.Map;

import com.doccube.core.EntryKey;
import com.doccube.core.IEntryConverter;
import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.accessor.TraceableEntry;
import com.doccube.core.hbase.HEntityAccessor;
import com.doccube.core.hbase.HEntryWrapper;
import com.doccube.core.hbase.HTraceableEntryWrapper;
import com.doccube.core.security.Principal;
import com.doccube.exception.BaseException;
import com.doccube.meta.EntityConstants;
import com.doccube.meta.EntityManager;
import com.doccube.meta.EntityMeta;

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
			EntityMeta meta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_PRINCIPAL);
			entry.setAttrValue(meta.getAttr("i_account"), fromObject.getAccount());
			entry.setAttrValue(meta.getAttr("i_name"), fromObject.getName());
			entry.setAttrValue(meta.getAttr("i_source"), fromObject.getSource());
			entry.setAttrValue(meta.getAttr("i_password"), fromObject.getPassword());
			entry.setAttrValue(meta.getAttr("i_groups"), fromObject.getGroups());
			entry.setAttrValue(meta.getAttr("i_roles"), fromObject.getRoles());
			
			return entry;
		}};

}
