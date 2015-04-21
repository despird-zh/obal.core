package com.dcube.accessor.hbase;

import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.security.UserRole;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;

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
					
					UserRole role = new UserRole();
					role.setGenericEntry(fromObject);
					return role;
				}

				@Override
				public TraceableEntry toSource(UserRole toObject)
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
