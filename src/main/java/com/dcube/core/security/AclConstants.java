package com.dcube.core.security;

/**
 * Define the constants used in Access control 
 **/
public class AclConstants {
	
	/** owner name */
	public static final String OWNER_NAME = "";
	/** ACL column family */
	public static final String CF_ACL = "acl";
	/** owner qualifier prefix  */
	public static final String QL_OWNER_PREFIX = "o:";
	/** user qualifier prefix  */
	public static final String QL_USER_PREFIX = "u:";
	/** group qualifier prefix  */
	public static final String QL_GROUP_PREFIX = "g:";
	/** other qualifier prefix  */
	public static final String QL_OTHER_PREFIX = "e:";
	/** owner qualifier  */
	public static final String QL_OWNRER = "owner";
	
	private static String ABBR_NONE = "n";
	private static String ABBR_BROWSE = "b";
	private static String ABBR_READ = "r";
	private static String ABBR_WRITE = "w";
	private static String ABBR_DELETE = "d";
	
	private static String ABBR_OWNER = "o";
	private static String ABBR_USER = "u";
	private static String ABBR_GROUP = "g";
	private static String ABBR_OTHER = "e";
	
	/** privilege enum  */
	public static enum AcePrivilege {

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
		private AcePrivilege(String abbr,int priority){  
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
	public static AcePrivilege convertPrivilege(String abbr){
		
		if(ABBR_NONE.equals(abbr)) return AcePrivilege.NONE;
		if(ABBR_BROWSE.equals(abbr)) return AcePrivilege.BROWSE;
		if(ABBR_READ.equals(abbr)) return AcePrivilege.READ;
		if(ABBR_WRITE.equals(abbr)) return AcePrivilege.WRITE;
		if(ABBR_DELETE.equals(abbr)) return AcePrivilege.DELETE;
		
		return AcePrivilege.NONE;
	}
	
	/** The Ace type enumerator */
	public static enum AceType{

		User( ABBR_USER,2),
		Owner( ABBR_OWNER,1),
		Other( ABBR_OTHER,4),
		Group( ABBR_GROUP,3);

		public final String abbr;
		public final int priority;
		/**
		 * Hide default constructor 
		 **/
		private AceType( String abbr, int priority){  
			this.abbr = abbr;
			this.priority = priority;
	    }
		
	}
	
	/**
	 * Convert abbr into type enum, because owner don't have special abbr.
	 * for [u] we only return TypeEnum.User.
	 * 
	 **/
	public static AceType convertType(String abbr){
		
		if(ABBR_OWNER.equals(abbr)) return AceType.Owner;
		if(ABBR_USER.equals(abbr)) return AceType.User;
		if(ABBR_GROUP.equals(abbr)) return AceType.Group;
		if(ABBR_OTHER.equals(abbr)) return AceType.Other;

		return AceType.User;
	}
}
