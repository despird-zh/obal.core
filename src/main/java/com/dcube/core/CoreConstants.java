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
		
	/** the postfix of IBaseAccessor detect package entry key in configuration file */
	public static final String CONFIG_ACCESSOR_PACKAGE = "package.";
	
	/** The config key enumerator */
	public static enum ConfigEnum{
		
		DefaultBuilder("builder.default", BUILDER_HBASE),
		CacheBuilder("builder.cache",BUILDER_REDIS),
		CacheAccessor("cache.accessor","cache.accessor"),
		AdminAccount("admin.account", null),
		AdminPassword("admin.password", null);
		
		public String key; // config key
		public String value; // config value
		
		private ConfigEnum(String key, String value){
			this.key = key;
			this.value = value;
		}
	}
}
