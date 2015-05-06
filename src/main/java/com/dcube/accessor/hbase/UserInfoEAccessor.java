package com.dcube.accessor.hbase;

import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HEntityAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;

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
					
					Principal principal = new Principal(fromObject);
					
					return principal;
				}

				@Override
				public TraceableEntry toSource(Principal toObject)
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
