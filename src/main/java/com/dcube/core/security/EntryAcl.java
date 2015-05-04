package com.dcube.core.security;

import java.util.ArrayList;
import java.util.List;

import com.dcube.core.security.AclConstants.PrivilegeEnum;
import com.dcube.core.security.AclConstants.TypeEnum;
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
				if(e.getPrivilege().priority < ace.getPrivilege().priority)
					e.setPrivilege(ace.getPrivilege());
				
				e.grant((String[])ace.getPermissions().toArray());
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
	public List<EntryAce> getAllAces(){
		
		return aces;
	}
	
	/**
	 * Get the user aces
	 * @return the entry ace list 
	 **/
	public List<EntryAce> getUserAces(){
		List<EntryAce> uaces = new ArrayList<EntryAce>();
		
		for(EntryAce e:aces){
			
			if(TypeEnum.User == e.getType())
				uaces.add(e);
		}
		
		return uaces;
	}
		
	/**
	 * Get the group aces
	 * @return the entry ace list 
	 **/
	public List<EntryAce> getGroupAces(){
		
		List<EntryAce> gaces = new ArrayList<EntryAce>();
		
		for(EntryAce e:aces){
			
			if(TypeEnum.Group == e.getType())
				gaces.add(e);
		}
		
		return gaces;
	}

	public EntryAce getEntryAce(TypeEnum type, String name){
		
		for(EntryAce e:aces){
			
			if(type == e.getType() && e.getName().equals(name))
				return e;
		}
		
		return null;
	}
	/**
	 * CheckObject is readable or not
	 * 
	 * @param principal the principal object
	 * @return boolean true:object could be read on behalf of principal; false:not readable
	 **/
	public boolean checkReadable(Principal principal){
		
		PrivilegeEnum readPriv = PrivilegeEnum.NONE;
		
		for(EntryAce ace:aces){
			
			if(TypeEnum.User == ace.getType() && ace.getName().equals(principal.getAccount())){
				
				readPriv = readPriv.priority < ace.getPrivilege().priority ? ace.getPrivilege():readPriv;
				
			}else if(TypeEnum.Group == ace.getType() && principal.inGroup(ace.getName())){
				
				readPriv = readPriv.priority < ace.getPrivilege().priority ? ace.getPrivilege():readPriv;
					
			}
			
			if(readPriv != PrivilegeEnum.NONE)
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

}
