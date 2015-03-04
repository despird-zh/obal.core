package com.doccube.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.doccube.core.accessor.EntryInfo;
import com.doccube.meta.EntityAttr;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;

public class EntryInfoSerializer extends Serializer<EntryInfo>{
	
	private MapSerializer mapserializer = new MapSerializer();
	private CollectionSerializer listserializer = new CollectionSerializer();
	
	public EntryInfoSerializer(){
		
	}
	@Override
	public EntryInfo read(Kryo kryo, Input input, Class<EntryInfo> clazz) {
		// TODO Auto-generated method stub
		EntryInfo tile = new EntryInfo("","");
	        kryo.reference(tile); // Only necessary if Kryo#setReferences is true AND Tile#something could reference this tile.

	        //tile.something = kryo.readClassAndObject(input);
	        return tile;

	}

	@Override
	public void write(Kryo kryo, Output output, EntryInfo object) {
		Collection<EntityAttr> attrs = object.getAttrs();
		ArrayList<EntityAttr> attrList = new ArrayList<EntityAttr>(attrs);
		Collections.sort(attrList,AttrComparator);
		
		kryo.writeClassAndObject(output, object);
	}

	// create comparator
	private static Comparator<EntityAttr> AttrComparator = new Comparator<EntityAttr>(){  
        @Override  
        public int compare(EntityAttr b1, EntityAttr b2) {  
            return b1.getAttrName().compareTo(b2.getAttrName());  
        }  
          
    };
}
