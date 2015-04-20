package com.dcube.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.dcube.core.IGenericEntry.AttributeItem;
import com.dcube.core.accessor.EntityEntry;
import com.dcube.meta.EntityAttr;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;

public class EntryInfoSerializer extends Serializer<EntityEntry>{
	
	private MapSerializer mapserializer = new MapSerializer();
	private CollectionSerializer listserializer = new CollectionSerializer();
	
	public EntryInfoSerializer(){
		
	}
	@Override
	public EntityEntry read(Kryo kryo, Input input, Class<EntityEntry> clazz) {
		// TODO Auto-generated method stub
		EntityEntry tile = new EntityEntry("","");
	        kryo.reference(tile); // Only necessary if Kryo#setReferences is true AND Tile#something could reference this tile.

	        //tile.something = kryo.readClassAndObject(input);
	        return tile;

	}

	@Override
	public void write(Kryo kryo, Output output, EntityEntry object) {
		Collection<AttributeItem> attrs = object.getAttrItemList();
		ArrayList<AttributeItem> attrList = new ArrayList<AttributeItem>(attrs);
		Collections.sort(attrList,AttrComparator);
		
		kryo.writeClassAndObject(output, object);
	}

	// create comparator
	private static Comparator<AttributeItem> AttrComparator = new Comparator<AttributeItem>(){  
        @Override  
        public int compare(AttributeItem b1, AttributeItem b2) {  
            return b1.attribute().compareTo(b2.attribute());  
        }  
          
    };
}
