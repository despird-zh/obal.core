package com.dcube.serializer;

import com.dcube.meta.EntityAttr;
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
		
		attr.setIndexable(input.readBoolean());
		attr.setHidden(input.readBoolean());
		attr.setReadonly(input.readBoolean());
		attr.setRequired(input.readBoolean());

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
		
		output.writeBoolean(attr.isIndexable());
		output.writeBoolean(attr.isHidden());
		output.writeBoolean(attr.isReadonly());
		output.writeBoolean(attr.isRequired());
		
	}

}
