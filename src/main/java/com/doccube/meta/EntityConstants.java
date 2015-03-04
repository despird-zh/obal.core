package com.doccube.meta;

/**
 * Define necessary entity names
 *  
 **/
public class EntityConstants {

	public static final String ENTITY_PREFIX = "dcube.";
	/** the blind entity */
	public static String ENTITY_BLIND = ENTITY_PREFIX + "blind";
	/** the meta info */
	public static String ENTITY_META_INFO = ENTITY_PREFIX + "meta.info";
	/** the meta attr */
	public static String ENTITY_META_ATTR = ENTITY_PREFIX + "meta.attr";

	/** 
	 * the meta general , key of Accessor 
	 * 
	 * @see IMetaAttrGeneral
	 **/
	public static String ENTITY_META_GENERIC = ENTITY_PREFIX + "meta.generic";
	/**
	 * the entity name of principal
	 **/
	public static String ENTITY_PRINCIPAL = ENTITY_PREFIX + "user";
	
	/**
	 * the entity name of role
	 **/
	public static String ENTITY_USER_ROLE = ENTITY_PREFIX + "user.role";
	
	/**
	 * the entity name of group
	 **/
	public static String ENTITY_USER_GROUP = ENTITY_PREFIX + "user.group";
	
	public static String NAME_SEPARATOR = ".";
}
