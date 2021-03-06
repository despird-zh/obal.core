package com.dcube.core;

import java.util.List;

import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

/**
 * IGenericInfo is the basic interface for row level data wrapping.
 * 
 * @author despird
 * @version 0.1 2014-3-1
 * 
 **/
public interface IGenericEntry{

	/**
	 * Get the attribute of specified attribute
	 **/
	public AttributeItem getAttrItem(String entityname,String attrname);

	/**
	 * Get the AttributeItem List
	 **/
	public List<AttributeItem> getAttrItemList();
	
	/**
	 * Get the changed AttributeItem List,
	 * changed AttributeItem means be set new value after initial.
	 **/
	public List<AttributeItem> getChangedAttrItemList();
	
	/**
	 * Get the value of attribute as specified type
	 * @param entityname
	 * @param attrname 
	 **/
	public <K> K getAttrValue(String entityname,String attrname, Class<K> type);
	
	/**
	 * Get the value of attribute as Object
	 * @param entityname
	 * @param attrname 
	 **/
	public Object getAttrValue(String entityname,String attrname);

	/**
	 * Set attribute value
	 **/
	public void setAttrValue(EntityAttr attribute, Object value);
	
	/**
	 * Set attribute value
	 **/
	public void setAttrValue(String entityname, String attrname, Object value);
	
	/**
	 * Check attribute null or not 
	 **/
	public boolean checkRequired(String entityname, String attrname);
	
	/**
	 * Inner class to wrap value and attribute, it record the value once changed or not.
	 * so as to keep the trace information
	 **/
	public static class AttributeItem implements Cloneable{
		
		/**
		 * AttribueItem to hold the attribute data 
		 **/
		public AttributeItem(String entityname, String attrname){
			
			this.entityname = entityname;
			this.attrname = attrname;
			
		}
		
		/**
		 * AttribueItem to hold the attribute data 
		 **/
		public AttributeItem(String entityname, String attrname, Object value){
			
			this.entityname = entityname;
			this.attrname = attrname;
			this.currentVal = value;
			
		}
		
		/**
		 * Get the full name of attribute 
		 * eg. "[entity name].[attr name]"
		 **/
		public String getFullName(){
			
			return this.entityname + EntityConstants.NAME_SEPARATOR + this.attrname;
		}
		
		private String entityname = null;
		private String attrname = null;
		private Object currentVal = null;
		private Object originVal = null;
		private boolean changed = false;
		
		/**
		 * Get the entity name 
		 **/
		public String entity(){
			
			return this.entityname;
		}
		
		/**
		 * Get the attribute name 
		 **/
		public String attribute(){
			
			return this.attrname;
		}
		
		/**
		 * Get the value  
		 **/
		public Object value(){
			
			return this.currentVal;
		}
		
		/**
		 * Get the orininal value 
		 **/
		public Object originValue(){
			
			return this.originVal;
		}
		
		/**
		 * Set the current value 
		 **/
		public void setValue(Object newVal){

			this.currentVal = newVal;
		}
		
		/**
		 * Set the new value and changed flag 
		 * 
		 **/
		public void setNewValue(Object newVal){
			if(!changed){
				this.originVal = this.currentVal;// save original value
				changed = true;
			}
			this.currentVal = newVal;
		}
		
		/**
		 * check if the value once changed 
		 **/
		public boolean isChanged(){
			return changed;
		}
		
		/**
		 * Get original value 
		 **/
		public Object getOriginalValue(){
			return this.originVal;
		}
		
		/**
		 * Set changed flag 
		 **/
		public void setChanged(boolean changed){
			this.changed = changed;
		}
		
		@Override
	    public Object clone() {
	       
			AttributeItem rtv = new AttributeItem(this.entityname,
					this.attrname,
					this.currentVal);
			
			rtv.changed = this.changed;
			rtv.originVal = this.originVal;
			
			return rtv;
	    }
	}
}
