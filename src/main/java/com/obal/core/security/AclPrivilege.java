package com.obal.core.security;

public enum AclPrivilege {

	NONE("NONE",0),
	BROWSE("BROWSE",1),
	READ("READ",2),
	WRITE("WRITE",3),
	EXECUTE("EXECUTE",4);
	
	private String privilege = null;
	private int priority = -1;
	/**
	 * Hide Rtype default constructor 
	 **/
	private AclPrivilege(String privilege,int priority){  
		this.privilege = privilege;
		this.priority = priority;
    }
	
	public int priority(){
		return this.priority;
	}
	
	@Override
	public String toString(){
		return this.privilege;
	}
	
	@Deprecated
	public byte toByte(){
		
		byte b = (byte)1;
		for(int i = 0; i< this.ordinal();i++)
			b = (byte)(b << 1);
		
		return b;
	}
}
