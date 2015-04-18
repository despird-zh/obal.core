package com.dcube.accessor.hbase;

import java.util.List;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import com.dcube.accessor.IGroupGAccessor;
import com.dcube.core.AccessorFactory;
import com.dcube.core.EntryFilter;
import com.dcube.core.IEntryConverter;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.core.accessor.TraceableEntry;
import com.dcube.core.hbase.HGenericAccessor;
import com.dcube.core.security.Principal;
import com.dcube.core.security.UserGroup;
import com.dcube.exception.AccessorException;
import com.dcube.exception.BaseException;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityConstants.GroupEnum;
import com.dcube.util.AccessorUtils;

public class GroupGAccessor extends HGenericAccessor implements IGroupGAccessor{

	public GroupGAccessor() {
		super(EntityConstants.ACCESSOR_GENERIC_GROUP);
	}

	@Override
	public void addUser(String group, String user) throws AccessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUser(String group, String user) throws AccessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addGroup(String parent, String group) throws AccessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeGroup(String parent, String group)
			throws AccessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Principal> getPrincipalsByGroup(String group)
			throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getGroupChain(String group) throws AccessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserGroup getGroupByName(String name) throws AccessorException {
		
		UserGroup rtv = null;
		GroupInfoEAccessor gea = null;
		try{
			gea = (GroupInfoEAccessor)AccessorFactory.buildEntityAccessor(this, EntityConstants.ACCESSOR_ENTITY_GROUP);
			GroupEnum attrInfo = GroupEnum.Name;
	
			Filter filter = new SingleColumnValueFilter(attrInfo.colfamily.getBytes(), 
						attrInfo.qualifier.getBytes(), 
						CompareFilter.CompareOp.EQUAL, name.getBytes());
			
			EntryCollection<TraceableEntry> ecoll = gea.doScanEntry(new EntryFilter<Filter>(filter));
			
			if(ecoll.isEmpty()){
				
				return null;
			}			
			else{
				
				TraceableEntry tentry = ecoll.iterator().next();
				IEntryConverter<TraceableEntry, UserGroup> cvt = gea.getEntryConverter(UserGroup.class);
				rtv = cvt.toTarget(tentry);
			}
		}catch(BaseException e){
			
			throw new AccessorException("Error when scan the data",e);
		}finally{
			
			AccessorUtils.closeAccessor(gea);
		}
		return rtv;
	}

}
