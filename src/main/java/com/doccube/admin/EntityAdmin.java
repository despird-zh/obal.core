package com.doccube.admin; 

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doccube.core.AccessorFactory;
import com.doccube.core.security.Principal;
import com.doccube.exception.AccessorException;
import com.doccube.exception.EntityException;
import com.doccube.meta.EntityAttr;
import com.doccube.meta.EntityConstants;
import com.doccube.meta.EntityManager;
import com.doccube.meta.EntityMeta;
import com.doccube.meta.accessor.IMetaGenericAccessor;
import com.doccube.util.AccessorUtils;

/**
 * EntityAdmin in charge of the entity loading and create, drop etc. operation.
 * 
 * @author despird
 * @version 0.1 2014-11-15 
 * 
 * @see AdminAccessor
 **/
public class EntityAdmin {

	public static final String ADMIN_ACCESSOR = EntityConstants.ENTITY_PREFIX+ "admin";
	
	Logger LOGGER = LoggerFactory.getLogger(EntityAdmin.class);
	
	private EntityAdmin(){
		
		String path = this.getClass().getPackage().getName().replace(".", "/");
		path += "/AccessorMap.hbase.properties";
		AccessorFactory.getInstance().appendMapping("hbase", path);		
	}
	
	private static EntityAdmin instance;
	
	/**
	 * Get the singleton instance 
	 * 
	 **/
	public static EntityAdmin getInstance(){
		
		if(instance == null){
			
			instance = new EntityAdmin();
		}
		
		return instance;
	}
	
	/**
	 * Get the AdminAccessor instance, it is used for entity meta operation
	 * 
	 * @param principal the principal object. 
	 **/
	public IAdminAccessor getAdminAccessor(Principal principal){
		
		IAdminAccessor aa = null;
		try {
			aa = AccessorFactory.getInstance().buildGenericAccessor(principal, ADMIN_ACCESSOR);
		} catch (EntityException e) {
			
			LOGGER.error("Error when getting Admin service",e);
		}
		
		return aa;
	}


	/**
	 * Load all the entity meta information from hbase.
	 * meta information will be stored in EntityManager instance.
	 * 
	 **/
	public void loadEntityMeta() {

		IMetaGenericAccessor imeta = null;
		Principal princ = new Principal("acc", "demo", "pwd");
		try {
			EntityManager smgr = EntityManager.getInstance();
			
			imeta = AccessorUtils.getGenericAccessor(princ,
					EntityConstants.ENTITY_META_GENERIC);
			// query all the entity list data.
			List<EntityMeta> entrymetas = imeta.getEntityMetaList();
			for (EntityMeta em : entrymetas) {
				// save entity meta information to entity manager for later use.
				smgr.putEntityMeta(em);
			}

		} catch (AccessorException e) {
			
			LOGGER.debug("Error when loading entity meta information",e);
		} catch (EntityException e) {
			
			LOGGER.debug("Error when loading entity meta information",e);
		} finally {
			AccessorUtils.closeAccessor(imeta);
		}
	}
	
	/**
	 * Setup the schema create the schema as per the meta information.
	 * 
	 * @param meta the entity meta information
	 **/
	public void setupSchema(EntityMeta meta) {

		Principal princ = new Principal("acc", "demo", "pwd");

		IAdminAccessor adminAccessor = getAdminAccessor(princ);
		IMetaGenericAccessor metaAttrAccessor = null;
		List<EntityAttr> attrs = meta.getAllAttrs();

		try {
			// create the schema table and columnfamily
			adminAccessor.createSchema(meta.getEntityName(),attrs);

			metaAttrAccessor = AccessorUtils.getGenericAccessor(princ,
					EntityConstants.ENTITY_META_GENERIC);
			// save the entity info and entity attributes data.
			metaAttrAccessor.putEntityMeta(meta);

		} catch (AccessorException e) {
			
			LOGGER.debug("Error when loading entity meta information",e);
		} catch (EntityException e) {
			
			LOGGER.debug("Error when loading entity meta information",e);
		} finally {

			AccessorUtils.closeAccessor(metaAttrAccessor, adminAccessor);
		}
	}
	
	/**
	 * Drop schema(table) by schema name 
	 * 
	 * @param schemaName the name of schema
	 **/
	public void dropSchema(String schemaName){
		
		Principal princ = new Principal("acc", "demo", "pwd");

		IAdminAccessor adminAccessor = getAdminAccessor(princ);
		
		try {
			adminAccessor.dropSchema(schemaName);
		} catch (AccessorException e) {
			
			LOGGER.debug("Error when drop schema-{}",e, schemaName);
		}finally {

			AccessorUtils.closeAccessor(adminAccessor);
		}
		
	}
}
