package com.dcube.accessor.hbase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;
import com.dcube.meta.EntityConstants.GroupEnum;
import com.dcube.meta.EntityConstants.UserEnum;

public class UserInfoEAccessor extends HEntityAccessor<TraceableEntry> {

	public UserInfoEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_USER);
	}

	@Override
	public TraceableEntry newEntryObject() {
		
		return new TraceableEntry();
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
					principal.setAccount(fromObject.getAttrValue(UserEnum.Account.attribute, String.class));
					principal.setName(fromObject.getAttrValue(UserEnum.Name.attribute, String.class));
					principal.setSource(fromObject.getAttrValue(UserEnum.Source.attribute, String.class));
					principal.setSalt(fromObject.getAttrValue(UserEnum.Salt.attribute, String.class));
					principal.setPassword(fromObject.getAttrValue(UserEnum.Password.attribute, String.class));
					
					Map<String, String> mapAttr = fromObject.getAttrValue(UserEnum.Groups.attribute, Map.class);
					Set<String> gset = mapAttr.keySet();
					principal.setGroups(gset);
					
					mapAttr = fromObject.getAttrValue(UserEnum.Roles.attribute, Map.class);
					Set<String> rset = mapAttr.keySet();
					principal.setRoles(rset);

					principal.setProfileSettings(fromObject.getAttrValue(UserEnum.Profile.attribute, Map.class));
					return principal;
				}

				@Override
				public TraceableEntry toSource(Principal toObject)
						throws BaseException {
					
					String id = toObject.getKey();
					
					TraceableEntry entry = new TraceableEntry(EntityConstants.ENTITY_USER,id);
					EntityMeta meta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_USER);
					entry.setAttrValue(meta.getAttr(UserEnum.Account.attribute), toObject.getAccount());
					entry.setAttrValue(meta.getAttr(UserEnum.Name.attribute), toObject.getName());
					entry.setAttrValue(meta.getAttr(UserEnum.Source.attribute), toObject.getSource());
					entry.setAttrValue(meta.getAttr(UserEnum.Salt.attribute), toObject.getSalt());
					entry.setAttrValue(meta.getAttr(UserEnum.Password.attribute), toObject.getPassword());
					
					Set<String> gset = toObject.getGroups();
					Map<String, String> attrMap = new HashMap<String, String>();
					for(String t:gset){
						attrMap.put(t, EntityConstants.BLANK_VALUE);
					}
					entry.setAttrValue(meta.getAttr(GroupEnum.Groups.attribute), attrMap);
										
					Set<String> rset = toObject.getRoles();
					attrMap = new HashMap<String, String>();
					for(String t:rset){
						attrMap.put(t, EntityConstants.BLANK_VALUE);
					}
					entry.setAttrValue(meta.getAttr(UserEnum.Roles.attribute), attrMap);
					
					entry.setAttrValue(meta.getAttr(UserEnum.Profile.attribute), toObject.getProfileSettings());
					
					entry.setTraceInfo(toObject.getTraceInfo());
					return entry;
				}
				
			};
			
			return (IEntryConverter<TraceableEntry, To>) converter;
		}
		
		return super.getEntryConverter(cto);
	}
	
}
