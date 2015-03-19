package com.dcube.core.accessor;

import java.util.Date;

import com.dcube.core.EntryKey;
import com.dcube.core.ITraceable;
import com.dcube.meta.EntityConstants.AccessControlTraceInfo;

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
		
		return super.getAttrValue(AccessControlTraceInfo.Creator.attribute, String.class);
	}

	@Override
	public void setCreator(String creator) {
		
		super.setAttrValue(AccessControlTraceInfo.Creator.attribute, creator);
		
	}

	@Override
	public String getModifier() {
		
		return super.getAttrValue(AccessControlTraceInfo.Modifier.attribute, String.class);
	}

	@Override
	public void setModifier(String modifier) {
		
		super.setAttrValue(AccessControlTraceInfo.Modifier.attribute, modifier);
	}

	@Override
	public Date getNewCreate() {
		
		return super.getAttrValue(AccessControlTraceInfo.NewCreate.attribute, Date.class);
	}

	@Override
	public void setNewCreate(Date newCreate) {

		super.setAttrValue(AccessControlTraceInfo.NewCreate.attribute, newCreate);
	}

	@Override
	public Date getLastModify() {
		
		return super.getAttrValue(AccessControlTraceInfo.LastModify.attribute, Date.class);
	}

	@Override
	public void setLastModify(Date lastModify) {

		super.setAttrValue(AccessControlTraceInfo.LastModify.attribute, lastModify);
	}
}
