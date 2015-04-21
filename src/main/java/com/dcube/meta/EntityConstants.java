package com.dcube.meta;

/**
 * Define necessary entity names
 * dcube.meta.info table store normal entity and repository entity.
 * the two parts entities is identified by the category field
 **/
public class EntityConstants {

	public static final String ATTR_ACL_COLUMN = "acl";
	
	public static final String ATTR_DFT_COLUMN = "c0";
	
	public static final String ENTITY_PREFIX = "dcube.";
	// the blind entity 
	public static final String ENTITY_BLIND = ENTITY_PREFIX + "blind";
	// the meta info 
	public static final String ENTITY_META_INFO = ENTITY_PREFIX + "meta.info";
	// the meta attr
	public static final String ENTITY_META_ATTR = ENTITY_PREFIX + "meta.attr";
	//the entity name of principal
	public static final String ENTITY_USER = ENTITY_PREFIX + "user";	
	// the entity name of role
	public static final String ENTITY_USER_ROLE = ENTITY_PREFIX + "user.role";	
	// the entity name of group
	public static final String ENTITY_USER_GROUP = ENTITY_PREFIX + "user.group";
	
	public static final String ENTITY_AUDIT = ENTITY_PREFIX + "audit";
	
	//the meta general , key of Accessor 
	public static String ACCESSOR_GENERIC_META = "generic.meta";
	public static String ACCESSOR_GENERIC_USER = "generic.user";
	public static String ACCESSOR_GENERIC_ADMIN = "generic.admin";
	public static String ACCESSOR_GENERIC_GROUP = "generic.group";
	public static String ACCESSOR_GENERIC_ROLE = "generic.role";
	
	public static String ACCESSOR_ENTITY_META  = "entity.meta";
	public static String ACCESSOR_ENTITY_ATTR  = "entity.attr";
	public static String ACCESSOR_ENTITY_USER  = "entity.user";
	public static String ACCESSOR_ENTITY_ROLE  = "entity.role";
	public static String ACCESSOR_ENTITY_GROUP = "entity.group";
	public static String ACCESSOR_ENTITY_AUDIT = "entity.audit";
	
	public static String ACCESSOR_ENTITY_INDEX = "entity.index";
	
	public static String NAME_SEPARATOR = ".";
	
	/** blank key/value of map entry placeholder */
	public static String BLANK_VALUE = "_BLANK_VAL";
	public static String BLANK_KEY   = "_BLANK_KEY";
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum MetaEnum{
		
		EntityClass("i_entity_class","entityclass"),
		EntityName("i_entity_name" , "entityname"),
		Description("i_description","description"),
		Traceable("i_traceable","traceable"),
		Attributes("i_attributes","attributes"),
		Schema("i_schema","schema"),
		AccessorName("i_accessor_name","accessorname"),
		Category("i_category","category");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private MetaEnum(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = ATTR_DFT_COLUMN;
	    }
		
		private MetaEnum(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The attribute info enumerator 
	 **/
	public static enum AttrEnum{

		AttrName("i_attr_name","attrname"),
		Format("i_format" , "format"),
		Column("i_column","column"),
		Qualifier("i_qualifier","qualifier"),
		Hidden("i_hidden","hidden"),
		Readonly("i_readonly","readonly"),
		Required("i_required","required"),
		Indexable("i_indexable","indexable"),
		Entity("i_entity","entity"),
		Type("i_type","type"),
		Mode("i_mode","mode"),
		Description("i_description","description");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private AttrEnum(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = ATTR_DFT_COLUMN;
	    }
		
		private AttrEnum(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum UserEnum{

		Account("i_account","account"),
		Domain("i_domain" , "domain"),
		Name("i_name","name"),
		Source("i_source","source"),
		Password("i_password","password"),
		Salt("i_salt","salt"),
		Groups("i_groups","groups"),
		Roles("i_roles","roles"),
		Profile("i_profile","profile");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private UserEnum(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = ATTR_DFT_COLUMN;
	    }
		
		private UserEnum(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum GroupEnum{

		Name("i_group_name","groupname"),
		Description("i_description","description"),
		Parent("i_parent","parent"),
		Users("i_users" , "users"),
		Groups("i_groups","groups");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private GroupEnum(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = ATTR_DFT_COLUMN;
	    }
		
		private GroupEnum(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum RoleEnum{

		Name("i_role_name","rolename"),
		Description("i_description","description"),
		Groups("i_groups","groups"),
		Users("i_users" , "users");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private RoleEnum(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = ATTR_DFT_COLUMN;
	    }
		
		private RoleEnum(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum TraceableEnum{

		Creator(   "i_creator",    "creator"),
		Modifier(  "i_modifier",   "modifier"),
		LastModify("i_newcreate",  "newcreate"),
		NewCreate( "i_lastmodify", "lastmodify");

		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private TraceableEnum(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = ATTR_DFT_COLUMN;
	    }
		
		private TraceableEnum(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
		
}
