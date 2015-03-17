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
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum MetaInfo{

		SchemaClass("i_schema_class","schemaclass"),
		EntityName("i_entity_name" , "entityname"),
		Description("i_description","description"),
		Traceable("i_traceable","traceable"),
		Attributes("i_attributes","attributes"),
		Schema("i_schema","schema"),
		AccessorClass("i_accessor_class","accessorclass");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private MetaInfo(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = "c0";
	    }
		
		private MetaInfo(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The attribute info enumerator 
	 **/
	public static enum AttrInfo{

		AttrName("i_attr_name","attrname"),
		Format("i_format" , "format"),
		Column("i_column","column"),
		Qualifier("i_qualifier","qualifier"),
		Hidden("i_hidden","hidden"),
		Readonly("i_readonly","readonly"),
		Required("i_required","required"),
		Primary("i_primary","primary"),
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
		private AttrInfo(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = "c0";
	    }
		
		private AttrInfo(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum UserInfo{

		Account("i_account","account"),
		Domain("i_domain" , "domain"),
		Name("i_name","name"),
		Source("i_source","source"),
		Password("i_password","password"),
		Salt("i_salt","salt"),
		Groups("i_groups","groups"),
		Roles("i_roles","roles");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private UserInfo(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = "c0";
	    }
		
		private UserInfo(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum GroupInfo{

		Name("i_group_name","groupname"),
		Users("i_users" , "users"),
		Groups("i_groups","groups");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private GroupInfo(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = "c0";
	    }
		
		private GroupInfo(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum RoleInfo{

		Name("i_role_name","rolename"),
		Users("i_users" , "users");
		
		public final String attribute;
		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private RoleInfo(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = "c0";
	    }
		
		private RoleInfo(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
}
