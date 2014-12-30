package com.obal.repo;

import java.util.List;

import com.obal.core.security.EntryAcl;

public class NodeInfo {
	
	private String nodeName = null;
	
	
	public NodeInfo(String nodeName){
		
		this.nodeName = nodeName;
	}
	
	public String getNodeName(){
		
		return this.nodeName;
	}
	
	public List<NodeInfo> childNodes(){
		return null;
		
	}
	
	public NodeInfo parentNode(){
		return null;	
		
	}
	
	public EntryAcl getEntryAcl(){
		
		return null;
	}
}
