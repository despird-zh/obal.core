package com.obal.util;

import java.util.List;

import com.obal.admin.EntityAdmin;
import com.obal.admin.IAdminAccessor;
import com.obal.core.security.Principal;
import com.obal.exception.AccessorException;
import com.obal.meta.EntityAttr;

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
