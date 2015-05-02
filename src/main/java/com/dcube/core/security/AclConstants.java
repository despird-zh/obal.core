package com.dcube.core.security;

/**
 * Define the constants used in Access control 
 **/
public class AclConstants {
	
	/** ACL column family */
	public static final String CF_ACL = "acl";
	/** user qualifier prefix  */
	public static final String QL_USER_PREFIX = "u:";
	/** group qualifier prefix  */
	public static final String QL_GROUP_PREFIX = "g:";
	/** other qualifier prefix  */
	public static final String QL_OTHER_PREFIX = "o:";
	/** owner qualifier  */
	public static final String QL_OWNRER = "owner";
	
	private static String ABBR_NONE = "n";
	private static String ABBR_BROWSE = "b";
	private static String ABBR_READ = "r";
	private static String ABBR_WRITE = "w";
	private static String ABBR_DELETE = "d";
	
	/** privilege enum  */
	public static enum PrivilegeEnum {

		NONE(ABBR_NONE,0),
		BROWSE(ABBR_BROWSE,1),
		READ(ABBR_READ,2),
		WRITE(ABBR_WRITE,3),
		DELETE(ABBR_DELETE,4);
		
		public final String abbr;
		public final int priority;
		
		/**
		 * Hide Rtype default constructor 
		 **/
		private PrivilegeEnum(String abbr,int priority){  
			this.abbr = abbr;
			this.priority = priority;
	    }
				
		@Override
		public String toString(){
			return this.abbr;
		}

	}
	
	/**
	 * Convert the privilege string into Privilege Enum 
	 **/
	public static PrivilegeEnum convert(String abbr){
		
		if(ABBR_NONE.equals(abbr)) return PrivilegeEnum.NONE;
		if(ABBR_BROWSE.equals(abbr)) return PrivilegeEnum.BROWSE;
		if(ABBR_READ.equals(abbr)) return PrivilegeEnum.READ;
		if(ABBR_WRITE.equals(abbr)) return PrivilegeEnum.WRITE;
		if(ABBR_DELETE.equals(abbr)) return PrivilegeEnum.DELETE;
		
		return PrivilegeEnum.NONE;
	}
	
	/** The Ace type enumerator */
	public static enum TypeEnum{

		User( "u",3),
		Group( "g",1);

		public final String abbr;
		public final int priority;
		/**
		 * Hide default constructor 
		 **/
		private TypeEnum( String abbr, int priority){  
			this.abbr = abbr;
			this.priority = priority;
	    }
		
	}
}
