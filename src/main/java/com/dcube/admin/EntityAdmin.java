package com.dcube.admin; 

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.accessor.IAdminGAccessor;
import com.dcube.accessor.IMetaGAccessor;
import com.dcube.core.AccessorFactory;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.exception.EntityException;
import com.dcube.launcher.ILifecycle.LifeState;
import com.dcube.launcher.LifecycleHooker;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityManager;
import com.dcube.meta.EntityMeta;
import com.dcube.util.AccessorUtils;

/**
 * EntityAdmin in charge of the entity loading and create, drop etc. operation.
 * 
 * @author despird
 * @version 0.1 2014-11-15 
 * 
 * @see AdminGAccessor
 **/
public class EntityAdmin {

	Logger LOGGER = LoggerFactory.getLogger(EntityAdmin.class);
	
	LifecycleHooker hooker = null;
	
	private EntityAdmin(){
		
		hooker = new LifecycleHooker("EntityAdmin", 990){

			@Override
			public void initial() {
				sendFeedback(false,"EntityAdmin initial do nothing");
			}

			@Override
			public void startup() {
				sendFeedback(false,"EntityAdmin trying load EntityMeta.");
				loadEntityMeta();
				sendFeedback(false,"EntityAdmin loadEntityMeta done.");
			}

			@Override
			public void shutdown() {
				
				sendFeedback(false,"EntityAdmin shutdown do nothing");
			}		
			
		};
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
	public IAdminGAccessor getAdminAccessor(Principal principal){
		
		IAdminGAccessor aa = null;
		try {
			aa = AccessorFactory.buildGenericAccessor(principal, EntityConstants.ACCESSOR_GENERIC_ADMIN);
		} catch (AccessorException e) {
			
			LOGGER.error("Error when getting Admin service",e);
		}
		
		return aa;
	}

	/**
	 * Get the lifecycle hooker, keep step with core lifecycle event 
	 **/
	public LifecycleHooker getLifecycleHooker(){
		
		return hooker;
	}

	/**
	 * Load all the entity meta information from hbase.
	 * meta information will be stored in EntityManager instance.
	 * 
	 **/
	public void loadEntityMeta() {

		IMetaGAccessor imeta = null;
		Principal princ = new Principal("acc", "demo", "pwd");
		try {
			EntityManager smgr = EntityManager.getInstance();
			
			imeta = AccessorUtils.getGenericAccessor(princ,
					EntityConstants.ACCESSOR_GENERIC_META);
			// query all the entity list data.
			List<EntityMeta> entrymetas = imeta.getEntityMetaList();
			for (EntityMeta em : entrymetas) {
				// save entity meta information to entity manager for later use.
				smgr.putEntityMeta(em);
			}

		} catch (AccessorException e) {
			
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

		IAdminGAccessor adminAccessor = getAdminAccessor(princ);
		IMetaGAccessor metaAttrAccessor = null;
		List<EntityAttr> attrs = meta.getAllAttrs();

		try {
			// create the schema table and columnfamily
			adminAccessor.createSchema(meta.getSchema(),attrs);

			metaAttrAccessor = AccessorUtils.getGenericAccessor(princ,
					EntityConstants.ACCESSOR_GENERIC_META);
			// save the entity info and entity attributes data.
			metaAttrAccessor.putEntityMeta(meta);

		} catch (AccessorException e) {
			
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

		IAdminGAccessor adminAccessor = getAdminAccessor(princ);
		
		try {
			adminAccessor.dropSchema(schemaName);
		} catch (AccessorException e) {
			
			LOGGER.debug("Error when drop schema-{}",e, schemaName);
		}finally {

			AccessorUtils.closeAccessor(adminAccessor);
		}
		
	}
}
