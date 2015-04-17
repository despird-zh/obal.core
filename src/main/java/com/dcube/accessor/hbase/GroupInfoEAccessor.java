package com.dcube.accessor.hbase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dcube.core.EntryKey;
import com.dcube.core.IEntryConverter;
import com.dcube.core.TraceInfo;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.security.UserGroup;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.GroupEnum;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;

public class GroupInfoEAccessor extends HEntityAccessor<TraceableEntry>{

	public GroupInfoEAccessor() {
		super(EntityConstants.ACCESSOR_ENTITY_GROUP);
	}

	@Override
	public TraceableEntry newEntryObject() {
		
		return new TraceableEntry();
	}

	@SuppressWarnings("unchecked")
	@Override 
	public <To> IEntryConverter<TraceableEntry, To> getEntryConverter(Class<To> cto){
		
		if(cto.equals(UserGroup.class)){
			
			IEntryConverter<TraceableEntry,UserGroup> converter = new IEntryConverter<TraceableEntry,UserGroup>(){

				@Override
				public UserGroup toTarget(TraceableEntry fromObject)
						throws BaseException {
					
					String name = fromObject.getAttrValue(GroupEnum.Name.attribute, String.class);
					EntryKey key = fromObject.getEntryKey();
					UserGroup group = new UserGroup(name,key.getKey());
					
					TraceInfo tinfo = fromObject.getTraceInfo();
					group.setTraceInfo(tinfo);
					group.setDescription(fromObject.getAttrValue(GroupEnum.Description.attribute, String.class));
					
					Map<String, String> mapAttr = fromObject.getAttrValue(GroupEnum.Users.attribute, Map.class);
					Set<String> userset = mapAttr.keySet();
					group.setUsers(userset);
					
					mapAttr = fromObject.getAttrValue(GroupEnum.Groups.attribute, Map.class);
					Set<String> groupset = mapAttr.keySet();
					group.setGroups(groupset);
					
					group.setParent(fromObject.getAttrValue(GroupEnum.Parent.attribute, String.class));
					
					return group;
				}

				@Override
				public TraceableEntry toSource(UserGroup toObject)
						throws BaseException {
					
					String id = toObject.getKey();
					
					TraceableEntry entry = new TraceableEntry(EntityConstants.ENTITY_USER_GROUP,id);
					
					EntityMeta meta = EntityManager.getInstance().getEntityMeta(EntityConstants.ENTITY_USER_GROUP);
					entry.setAttrValue(meta.getAttr(GroupEnum.Name.attribute), toObject.name());
					entry.setAttrValue(meta.getAttr(GroupEnum.Description.attribute), toObject.getDescription());
					
					Set<String> uset = toObject.getUsers();
					Map<String, String> attrMap = new HashMap<String, String>();
					for(String t:uset){
						attrMap.put(t, EntityConstants.BLANK_VALUE);
					}
					entry.setAttrValue(meta.getAttr(GroupEnum.Users.attribute), attrMap);
					
					Set<String> gset = toObject.getGroups();
					attrMap = new HashMap<String, String>();
					for(String t:gset){
						attrMap.put(t, EntityConstants.BLANK_VALUE);
					}
					entry.setAttrValue(meta.getAttr(GroupEnum.Groups.attribute), attrMap);
					entry.setAttrValue(meta.getAttr(GroupEnum.Parent.attribute), toObject.getParent());
					
					entry.setTraceInfo(toObject.getTraceInfo());
					return entry;
				}
				
			};
			
			return (IEntryConverter<TraceableEntry, To>) converter;
		}
		
		return super.getEntryConverter(cto);
	}
}
