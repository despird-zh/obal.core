package com.dcube.accessor.hbase;

import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.security.UserGroup;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;

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
					
					UserGroup group = new UserGroup(fromObject);
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
