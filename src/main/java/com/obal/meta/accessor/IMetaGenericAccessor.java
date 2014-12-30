package com.obal.meta.accessor;

import java.util.List;

import com.obal.core.EntryKey;
import com.obal.core.IBaseAccessor;
import com.obal.exception.AccessorException;
import com.obal.meta.EntityAttr;
import com.obal.meta.EntityMeta;

public interface IMetaGenericAccessor extends IBaseAccessor{

	public EntityAttr getEntityAttr(String attrId )throws AccessorException;
	
	public List<EntityAttr> getAttrList(String entryName)throws AccessorException;
	
	public EntryKey putEntityAttr(EntityAttr attr)throws AccessorException;
	
	public EntityMeta getEntityMeta(String entryName)throws AccessorException;
	
	public List<EntityMeta> getEntityMetaList()throws AccessorException;
	
	public EntryKey putEntityMeta(EntityMeta meta)throws AccessorException;
	
}
