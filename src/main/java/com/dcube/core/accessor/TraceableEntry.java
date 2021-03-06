package com.dcube.core.accessor;

import java.util.Date;

import com.dcube.core.EntryKey;
import com.dcube.core.ITraceable;
import com.dcube.core.TraceInfo;
import com.dcube.meta.EntityConstants.TraceableEnum;

/**
 * TraceableEntry 
 **/
public class TraceableEntry extends EntityEntry implements ITraceable{

	/**
	 * Default constructor
	 **/
	public TraceableEntry (){
		
		super();
		setNewCreate(new Date(System.currentTimeMillis()));
		setLastModify(new Date(System.currentTimeMillis()));
	}
	
	public TraceableEntry (String entityName,String key){
		
		super(entityName, key);
		setNewCreate(new Date(System.currentTimeMillis()));
		setLastModify(new Date(System.currentTimeMillis()));
	}
	
	public TraceableEntry (EntryKey entryKey){
		
		super(entryKey);
		setNewCreate(new Date(System.currentTimeMillis()));
		setLastModify(new Date(System.currentTimeMillis()));
	}
	
	@Override
	public String getCreator() {
		
		return super.getAttrValue(TraceableEnum.Creator.attribute, String.class);
	}

	@Override
	public void setCreator(String creator) {
		
		super.setAttrValue(TraceableEnum.Creator.attribute, creator);
		
	}

	@Override
	public String getModifier() {
		
		return super.getAttrValue(TraceableEnum.Modifier.attribute, String.class);
	}

	@Override
	public void setModifier(String modifier) {
		
		super.setAttrValue(TraceableEnum.Modifier.attribute, modifier);
	}

	@Override
	public Date getNewCreate() {
		
		return super.getAttrValue(TraceableEnum.NewCreate.attribute, Date.class);
	}

	@Override
	public void setNewCreate(Date newCreate) {

		super.setAttrValue(TraceableEnum.NewCreate.attribute, newCreate);
	}

	@Override
	public Date getLastModify() {
		
		return super.getAttrValue(TraceableEnum.LastModify.attribute, Date.class);
	}

	@Override
	public void setLastModify(Date lastModify) {

		super.setAttrValue(TraceableEnum.LastModify.attribute, lastModify);
	}
	
	/**
	 * Get TraceInfo object 
	 **/
	public TraceInfo getTraceInfo(){
		
		String creator = getAttrValue(TraceableEnum.Creator.attribute, String.class);
		String modifier = getAttrValue(TraceableEnum.Modifier.attribute, String.class);
		Date newCreate = getAttrValue(TraceableEnum.NewCreate.attribute, Date.class);
		Date lastModify = getAttrValue(TraceableEnum.LastModify.attribute, Date.class);
		
		return new TraceInfo(creator, newCreate, modifier, lastModify);
	}
	
	/**
	 * Set TraceInfo object 
	 **/
	public void setTraceInfo(TraceInfo traceInfo){
		
		if(traceInfo == null) return;
		
		super.setAttrValue(TraceableEnum.Creator.attribute, traceInfo.getCreator());
		super.setAttrValue(TraceableEnum.Modifier.attribute, traceInfo.getModifier());
		super.setAttrValue(TraceableEnum.NewCreate.attribute, traceInfo.getNewCreate());
		super.setAttrValue(TraceableEnum.LastModify.attribute, traceInfo.getLastModify());
	}
}
