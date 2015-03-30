package com.dcube.accessor.hbase;

import java.util.List;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import com.dcube.accessor.IPrincipalGAccessor;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryFilter;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HGenericAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.UserEnum;
import com.dcube.util.AccessorUtils;

public class PrincipalGAccessor extends HGenericAccessor implements IPrincipalGAccessor{

	public PrincipalGAccessor() {
		super(EntityConstants.ACCESSOR_GENERIC_USER);
	}

	@Override
	public Principal getPrincipalByAccount(String account)
			throws AccessorException {
		
		Principal rtv = null;
		UserInfoEAccessor uea = null;
		try{
			uea = (UserInfoEAccessor)AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_USER);
			UserEnum attrInfo = UserEnum.Account;
	
			Filter filter = new SingleColumnValueFilter(attrInfo.colfamily.getBytes(), 
						attrInfo.qualifier.getBytes(), 
						CompareFilter.CompareOp.EQUAL, account.getBytes());
			
			EntryCollection<TraceableEntry> ecoll = uea.doScanEntry(new EntryFilter<Filter>(filter));
			
			if(ecoll.isEmpty()){
				
				throw new BaseException("The result is empty");
			}			
			else{
				
				TraceableEntry tentry = ecoll.iterator().next();
				IEntryConverter<TraceableEntry, Principal> cvt = uea.getEntryConverter(Principal.class);
				rtv = cvt.toTarget(tentry);
			}
		}catch(BaseException e){
			
			throw new AccessorException("Error when scan the data",e);
		}finally{
			
			AccessorUtils.closeAccessor(uea);
		}
		return rtv;
	}

	@Override
	public Principal getPrincipalByName(String name) throws AccessorException {

		Principal rtv = null;
		UserInfoEAccessor uea = null;
		try{
			uea = (UserInfoEAccessor)AccessorFactory.buildEntityAccessor(this, EntityConstants.ENTITY_USER);
			UserEnum attrName = UserEnum.Name;
	
			Filter filter = new SingleColumnValueFilter(attrName.colfamily.getBytes(), 
					attrName.qualifier.getBytes(), 
						CompareFilter.CompareOp.EQUAL, name.getBytes());
			
			EntryCollection<TraceableEntry> ecoll = uea.doScanEntry(new EntryFilter<Filter>(filter));
			
			if(ecoll.isEmpty()){
				
				throw new BaseException("The result is empty");
			}			
			else{
				
				TraceableEntry tentry = ecoll.iterator().next();
				IEntryConverter<TraceableEntry, Principal> cvt = uea.getEntryConverter(Principal.class);
				rtv = cvt.toTarget(tentry);
			}
		}catch(BaseException e){
			
			throw new AccessorException("Error when scan the data",e);
		}finally{
			
			AccessorUtils.closeAccessor(uea);
		}
		return rtv;
	}

	@Override
	public List<Principal> getPrincipalsByGroup(String group)
			throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Principal> getPrincipalsByRole(String role)
			throws AccessorException {
		
		return null;
	}

}
