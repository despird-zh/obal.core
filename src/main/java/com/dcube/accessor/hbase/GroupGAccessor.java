package com.dcube.accessor.hbase;

import java.util.List;

import com.dcube.accessor.IGroupGAccessor;
import com.dcube.core.hbase.HGenericAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityConstants;

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


}
