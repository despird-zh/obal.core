package com.obal.core;

public class CoreConstants {
	
	/**
	 * Separator to connect the qualifier prefix and name ->"."
	 **/
	public static final String QUALIFIER_PREFIX_SEPARATOR = ".";
	
	/**
	 * Separator used to combine multiple keys ->":"
	 **/
	public static final String KEYS_SEPARATOR = ":";

	/**
	 * Separator used to combine value-> "|"
	 **/
	public static final String VALUE_SEPARATOR = "|";
	
	/**
	 * Separator used to combine collection element-> ","
	 **/
	public static final String COLLECT_ELM_SEPARATOR = ",";
	
	/** Ace type user */
	public static final String ACE_TYPE_USER = "_user";
	/** Ace type role */
	public static final String ACE_TYPE_ROLE = "_role";
	/** Ace type group */
	public static final String ACE_TYPE_GROUP = "_group";
	
	/** the builder name : hbase */
	public static final String BUILDER_HBASE = "hbase";
	
	/** the builder name : redis */
	public static final String BUILDER_REDIS = "redis";
	
}
