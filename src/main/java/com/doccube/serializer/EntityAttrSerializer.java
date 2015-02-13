package com.doccube.serializer;

import java.util.Date;

import com.doccube.meta.EntityAttr;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EntityAttrSerializer  extends Serializer<EntityAttr>{

	@Override
	public EntityAttr read(Kryo kryo, Input input, Class<EntityAttr> targetClazz) {

		String attrname = input.readString();
		String column = input.readString();
		String qualifier = input.readString();
		EntityAttr attr = new EntityAttr(attrname,column,qualifier);
		
		attr.setEntityName(input.readString());
		attr.mode = EntityAttr.AttrMode.valueOf(input.readString());
		attr.type = EntityAttr.AttrType.valueOf(input.readString());
		attr.setDescription(input.readString());
		
		attr.setHidden(input.readBoolean());
		attr.setPrimary(input.readBoolean());
		attr.setReadonly(input.readBoolean());
		attr.setRequired(input.readBoolean());
		
		attr.setCreator(input.readString());
		attr.setModifier(input.readString());
		
		long time = input.readLong();
		if(time != -1l)
		attr.setNewCreate(new Date(time));
		
		time = input.readLong();
		if(time != -1l)
		attr.setLastModify(new Date(time));
		
		return attr;
	}

	@Override
	public void write(Kryo kryo, Output output, EntityAttr attr) {
		
		output.writeString(attr.getAttrName());
		output.writeString(attr.getColumn());
		output.writeString(attr.getQualifier());	
		
		output.writeString(attr.getEntityName());		
		output.writeString(attr.mode.toString());
		output.writeString(attr.type.toString());
		output.writeString(attr.getDescription());
		
		output.writeBoolean(attr.isHidden());
		output.writeBoolean(attr.isPrimary());
		output.writeBoolean(attr.isReadonly());
		output.writeBoolean(attr.isRequired());
		
		output.writeString(attr.getCreator());
		output.writeString(attr.getModifier());
		
		if(attr.getLastModify() == null)
			output.writeLong(-1l);
		else
			output.writeLong(attr.getLastModify().getTime());
		
		if(attr.getNewCreate() == null)
			output.writeLong(-1l);
		else
			output.writeLong(attr.getNewCreate().getTime());
	}

}
