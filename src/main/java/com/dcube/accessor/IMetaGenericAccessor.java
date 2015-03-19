package com.dcube.accessor;

import java.util.List;

import com.dcube.core.EntryKey;
import com.dcube.core.IBaseAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityMeta;

/**
 * Interface to access entity meta data
 * 
 * @author despird-zh
 * @version 0.1 2014-2-1
 * 
 **/
public interface IMetaGenericAccessor extends IBaseAccessor{

	/**
	 * Get entity attribute
	 * 
	 * @param attrKey the attribute key
	 **/
	public EntityAttr getEntityAttr(String attrKey )throws AccessorException;
	
	/**
	 * Get attribute list
	 * 
	 * @param entityName the entity name
	 **/
	public List<EntityAttr> getAttrList(String entityName)throws AccessorException;
	
	/**
	 * Put entity attribute
	 * 
	 * @param attribute the entity attribute
	 **/
	public EntryKey putEntityAttr(EntityAttr attribute)throws AccessorException;
	
	/**
	 * Get entity meta
	 * 
	 * @param entityName the entity name
	 **/
	public EntityMeta getEntityMeta(String entityName)throws AccessorException;
	
	/**
	 * Get entity meta list
	 *
	 **/
	public List<EntityMeta> getEntityMetaList()throws AccessorException;
	
	/**
	 * Put Entity meta
	 * 
	 * @param entityMeta entity meta 
	 **/
	public EntryKey putEntityMeta(EntityMeta entityMeta)throws AccessorException;
	
}
