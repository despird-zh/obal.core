package com.dcube.core.accessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dcube.meta.EntityAttr;

/**
 * EntryCollection wrap all the entries returned from scan operation.
 * it implements the Iteratable interface to support for(....) clause.
 * 
 * @author despird-zh
 * @version 0.1 2015-3-1
 * 
 * @see GenericInfo
 **/
public class EntryCollection<GB extends GenericInfo> implements Iterable<GB> {

	private List<EntityAttr> attrlist = null;
	
	private List<GB> entrylist = null;
	
	/**
	 * Default constructor
	 **/
	public EntryCollection(){
		
		this.attrlist = new ArrayList<EntityAttr>();
		entrylist = new ArrayList<GB>();
	}
	
	/**
	 * Constructor with EntityAttr list of every entry item 
	 **/
	public EntryCollection(List<EntityAttr> attrlist){
		
		this.attrlist = attrlist;
		entrylist = new ArrayList<GB>();
	}
	
	/**
	 * Get the EntityAttr list  
	 **/
	public List<EntityAttr> getAttrList(){
		
		return this.attrlist;
	}
	
	/**
	 * Add entry to collection 
	 **/
	public void addEntry(GB item){
		
		if(this.attrlist.size() == 0 ){
			List<EntityAttr> attrs = item.getAttrs();
			attrlist.addAll(attrs);
		}
		
		entrylist.add(item);
	}
	
	/**
	 * Get Iterator object 
	 **/
	@Override
	public Iterator<GB> iterator() {
		
		return entrylist.iterator();
	}

}
