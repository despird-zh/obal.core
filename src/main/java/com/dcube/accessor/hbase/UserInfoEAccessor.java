package com.dcube.accessor.hbase;

import java.util.Map;

import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.hbase.HEntryWrapper;
import com.dcube.core.hbase.HTraceableEntryWrapper;
import com.dcube.core.security.Principal;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;
import com.dcube.meta.EntityConstants.UserInfo;

public class UserInfoEAccessor extends HEntityAccessor<TraceableEntry> {

	public UserInfoEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_USER);
	}

	@Override
	public HEntryWrapper<TraceableEntry> getEntryWrapper() {
		
		HTraceableEntryWrapper wrapper = new HTraceableEntryWrapper();
		
		return wrapper;
	}

	@SuppressWarnings("unchecked")
	@Override 
	public <To> IEntryConverter<TraceableEntry, To> getEntryConverter(Class<To> cto){
		
		if(cto.equals(Principal.class)){
			
			IEntryConverter<TraceableEntry,Principal> converter = new IEntryConverter<TraceableEntry,Principal>(){

				@Override
				public Principal toTarget(TraceableEntry fromObject)
						throws BaseException {
					EntryKey key = fromObject.getEntryKey();
					Principal principal = new Principal(key.getKey());
					principal.setAccount(fromObject.getAttrValue(UserInfo.Account.attribute, String.class));
					principal.setName(fromObject.getAttrValue(UserInfo.Name.attribute, String.class));
					principal.setSource(fromObject.getAttrValue(UserInfo.Source.attribute, String.class));
					principal.setSalt(fromObject.getAttrValue(UserInfo.Salt.attribute, String.class));
					principal.setPassword(fromObject.getAttrValue(UserInfo.Password.attribute, String.class));
					principal.setGroups(fromObject.getAttrValue(UserInfo.Groups.attribute, Map.class));
					principal.setRoles(fromObject.getAttrValue(UserInfo.Roles.attribute, Map.class));
					
					return principal;
				}

				@Override
				public TraceableEntry toSource(Principal toObject)
						throws BaseException {
					
					String id = toObject.getId();
					
					TraceableEntry entry = new TraceableEntry(EntityConstants.ENTITY_USER,id);
					EntityMeta meta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_USER);
					entry.setAttrValue(meta.getAttr(UserInfo.Account.attribute), toObject.getAccount());
					entry.setAttrValue(meta.getAttr(UserInfo.Name.attribute), toObject.getName());
					entry.setAttrValue(meta.getAttr(UserInfo.Source.attribute), toObject.getSource());
					entry.setAttrValue(meta.getAttr(UserInfo.Salt.attribute), toObject.getSalt());
					entry.setAttrValue(meta.getAttr(UserInfo.Password.attribute), toObject.getPassword());
					entry.setAttrValue(meta.getAttr(UserInfo.Groups.attribute), toObject.getGroups());
					entry.setAttrValue(meta.getAttr(UserInfo.Roles.attribute), toObject.getRoles());
					
					return entry;
				}
				
			};
			
			return (IEntryConverter<TraceableEntry, To>) converter;
		}
		
		return super.getEntryConverter(cto);
	}
	
}
