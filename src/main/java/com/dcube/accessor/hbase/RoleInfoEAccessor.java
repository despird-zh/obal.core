package com.dcube.accessor.hbase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.TraceInfo;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.security.UserRole;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.RoleEnum;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;

public class RoleInfoEAccessor extends HEntityAccessor<TraceableEntry>{

	public RoleInfoEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_ROLE);
	}

	@Override
	public TraceableEntry newEntryObject() {
		
		return new TraceableEntry();
	}

	@SuppressWarnings("unchecked")
	@Override 
	public <To> IEntryConverter<TraceableEntry, To> getEntryConverter(Class<To> cto){
		
		if(cto.equals(UserRole.class)){
			
			IEntryConverter<TraceableEntry,UserRole> converter = new IEntryConverter<TraceableEntry,UserRole>(){

				@Override
				public UserRole toTarget(TraceableEntry fromObject)
						throws BaseException {
					
					String name = fromObject.getAttrValue(RoleEnum.Name.attribute, String.class);
					EntryKey key = fromObject.getEntryKey();
					UserRole role = new UserRole(name,key.getKey());
					
					TraceInfo tinfo = fromObject.getTraceInfo();
					role.setTraceInfo(tinfo);
					role.setDescription(fromObject.getAttrValue(RoleEnum.Description.attribute, String.class));
					
					Map<String, String> mapAttr = fromObject.getAttrValue(RoleEnum.Users.attribute, Map.class);
					Set<String> userset = mapAttr.keySet();
					role.setUsers(userset);
					
					mapAttr = fromObject.getAttrValue(RoleEnum.Groups.attribute, Map.class);
					Set<String> groupset = mapAttr.keySet();
					role.setGroups(groupset);
					
					return role;
				}

				@Override
				public TraceableEntry toSource(UserRole toObject)
						throws BaseException {
					
					String id = toObject.getKey();
					
					TraceableEntry entry = new TraceableEntry(EntityConstants.ENTITY_USER_ROLE,id);
					
					EntityMeta meta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_USER_ROLE);
					entry.setAttrValue(meta.getAttr(RoleEnum.Name.attribute), toObject.name());
					entry.setAttrValue(meta.getAttr(RoleEnum.Description.attribute), toObject.getDescription());
					
					Set<String> uset = toObject.getUsers();
					Map<String, String> attrMap = new HashMap<String, String>();
					for(String t:uset){
						attrMap.put(t, EntityConstants.BLANK_VALUE);
					}
					entry.setAttrValue(meta.getAttr(RoleEnum.Users.attribute), attrMap);
					
					Set<String> gset = toObject.getGroups();
					attrMap = new HashMap<String, String>();
					for(String t:gset){
						attrMap.put(t, EntityConstants.BLANK_VALUE);
					}
					entry.setAttrValue(meta.getAttr(RoleEnum.Groups.attribute), attrMap);
					
					return entry;
				}
				
			};
			
			return (IEntryConverter<TraceableEntry, To>) converter;
		}
		
		return super.getEntryConverter(cto);
	}
}
