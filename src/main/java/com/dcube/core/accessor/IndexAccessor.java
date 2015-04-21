package com.dcube.core.accessor;

import java.util.List;

import com.dcube.core.EntryKey;
import com.dcube.core.IBaseAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityConstants;

/**
 * IndexAccessor to be used for Index Data get/set.
 **/
public abstract class IndexAccessor implements IBaseAccessor{
	
	/** the context object */
	private AccessorContext context;
	/** the IBaseAccessor name */
	private String accessorName;
	
	/**
	 * Constructor with entry schema information 
	 * 
	 * @param context the context that provides principal etc. 
	 **/
	public IndexAccessor(AccessorContext context){
		this.accessorName = EntityConstants.ACCESSOR_ENTITY_INDEX;
		this.context = context;
	}
	
	/**
	 * Get EntryKey via the attribute value, if there multiple keys, 
	 * return top one. 
	 * @param attribute 
	 * @param value 
	 **/
	public abstract EntryKey doGetEntryKey(String attribute, Object value) throws AccessorException;
	
	/**
	 * Get EntryKey list via the attribute value
	 * @param attribute 
	 * @param value 
	 **/
	public abstract List<EntryKey> doGetEntryKeyList(String attribute, Object value)throws AccessorException;
	
	/**
	 * Delete the index of attribute with specified key,
	 * If key not specified means delete all keys
	 **/
	public abstract void doDelEntryKey(String attribute, Object value, String ... key)throws AccessorException;
	
	/**
	 * Put entry attribute and keys as index pair 
	 **/
	public abstract void doPutEntryKey(String attribute, Object value, String ... keys)throws AccessorException;
	
	@Override
	public void close() throws Exception {
		if(context != null){
			// not embed accessor, purge all resource;embed only release object pointers.
			context.clear();		
			context = null;
		}
	}

	@Override
	public String getAccessorName() {
		
		return this.accessorName;
	}

	@Override
	public void setContext(GenericContext context) throws AccessorException {
		if(!(context instanceof AccessorContext))
			throw new AccessorException("context must be AccessorContext.");
		
		this.context = (AccessorContext)context;
	}

	@Override
	public AccessorContext getContext() {

		return context;
	}

	@Override
	public boolean isEmbed() {

		return context == null? false:context.isEmbed();
	}
}
