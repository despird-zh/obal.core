package com.dcube.core;

public class CoreConstants {
	
	/** Separator used to combine multiple keys ->":" */
	public static final String KEYS_SEPARATOR = ":";

	/** Separator used to combine value-> "|" */
	public static final String VALUE_SEPARATOR = "|";
	
	/** Separator used to combine collection element-> "," */
	public static final String COLLECT_ELM_SEPARATOR = ",";
	
	/** the builder name : hbase */
	public static final String BUILDER_HBASE = "hbase";
	
	/** the builder name : redis */
	public static final String BUILDER_REDIS = "redis";
	
	/** the configuration key of default builder */
	public static final String CONFIG_DFT_BUILDER = "builder.default";

	/** the configuration key of default builder */
	public static final String CONFIG_CACHE_BUILDER = "builder.cache";
	
	/** the postfix of IBaseAccessor detect package entry key in configuration file */
	public static final String CONFIG_ACCESSOR_PACKAGE = "package.";
	
	/** the cache accessor name*/
	public static final String CACHE_ACCESSOR = "cache.accessor";
}
