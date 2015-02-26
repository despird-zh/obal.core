package com.doccube.serializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.doccube.admin.EntitySetup;
import com.doccube.base.BlankTester;
import com.doccube.meta.EntityAttr;
import com.doccube.serializer.EntityAttrSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EntityAttrSerialTest extends BlankTester{
	
	public void testInitializer(){
		Kryo kryo = new Kryo();
		try{
		    Output output = new Output(new FileOutputStream("d:\\file.bin"));
		    EntityAttr attr = new EntityAttr("Demo1","column1","qualifier1");
		    kryo.register(EntityAttr.class, new EntityAttrSerializer());
		    kryo.writeObject(output, attr);
		    output.close();
	
		    Input input = new Input(new FileInputStream("d:\\file.bin"));
		    EntityAttr attr1 = kryo.readObject(input, EntityAttr.class);
		    input.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	 protected void setUp() throws Exception {  
	     initLog4j();  
	     super.setUp();  
	 }  
	  
	 protected void tearDown() throws Exception {  
	    
		 super.tearDown();  
	 }  
}