package com.dcube.meta;

/**
 * Define necessary entity names
 *  
 **/
public class EntityConstants {

	public static final String ATTR_ACL_COLUMN = "acl";
	
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
	
	//the meta general , key of Accessor 
	public static String ACCESSOR_GENERIC_META = "generic.meta";
	public static String ACCESSOR_GENERIC_USER = "generic.user";
	public static String ACCESSOR_GENERIC_ADMIN = "generic.admin";
	
	public static String ACCESSOR_ENTITY_META  = "entity.meta";
	public static String ACCESSOR_ENTITY_ATTR  = "entity.attr";
	public static String ACCESSOR_ENTITY_USER  = "entity.user";
	public static String ACCESSOR_ENTITY_ROLE  = "entity.role";
	public static String ACCESSOR_ENTITY_GROUP = "entity.group";
	public static String ACCESSOR_ENTITY_AUDIT = "entity.audit";
	
	public static String NAME_SEPARATOR = ".";
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum MetaInfo{
		
		EntityClass("i_entity_class","entityclass"),
		EntityName("i_entity_name" , "entityname"),
		Description("i_description","description"),
		Traceable("i_traceable","traceable"),
		Attributes("i_attributes","attributes"),
		Schema("i_schema","schema"),
		AccessorName("i_accessor_name","accessorname");
		
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
	
	/**
	 * The meta info enumerator 
	 **/
	public static enum TraceableInfo{

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
		private TraceableInfo(String attribute, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = "c0";
	    }
		
		private TraceableInfo(String attribute, String colfamily, String qualifier){  
			this.attribute = attribute;
			this.qualifier = qualifier;
			this.colfamily = colfamily;
	    }
	}
	
	/**
	 * The Acl info enumerator 
	 **/
	public static enum AclInfo{

		User( "user"),
		Group( "group"),
		Role( "role");

		public final String qualifier;
		public final String colfamily;
		
		/**
		 * Hide default constructor 
		 **/
		private AclInfo( String qualifier){  
			this.qualifier = qualifier;
			this.colfamily = EntityConstants.ATTR_ACL_COLUMN;
	    }
		
	}
	
}
