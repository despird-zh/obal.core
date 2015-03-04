package com.doccube.core.accessor;

import java.util.Date;
import java.util.List;

import com.doccube.core.EntryKey;
import com.doccube.core.ITraceable;
import com.doccube.meta.EntityAttr;

public class TraceableEntry extends EntryInfo implements ITraceable{

	public TraceableEntry (String entityName,String key){
		
		super(entityName, key);
	}
	
	public TraceableEntry (EntryKey entryKey){
		
		super(entryKey);
	}
	
	@Override
	public String getCreator() {
		
		return super.getAttrValue(ITraceable.ATTR_CREATOR, String.class);
	}

	@Override
	public void setCreator(String creator) {
		
		super.setAttrValue(ITraceable.ATTR_CREATOR, creator);
		
	}

	@Override
	public String getModifier() {
		
		return super.getAttrValue(ITraceable.ATTR_MODIFIER, String.class);
	}

	@Override
	public void setModifier(String modifier) {
		
		super.setAttrValue(ITraceable.ATTR_MODIFIER, modifier);
	}

	@Override
	public Date getNewCreate() {
		
		return super.getAttrValue(ITraceable.ATTR_NEWCREATE, Date.class);
	}

	@Override
	public void setNewCreate(Date newCreate) {

		super.setAttrValue(ITraceable.ATTR_NEWCREATE, newCreate);
	}

	@Override
	public Date getLastModify() {
		
		return super.getAttrValue(ITraceable.ATTR_LASTMOFIFY, Date.class);
	}

	@Override
	public void setLastModify(Date lastModify) {

		super.setAttrValue(ITraceable.ATTR_LASTMOFIFY, lastModify);
	}
}
