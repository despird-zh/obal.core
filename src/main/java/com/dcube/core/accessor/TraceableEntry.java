package com.dcube.core.accessor;

import java.util.Date;

import com.dcube.core.EntryKey;
import com.dcube.core.ITraceable;
import com.dcube.meta.EntityConstants.TraceableInfo;

/**
 * TraceableEntry 
 **/
public class TraceableEntry extends EntryInfo implements ITraceable{

	public TraceableEntry (String entityName,String key){
		
		super(entityName, key);
	}
	
	public TraceableEntry (EntryKey entryKey){
		
		super(entryKey);
	}
	
	@Override
	public String getCreator() {
		
		return super.getAttrValue(TraceableInfo.Creator.attribute, String.class);
	}

	@Override
	public void setCreator(String creator) {
		
		super.setAttrValue(TraceableInfo.Creator.attribute, creator);
		
	}

	@Override
	public String getModifier() {
		
		return super.getAttrValue(TraceableInfo.Modifier.attribute, String.class);
	}

	@Override
	public void setModifier(String modifier) {
		
		super.setAttrValue(TraceableInfo.Modifier.attribute, modifier);
	}

	@Override
	public Date getNewCreate() {
		
		return super.getAttrValue(TraceableInfo.NewCreate.attribute, Date.class);
	}

	@Override
	public void setNewCreate(Date newCreate) {

		super.setAttrValue(TraceableInfo.NewCreate.attribute, newCreate);
	}

	@Override
	public Date getLastModify() {
		
		return super.getAttrValue(TraceableInfo.LastModify.attribute, Date.class);
	}

	@Override
	public void setLastModify(Date lastModify) {

		super.setAttrValue(TraceableInfo.LastModify.attribute, lastModify);
	}
}
