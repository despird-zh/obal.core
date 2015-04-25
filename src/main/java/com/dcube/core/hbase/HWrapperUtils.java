package com.dcube.core.hbase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.CoreConstants;
import com.dcube.core.EntryKey;
import com.dcube.exception.WrapperException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

public class HWrapperUtils {

	protected static ObjectMapper objectMapper = new ObjectMapper();
	
	public static Logger LOGGER = LoggerFactory.getLogger(HWrapperUtils.class);

	public static final byte[] JMAP_VAL = "{}".getBytes();
	public static final byte[] JSET_VAL = "[]".getBytes();
	public static final byte[] JLIST_VAL = "[]".getBytes();
	public static final byte[] NULL_VAL = new byte[0];
	/**
	 * Get primitive value from cell, primitive means int,long,double,string,date
	 * 
	 * @param attr the attribute of entry
	 * @param cell the Cell of certain Row in hbase
	 * 
	 * @return Object the value object
	 **/
	public static Object getPrimitiveValue(EntityAttr attr, byte[] value)throws WrapperException{
		
		Object rtv = null;

		switch(attr.type){
			case INTEGER:
				rtv = Bytes.toInt(value);
				break;
			case BOOL:
				rtv = Bytes.toBoolean(value);
				break;
			case DOUBLE:
				rtv = Bytes.toDouble(value);
				break;
			case LONG:
				rtv = Bytes.toLong(value);
				break;
			case STRING:
				rtv = Bytes.toString(value);
				break;
			case DATE:
				Long time = Bytes.toLong(value);
				rtv = new Date(time);
				break;
			default:
				break;
		}
		if(LOGGER.isDebugEnabled()){
			
			LOGGER.debug("Get Primitive -> attribute:{} | value:{}", new String[]{attr.getAttrName(),String.valueOf(rtv)});
		}
		return rtv;
	}
	
	/**
	 * Get map value from cells, every cell is the entry of map
	 * 
	 * @param attr the attribute of entry
	 * @param cells the Cells of certain Row in hbase
	 * 
	 * @return Object the map object
	 **/
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getJMapValue(EntityAttr attr, byte[] value)throws WrapperException{
		Map<String,?> map = null;
		String jsonStr = Bytes.toString(value);
		try{
			ObjectReader oReader = null;
			switch(attr.type){
				case INTEGER:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Integer>>(){});					
					map = oReader.readValue(jsonStr);	
			
					break;
				case BOOL:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Boolean>>(){});					
					map = oReader.readValue(jsonStr);	
					
					break;
				case DOUBLE:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Double>>(){});					
					map = oReader.readValue(jsonStr);	
					break;
				case LONG:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Long>>(){});					
					map = oReader.readValue(jsonStr);	
					break;
				case STRING:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,String>>(){});					
					map = oReader.readValue(jsonStr);	
					break;
				case DATE:
					oReader=objectMapper.reader(new TypeReference<HashMap<String,Date>>(){});					
					map = oReader.readValue(jsonStr);	
					
					break;
				default:
					
					break;
			}
			
			if(LOGGER.isDebugEnabled()){
						
				LOGGER.debug("Get Map -> attribute:{} - value:{}", 
								new Object[]{attr.getAttrName(), jsonStr});
			}
			
		}catch(Exception e){
			
			throw new WrapperException("Error when wrap set value",e);
		}
				
		return (Map<String,Object>)map;
		
	}

	/**
	 * Get list value from cells, every cell is the entry of map
	 * 
	 * @param attr the attribute of entry
	 * @param cells the Cells of certain Row in hbase
	 * 
	 * @return Object the list object
	 **/
	@SuppressWarnings("unchecked")
	public static List<Object> getJListValue(EntityAttr attr, byte[] value)throws WrapperException{
		List<?> list = null;

		String jsonStr = Bytes.toString(value);
		try{
			ObjectReader oReader = null;
			switch(attr.type){
				case INTEGER:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Integer>>(){});					
					list = (jsonStr == null)? new ArrayList<Integer>() : (List<?>)oReader.readValue(jsonStr);					
					break;
					
				case BOOL:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Boolean>>(){});					
					list = (jsonStr == null)? new ArrayList<Boolean>() : (List<?>)oReader.readValue(jsonStr);	
					
					break;
				case DOUBLE:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Double>>(){});					
					list = (jsonStr == null)? new ArrayList<Double>() : (List<?>)oReader.readValue(jsonStr);	
					break;
				case LONG:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Long>>(){});					
					list = (jsonStr == null)? new ArrayList<Long>() : (List<?>)oReader.readValue(jsonStr);	
					break;
				case STRING:
					oReader=objectMapper.reader(new TypeReference<ArrayList<String>>(){});					
					list = (jsonStr == null)? new ArrayList<String>() : (List<?>)oReader.readValue(jsonStr);	
					break;
				case DATE:
					oReader=objectMapper.reader(new TypeReference<ArrayList<Date>>(){});					
					list = (jsonStr == null)? new ArrayList<Date>() : (List<?>)oReader.readValue(jsonStr);	
					
					break;
				default:
					
					break;
			}
			
			if(LOGGER.isDebugEnabled()){
						
				LOGGER.debug("Get List -> attribute:{} - value:{}", 
								new Object[]{attr.getAttrName(), jsonStr});
			}
			
		}catch(Exception e){
			
			throw new WrapperException("Error when wrap set value",e);
		}
				
		return (List<Object>)list;
		
	}


	/**
	 * Get Set value from cells, every cell is the element of set
	 * 
	 * @param attr the attribute of entry
	 * @param cells the Cells of certain Row in hbase
	 * 
	 * @return Object the list object
	 **/
	@SuppressWarnings("unchecked")
	public static Set<Object> getJSetValue(EntityAttr attr, byte[] value)throws WrapperException{
		
		Set<?> set = null;
		String jsonStr = Bytes.toString(value);
		try{
			ObjectReader oReader = null;
			switch(attr.type){
				case INTEGER:
					oReader=objectMapper.reader(new TypeReference<HashSet<Integer>>(){});					
					set = (jsonStr == null)? new HashSet<Integer>() : (Set<?>) oReader.readValue(jsonStr);
					
					break;
				case BOOL:
					oReader=objectMapper.reader(new TypeReference<HashSet<Boolean>>(){});					
					set = (jsonStr == null)? new HashSet<Boolean>() : (Set<?>) oReader.readValue(jsonStr);
					
					break;
				case DOUBLE:
					oReader=objectMapper.reader(new TypeReference<HashSet<Double>>(){});					
					set = (jsonStr == null)? new HashSet<Double>() : (Set<?>) oReader.readValue(jsonStr);
					break;
				case LONG:
					oReader=objectMapper.reader(new TypeReference<HashSet<Long>>(){});					
					set = (jsonStr == null)? new HashSet<Long>() : (Set<?>) oReader.readValue(jsonStr);
					break;
				case STRING:
					oReader=objectMapper.reader(new TypeReference<HashSet<String>>(){});					
					set = (jsonStr == null)? new HashSet<String>() : (Set<?>) oReader.readValue(jsonStr);
					break;
				case DATE:
					oReader=objectMapper.reader(new TypeReference<HashSet<Date>>(){});					
					set = (jsonStr == null)? new HashSet<Date>() : (Set<?>) oReader.readValue(jsonStr);
					
					break;
				default:
					
					break;
			}
			
			if(LOGGER.isDebugEnabled()){
						
				LOGGER.debug("Get Set -> attribute:{} - value:{}", 
								new Object[]{attr.getAttrName(), jsonStr});
			}
			
		}catch(Exception e){
			
			throw new WrapperException("Error when wrap set value",e);
		}
				
		return (Set<Object>)set;
	}
	
	/**
	 * Get Flag map from the Result 
	 * 
	 * @param rawEntry
	 * @param attrs
	 * @return The array of map
	 *
	public static Map<String,?>[] getFMapValue(Result rawEntry,EntityAttr ... attrs )throws WrapperException{
		Map<String,?> map = null;
		Map<String,?>[] rtv = null;
		CellScanner cs = rawEntry.cellScanner();
		byte[] separator = CoreConstants.KEYS_SEPARATOR.getBytes();
		byte[] defaultCol = EntityConstants.ATTR_DFT_COLUMN.getBytes();
		
		HashMap<String, VarsHolder<Map<String,?>>> attrMap = new HashMap<String,VarsHolder<Map<String,?>>>();
		for(int i = 0; i< attrs.length; i++){
			
			switch(attrs[i].type){
				case BOOL:
					map = new HashMap<String, Boolean>();
					break;
				case INTEGER:
					map = new HashMap<String, Integer>();
					break;
				case DOUBLE:
					map = new HashMap<String, Double>();
					break;
				case LONG:
					map = new HashMap<String, Long>();
					break;
				case STRING:
					map = new HashMap<String, String>();
					break;
				case DATE:
					map = new HashMap<String, Date>();
					break;
				default :
					map = null;
			}
			// new variables holder
			VarsHolder<Map<String,?>> holder = new VarsHolder<Map<String,?>>();
			holder.variable = map;
			holder.attr = attrs[i];
			attrMap.put(attrs[i].getQualifier(),holder);
		}// keep the varholder
		try{
			
			while(cs.advance()){
				Cell cell = cs.current();
				
				byte[] column = cell.getFamilyArray();
				byte[] qualifier = cell.getQualifierArray();
				int separatorPos = Bytes.indexOf(qualifier, separator);
				// found cell with qualifier within column family.
				if(separatorPos > 0	&& Bytes.equals(column, defaultCol)){
					
					// qualifier is the attribute name
					qualifier = Bytes.copy(qualifier, 0, separatorPos);
					if(attrMap.containsKey(Bytes.toString(qualifier))){
						// key of map
						byte[] mapKey = Bytes.copy(qualifier, separatorPos + separator.length, qualifier.length);
						Map<String,Object> vmap = (Map<String,Object> )attrMap.get(Bytes.toString(qualifier)).variable;
						EntityAttr attr = attrMap.get(Bytes.toString(qualifier)).attr;
						Object value = null;
						switch (attr.type){
							case BOOL:
								value = Bytes.toBoolean(cell.getValueArray());
								break;
							case INTEGER:
								value = Bytes.toInt(cell.getValueArray());
								break;
							case DOUBLE:
								value = Bytes.toDouble(cell.getValueArray());
								break;
							case LONG:
								value = Bytes.toLong(cell.getValueArray());
								break;
							case STRING:
								value = Bytes.toString(cell.getValueArray());
								break;
							case DATE:
								Long time = Bytes.toLong(cell.getValueArray());
								value = new Date(time);
								break;
							default :
								value = null;						
						}
						// put key-value pair to map
						vmap.put(Bytes.toString(mapKey), value);
						if(LOGGER.isDebugEnabled()){
						
							LOGGER.debug("Get Map.Entry -> attribute:{} - key:{} - value:{}", 
											new Object[]{attr.getAttrName(), Bytes.toString(qualifier),value});
						}
					}
				}
			}
			
			rtv = new HashMap[attrs.length];
			for(int i = 0; i< attrs.length; i++){
				rtv[i] = (Map<String,?> )attrMap.get(attrs[i].getAttrName()).variable;
			}
		}catch(Exception e){
			
			throw new WrapperException("Error when wrap set value",e);
		}
				
		return rtv;
		
	}*/
	
	/**
	 * Put the Primitive value to target Put operation
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 * 
	 **/
	public static void putPrimitiveValue(Put put, EntityAttr attr, Object value){
		byte[] bval = null;
    	//if(value == null) return;    	
    	switch(attr.type){
			case INTEGER:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Integer)value);
				break;
			case BOOL:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Boolean)value);
				break;
			case DOUBLE:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Double)value);
				break;
			case LONG:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Long)value);
				break;							
			case STRING:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((String)value);
				break;
			case DATE:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes(((Date)value).getTime());
				break;						
			default:
				
				break;					
		}
    	put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
	}
	
	/**
	 * Put the map value to target Put operation object
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 **/
	public static void putJMapValue(Put put, EntityAttr attr, Map<String,Object> mapVal)throws WrapperException{
		byte[] bval = null;
    	//if(mapVal == null) return;    	
		String mapJson = null;
		try{
			if(mapVal != null) {
				mapJson = objectMapper.writeValueAsString(mapVal);
				bval = mapJson.getBytes();
			}else{
				bval = JMAP_VAL;
			}
			put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
		}catch(Exception e){
			
			throw new WrapperException("Error when convert Map object to Json",e);
		} 
    	
	}

	/**
	 * Put the list value to target Put operation object
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 **/
	public static void putJListValue(Put put, EntityAttr attr, List<Object> listVal)throws WrapperException{
		byte[] bval = null;
		String listJson = null;
		//if(listVal == null) return;
		try{
			if(listVal == null){
				listJson = objectMapper.writeValueAsString(listVal);
				bval = listJson.getBytes();
			}else{
				bval = JLIST_VAL;
			}
			put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
		}catch(Exception e){
			
			throw new WrapperException("Error when convert List object to Json",e);
		}   	
    	
	}
	

	/**
	 * Put the set value to target Put operation object
	 * 
	 * @param put the Hbase Put operation object
	 * @param attr the target attribute object
	 * @param value the value to be put 
	 **/
	public static void putJSetValue(Put put, EntityAttr attr, Set<Object> setVal)throws WrapperException{
		byte[] bval = null;
		String setJson = null;
		//if(setVal == null) return;
		try{
			if(setVal == null){
				setJson = objectMapper.writeValueAsString(setVal);
				bval = setJson.getBytes();
			}else{
				bval = JSET_VAL;
			}
			put.add(attr.getColumn().getBytes(), attr.getQualifier().getBytes(), bval);
		}catch(Exception e){
			
			throw new WrapperException("Error when convert Set object to Json",e);
		}    	
    	
	}
	
	/**
	 * Convert the attribute value into index key. 
	 **/
	public static byte[] toIndexKey(EntityAttr attr, Object value){
		
		byte[] bval = null;
    	//if(value == null) return;    	
    	switch(attr.type){
			case INTEGER:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Integer)value);
				break;
			case BOOL:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Boolean)value);
				break;
			case DOUBLE:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Double)value);
				break;
			case LONG:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Long)value);
				break;							
			case STRING:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((String)value);
				break;
			case DATE:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes(((Date)value).getTime());
				break;						
			default:
				bval = new byte[0];
				break;					
		}
    	
    	byte[] prefix = Bytes.toBytes(attr.getQualifier() + CoreConstants.KEYS_SEPARATOR);
    	byte[] rtv = new byte[prefix.length + bval.length];
    	System.arraycopy(prefix, 0, rtv, 0, prefix.length);
    	if(bval.length > 0)
    		System.arraycopy(bval, 0, rtv, prefix.length, bval.length);
    	
    	return rtv;
	}

	/**
	 * Convert the attribute value into index entry key. 
	 **/
	public static EntryKey toIndexEntryKey(EntityAttr attr, Object value){
		
		byte[] bval = null;
    	//if(value == null) return;    	
    	switch(attr.type){
			case INTEGER:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Integer)value);
				break;
			case BOOL:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Boolean)value);
				break;
			case DOUBLE:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Double)value);
				break;
			case LONG:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((Long)value);
				break;							
			case STRING:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes((String)value);
				break;
			case DATE:
				bval = (value == null) ? NULL_VAL:Bytes.toBytes(((Date)value).getTime());
				break;						
			default:
				bval = new byte[0];
				break;					
		}
    	
    	String entityName = attr.getEntityName()+EntityConstants.ENTITY_INDEX_POSTFIX;
    	byte[] prefix = Bytes.toBytes(attr.getQualifier() + CoreConstants.KEYS_SEPARATOR);
    	byte[] rtv = new byte[prefix.length + bval.length];
    	System.arraycopy(prefix, 0, rtv, 0, prefix.length);
    	if(bval.length > 0)
    		System.arraycopy(bval, 0, rtv, prefix.length, bval.length);
    	
    	return new EntryKey(entityName, Bytes.toString(rtv));
	}
}
