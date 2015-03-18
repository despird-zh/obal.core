package com.doccube.util;

import java.util.List;

import com.doccube.accessor.IAdminAccessor;
import com.doccube.admin.EntityAdmin;
import com.doccube.core.security.Principal;
import com.doccube.exception.AccessorException;
import com.doccube.meta.EntityAttr;

/**
 * Utility tools for administration
 **/
public class AdminUtils {

	/**
	 * Create schema 
	 *  
	 *  @param schemaName the entry name
	 *  @param attrs the Attributes of entry meta
	 **/
	public static void createSchema(String schemaName, List<EntityAttr> attrs) throws AccessorException{
		Principal principal = null;
		IAdminAccessor aa = EntityAdmin.getInstance().getAdminAccessor(principal);
		
		aa.createSchema(schemaName, attrs);
	}
	
	/**
	 * Update schema 
	 * @param entryName the entry name
	 * @param attrs the attributes of entry schema
	 **/
	public static void updateSchema(String entryName, List<EntityAttr> attrs) throws AccessorException{
		Principal principal = null;
		IAdminAccessor aa = EntityAdmin.getInstance().getAdminAccessor(principal);
		
		aa.updateSchema(entryName, attrs);
	}
		
	public static void dropSchema(String schemaName) throws AccessorException{
		
		Principal principal = null;
		IAdminAccessor aa = EntityAdmin.getInstance().getAdminAccessor(principal);
		
		aa.dropSchema(schemaName);
	}
}
