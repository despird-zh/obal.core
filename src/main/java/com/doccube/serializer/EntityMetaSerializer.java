package com.doccube.serializer;

import com.doccube.meta.EntityMeta;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EntityMetaSerializer extends Serializer<EntityMeta>{

	@Override
	public EntityMeta read(Kryo kryo, Input input, Class<EntityMeta> targettype) {


		return null;
	}

	@Override
	public void write(Kryo kryo, Output output, EntityMeta meta) {


		
	}

}
