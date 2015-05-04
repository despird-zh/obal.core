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
import com.dcube.core.security.UserRole;
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
					
					UserGroup group = new UserGroup();
					group.setGenericEntry(fromObject);
					return group;
				}

				@Override
				public TraceableEntry toSource(UserGroup toObject)
						throws BaseException {
					
					TraceableEntry entry = (TraceableEntry)toObject.getGenericEntry();
					return entry;
				}
				
			};
			
			return (IEntryConverter<TraceableEntry, To>) converter;
		}
		
		return super.getEntryConverter(cto);
	}
}
