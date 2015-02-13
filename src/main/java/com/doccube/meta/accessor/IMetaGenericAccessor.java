package com.doccube.meta.accessor;

import java.util.List;

import com.doccube.core.EntryKey;
import com.doccube.core.IBaseAccessor;
import com.doccube.exception.AccessorException;
import com.doccube.meta.EntityAttr;
import com.doccube.meta.EntityMeta;

public interface IMetaGenericAccessor extends IBaseAccessor{

	public EntityAttr getEntityAttr(String attrId )throws AccessorException;
	
	public List<EntityAttr> getAttrList(String entryName)throws AccessorException;
	
	public EntryKey putEntityAttr(EntityAttr attr)throws AccessorException;
	
	public EntityMeta getEntityMeta(String entryName)throws AccessorException;
	
	public List<EntityMeta> getEntityMetaList()throws AccessorException;
	
	public EntryKey putEntityMeta(EntityMeta meta)throws AccessorException;
	
}
