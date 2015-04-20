package com.dcube.core.accessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dcube.core.IGenericEntry;
import com.dcube.core.IGenericEntry.AttributeItem;
import com.dcube.exception.MetaException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityManager;

/**
 * EntryCollection wrap all the entries returned from scan operation.
 * it implements the Iteratable interface to support for(....) clause.
 * 
 * @author despird-zh
 * @version 0.1 2015-3-1
 * 
 * @see GenericEntry
 **/
public class EntryCollection<GB extends IGenericEntry> implements Iterable<GB> {

	private List<AttributeItem> attritemlist = null;
	
	private List<GB> entrylist = null;
	
	/**
	 * Default constructor
	 **/
	public EntryCollection(){
		
		this.attritemlist = new ArrayList<AttributeItem>();
		entrylist = new ArrayList<GB>();
	}
	
	/**
	 * Constructor with EntityAttr list of every entry item 
	 **/
	public EntryCollection(List<EntityAttr> attrlist){
		
		this.attritemlist = new ArrayList<AttributeItem>();
		for(EntityAttr attr: attrlist){
			AttributeItem item = new AttributeItem(attr.getEntityName(),attr.getAttrName());
			this.attritemlist.add(item);
		}
		entrylist = new ArrayList<GB>();
	}
	
	/**
	 * Get the EntityAttr list  
	 **/
	public List<EntityAttr> getAttrList(){
		
		List<EntityAttr> rtv = new ArrayList<EntityAttr>();
		for(AttributeItem item: this.attritemlist){
			EntityAttr attr;
			try {
				attr = EntityManager.getInstance().getEntityAttr(item.entity(), item.attribute());
				rtv.add(attr);
			} catch (MetaException e) {
				
				e.printStackTrace();
			}
		
		}
		return rtv;
	}
	
	/**
	 * Add entry to collection 
	 **/
	public void addEntry(GB item){
		
		if(this.attritemlist.size() == 0 ){
			List<AttributeItem> attrs = item.getAttrItemList();
			attritemlist.addAll(attrs);
		}
		
		entrylist.add(item);
	}
	
	/**
	 * Get the entry list 
	 **/
	public List<GB> getEntryList(){
		
		return this.entrylist;
	}
	
	/**
	 * Check if the collection is null 
	 **/
	public boolean isEmpty(){
		
		return this.entrylist == null | entrylist.size() == 0;
	}
	
	/**
	 * Get Iterator object 
	 **/
	@Override
	public Iterator<GB> iterator() {
		
		return entrylist.iterator();
	}

}
