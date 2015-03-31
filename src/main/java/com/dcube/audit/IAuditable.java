package com.dcube.audit;

/**
 * IAuditable provide methods to collect the audit information.
 * 
 **/
public interface IAuditable {

	public void addPredicate(String name, String value);
	
	public void setSubject(String subject);
	
	public void setOperation();
	
	public void setVerb(String verb);
	
	public void setObject(String object);
	
	public AuditInfo getAuditInfo();
	
	public void auditOn();
	
	public void auditOff();
}
