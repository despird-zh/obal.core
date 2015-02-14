package com.doccube.dsl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

public class HScan<R> implements IHScan<R>,IOperation<R>{

	List<String> attrList = new ArrayList<String>();
	IHAttrFilter attrFilter = null;
	IHScanFilters<R> filters = null;
	
	private String schema;
	
	public HScan(String schema){
		
		this.schema = schema;
	}
	
	public IHScan<R> attribute(String... attrs){
		
		for(String attr:attrs)
			this.attrList.add(attr);
		
		return this;
	}
	
	public IHScan<R> attribute(IHAttrFilter attrFilter){
		
		this.attrFilter = attrFilter;
		
		return this;
	}
	
	@Override
	public IHScanFilters<R> filters(String operator) {

		this.filters = new HScanFilters<R>(this,operator);
		
		return this.filters;
	}

	@Override
	public IHScanFilters<R> filters() {

		return this.filters;
	}
	
	public IHScanFilter<R> filter(String attr) {
		
		if(null == this.filters)
		this.filters = new HScanFilters<R>(this,null);
		return this.filters.filter(attr);
	}
	
	@Override
	public IHScan<R> getHScan() {
		return this;
	}
	
	@Override
	public R execute() {
		return null;
	}

	@Override
	public String toJson(){
		
		JsonFactory jfactory = new JsonFactory();
		JsonGenerator jGenerator;
		StringWriter writer = new StringWriter();
		try {
			jGenerator = jfactory.createJsonGenerator(writer);
			jGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
			jGenerator.writeStartObject();//{
				jGenerator.writeStringField("entity", this.schema);// "schema":"schema-val",
				jGenerator.writeFieldName("filters"); // "filters" : 
				jGenerator.writeStartObject(); // {
					HScanFilters<R> fs = (HScanFilters<R>) this.filters;
					jGenerator.writeStringField("operator", fs.getOperator()); // "operator":"oper-val"
					jGenerator.writeFieldName("items");
						Set<IHScanFilter<R>> filterSet = fs.filterSet();
						jGenerator.writeStartArray();//"["
						for(IHScanFilter<R> filter: filterSet){
							
							HScanFilter<R> f = (HScanFilter<R>) filter;
							jGenerator.writeStartObject(); // {
							jGenerator.writeStringField("attr", f.attr);
							jGenerator.writeStringField("op", f.operator);
							jGenerator.writeStringField("value", f.value);
							jGenerator.writeEndObject();//}
						}
						jGenerator.writeEndArray();//"]"
						
				jGenerator.writeEndObject();// }
				
				jGenerator.writeFieldName("attrs");
					List<String> attrs = this.attrList;
					jGenerator.writeStartArray();//"["
					for(String attr: attrs){
						
						jGenerator.writeString(attr);
					}
					jGenerator.writeEndArray();//"]"
			
			jGenerator.writeEndObject();//}
			jGenerator.close();
			
			System.out.println(writer.toString());
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return writer.toString();
	}
	
	public void fromJson(String json){
		
		try {

			JsonFactory jfactory = new JsonFactory();
			/*** read from file ***/
			JsonParser jParser = jfactory.createJsonParser(json);

			// loop until token equal to "}"
			while (jParser.nextToken() != JsonToken.END_OBJECT) {

				String fieldname = jParser.getCurrentName();
				if ("entity".equals(fieldname)) {
					jParser.nextToken();
					this.schema = jParser.getText();
					
					System.out.println("==entity:"+jParser.getText());
				}

				else if ("filters".equals(fieldname)) {
					jParser.nextToken();
					fieldname = jParser.getCurrentName();
					IHScanFilters<R> filters = null;
					while (jParser.nextToken() != JsonToken.END_OBJECT) {
						fieldname = jParser.getCurrentName();
						if ("operator".equals(fieldname)) {
							jParser.nextToken();
							filters = this.filters(jParser.getText());
						}
						if ("items".equals(fieldname)) {
							jParser.nextToken();
							while (jParser.nextToken() != JsonToken.END_ARRAY) {
								
								String attr = null;
								String value = null;
								String op = null;
								while(jParser.nextToken() != JsonToken.END_OBJECT){
									fieldname = jParser.getCurrentName();
									jParser.nextToken();
									String fieldval = jParser.getText();
									if("attr".equals(fieldname))
										attr = fieldval;
									else if("op".equals(fieldname))
										op = fieldval;
									else if("value".equals(fieldname))
										value = fieldval;
									
									System.out.println("==filer:"+fieldname+"/"+fieldval);
								}
								filters.filter(attr).eq(value);
								System.out.println("== one item");
							}
						}
					}
				}

				if ("attrs".equals(fieldname)) {
					jParser.nextToken(); // current token is "[", move next
					// messages is array, loop until token equal to "]"
					while (jParser.nextToken() != JsonToken.END_ARRAY) {

						// display msg1, msg2, msg3
						System.out.println(jParser.getText());
						this.attribute(jParser.getText());
					}
				}
			}
			jParser.close();

		} catch (JsonGenerationException e) {

			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		
	}
	/**
	 * HScanDelegate defines the normal function of scan delegate object
	 * it will be the base class of IHScanFilter and IHScanFilters object.
	 * @author despird
	 * @version 0.1 2014-2-1
	 **/
	protected static class HScanDelegate<D> implements IHScan<D>{

		private IHScan<D> scan = null;
		public HScanDelegate(IHScan<D> scan){
			
			this.scan = scan;
		}
		
		@Override
		public IHScan<D> attribute(String... attrs) {
			return this.scan.attribute(attrs);
		}

		@Override
		public IHScan<D> attribute(IHAttrFilter attrs) {
			return this.scan.attribute(attrs);
		}

		@Override
		public IHScanFilters<D> filters(String Operator) {
			return this.filters(Operator);
		}

		@Override
		public IHScanFilters<D> filters() {
			return this.filters();
		}
		
		@Override
		public IHScan<D> getHScan() {
			return this.scan;
		}
	}

}
