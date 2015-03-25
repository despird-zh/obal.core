package com.dcube.core.security;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import com.dcube.core.CoreConstants;
import com.dcube.exception.SecurityException;
/**
 * EntryAcl is the entry access control list, item of it is access control setting for visitor
 * 
 * @author despird-zh
 * @version 0.1 2014-3-1
 * @since 0.1
 * 
 * @see EntryAce
 **/
public class EntryAcl {

	public static int PERFER_PRIVILEGE = 11;
	public static int PERFER_GRANULAR = 12;
	
	private List<EntryAce> aces = null;
	
	/**
	 * Constructor with acl name 
	 * 
	 * @param aclName the acl name
	 **/
	public EntryAcl(){
		aces = new ArrayList<EntryAce>();
	}
	
	/**
	 * Constructor with acl name and ace array
	 * 
	 * @param aclName the acl name
	 * @param aceArray the ace array
	 **/
	@JsonCreator
	public EntryAcl(EntryAce ... aceArray){
		aces = new ArrayList<EntryAce>();
		if(null == aceArray)
			return;
		else{
			
			for(EntryAce ace:aceArray){
				aces.add(ace);
			}
		}
	}

	/**
	 * Add EntryAce to Acl
	 * 
	 * @param ace the access control entry
	 * @param merge true:merge;false override.
	 **/
	public void addEntryAce(EntryAce ace,boolean merge){
		
		int i = this.aces.indexOf(ace);
		if(i > -1){
			// exist
			EntryAce e = this.aces.get(i);
			if(merge){// merge over original
				if(e.privilege().priority() < ace.privilege().priority())
					e.setPrivilege(ace.privilege());
				
				e.grant((String[])ace.permissions().toArray());
			}else{// replace original
				aces.remove(i);
				aces.add(ace);
			}
		}else{
			// none
			this.aces.add(ace);
		}
	}
	
	/**
	 * Get all ace list
	 **/
	@JsonProperty("acelist")
	public List<EntryAce> allAces(){
		
		return aces;
	}
	
	/**
	 * Get the user aces
	 * @return the entry ace list 
	 **/
	public List<EntryAce> userAces(){
		List<EntryAce> uaces = new ArrayList<EntryAce>();
		
		for(EntryAce e:aces){
			
			if(CoreConstants.ACE_TYPE_USER.equals(e.type()))
				uaces.add(e);
		}
		
		return uaces;
	}
	
	/**
	 * Get the role aces
	 * @return the entry ace list 
	 **/
	public List<EntryAce> roleAces(){
		
		List<EntryAce> races = new ArrayList<EntryAce>();
		
		for(EntryAce e:aces){
			
			if(CoreConstants.ACE_TYPE_ROLE.equals(e.type()))
				races.add(e);
		}
		
		return races;
	}
	
	/**
	 * Get the group aces
	 * @return the entry ace list 
	 **/
	public List<EntryAce> groupAces(){
		
		List<EntryAce> gaces = new ArrayList<EntryAce>();
		
		for(EntryAce e:aces){
			
			if(CoreConstants.ACE_TYPE_GROUP.equals(e.type()))
				gaces.add(e);
		}
		
		return gaces;
	}

	/**
	 * CheckObject is readable or not
	 * 
	 * @param principal the principal object
	 * @return boolean true:object could be read on behalf of principal; false:not readable
	 **/
	public boolean checkReadable(Principal principal){
		
		AclPrivilege readPriv = AclPrivilege.NONE;
		
		for(EntryAce ace:aces){
			
			if(CoreConstants.ACE_TYPE_USER.equals(ace.type()) && ace.name().equals(principal.getAccount())){
				
				readPriv = readPriv.priority() < ace.privilege().priority() ? ace.privilege():readPriv;
				
			}else if(CoreConstants.ACE_TYPE_GROUP.equals(ace.type()) && principal.inGroup(ace.name())){
				
				readPriv = readPriv.priority() < ace.privilege().priority() ? ace.privilege():readPriv;
					
			}else if(CoreConstants.ACE_TYPE_ROLE.equals(ace.type()) && principal.inRole(ace.name())){
				
				readPriv = readPriv.priority() < ace.privilege().priority() ? ace.privilege():readPriv;
					
			}
			
			if(readPriv != AclPrivilege.NONE)
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		
		return hashCode() == other.hashCode();
	}
	
	@Override
	public int hashCode() {
		
		int sumAces = 0;
		if(null != aces){
			for(EntryAce ace:aces){
				
				sumAces += ace.hashCode();
			}
		}
		
		return sumAces;
	}
	
	private static ObjectMapper jsonMapper = null;
	
	/**
	 * Read json string into EntryAcl object
	 * 
	 * @param aclJsonStr the acl json String
	 * @return EntryAcl the acl object
	 **/
	@Deprecated
	public static EntryAcl readJson(String aclJsonStr)throws SecurityException{
		
		EntryAcl entryAcl = null;
		if(null == jsonMapper){
			
			jsonMapper = new ObjectMapper();
		}		
		try {
			
			entryAcl = jsonMapper.readValue(aclJsonStr, EntryAcl.class);
			
		} catch (Exception e) {

			throw new SecurityException("Error when parse acl from json:{}", e, aclJsonStr);
		}
		
		return entryAcl;
	}
	
	/**
	 * Write entry acl object into json string
	 * 
	 * @param entryAcl the entry acl object
	 * @return String the json string of entry acl
	 **/
	@Deprecated
	public static String writeJson(EntryAcl entryAcl)throws SecurityException{
		
		String jsonStr = null;
		if(null == jsonMapper){
			
			jsonMapper = new ObjectMapper();
		}
		try {
			
			jsonStr = jsonMapper.writeValueAsString(entryAcl);
			
		} catch (Exception e) {
			
			throw new SecurityException("Error when wrap acl to json:{}", e, jsonStr);
		}
		
		return jsonStr;
	}
}
