package com.doccube.accessor.hbase;

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
import com.doccube.meta.EntityConstants.UserInfo;
import com.doccube.meta.EntityManager;
import com.doccube.meta.EntityMeta;

public class UserAccessor extends HEntityAccessor<TraceableEntry> {

	public UserAccessor(AccessorContext context) {
		super(EntityConstants.ACCESSOR_ENTITY_USER,context);
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
			principal.setAccount(fromObject.getAttrValue(UserInfo.Account.attribute, String.class));
			principal.setName(fromObject.getAttrValue(UserInfo.Name.attribute, String.class));
			principal.setSource(fromObject.getAttrValue(UserInfo.Source.attribute, String.class));
			principal.setPassword(fromObject.getAttrValue(UserInfo.Password.attribute, String.class));
			principal.setGroups(fromObject.getAttrValue(UserInfo.Groups.attribute, Map.class));
			principal.setRoles(fromObject.getAttrValue(UserInfo.Roles.attribute, Map.class));
			
			return principal;
		}};
	
	public static IEntryConverter<Principal,TraceableEntry> toEntryInfo = new IEntryConverter<Principal,TraceableEntry>(){

		@Override
		public TraceableEntry convert(Principal fromObject)
				throws BaseException {
			String id = fromObject.getId();
			
			TraceableEntry entry = new TraceableEntry(EntityConstants.ENTITY_PRINCIPAL,id);
			EntityMeta meta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_PRINCIPAL);
			entry.setAttrValue(meta.getAttr(UserInfo.Account.attribute), fromObject.getAccount());
			entry.setAttrValue(meta.getAttr(UserInfo.Name.attribute), fromObject.getName());
			entry.setAttrValue(meta.getAttr(UserInfo.Source.attribute), fromObject.getSource());
			entry.setAttrValue(meta.getAttr(UserInfo.Password.attribute), fromObject.getPassword());
			entry.setAttrValue(meta.getAttr(UserInfo.Groups.attribute), fromObject.getGroups());
			entry.setAttrValue(meta.getAttr(UserInfo.Roles.attribute), fromObject.getRoles());
			
			return entry;
		}};

}
