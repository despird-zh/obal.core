package com.dcube.common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * SimpleConverter convert data with simple format
 * <pre>
 * [filed1][char(31)][value1][char(30)][filed2][char(31)][value2][char(30)][char(29)] //-- Line1
 * [filed1][char(31)][value.n][char(30)][filed2][char(31)][value.n][char(30)][char(29)] // -- Line2
 * </pre>  
 * char(31) used as unit separator to combine field name and field value
 * char(30) used as group separator to combine field key-value pair.
 * char(29) used as record separator to combine multiple records.
 **/
public class SimpleConverter {
	
	/** the separator enumerator definition */
	public static enum Separator{
		
		GROUP((char)30),
		RECORD((char)29),
		UNIT((char)31);
		
		/** Character value */
		public final char value;
		/** Byte value of character */
		public final byte[] bvalue = new byte[2];
		
		private Separator(char placeholder){
			value = placeholder;
			bvalue[0] = (byte)(placeholder & 0xFF00 >> 8);
			bvalue[1] = (byte)(placeholder & 0x00FF );
		}
	}
	
	/**
	 * Convert the group map to bytes 
	 **/
	public static byte[] groupMapToBytes(Map<byte[],byte[]> inputMap){
		if(inputMap == null || inputMap.isEmpty()) return new byte[0];
		int capacity = 0;
		for(Map.Entry<byte[],byte[]> elm:inputMap.entrySet()){
			capacity += elm.getKey().length 
					+ elm.getValue().length 
					+ Separator.UNIT.bvalue.length 
					+ Separator.GROUP.bvalue.length;	
		}
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		for(Map.Entry<byte[],byte[]> elm:inputMap.entrySet()){
			
			buffer.put(elm.getKey());
			buffer.put(Separator.UNIT.bvalue);
			if(elm.getValue() != null)
				buffer.put(elm.getValue());
			
			buffer.put(Separator.GROUP.bvalue);
			
		}
		
		return buffer.array();
	}
	
	/**
	 * convert bytes to Group map 
	 **/
	public static Map<byte[],byte[]> bytesToGroupMap(byte[] rawbytes){
		Map<byte[],byte[]> rtv = new HashMap<byte[],byte[]>();
		List<byte[]> groups = splitGroups(rawbytes);
		for(int i = 0 ; i< groups.size(); i++){
			List<byte[]> units = splitUnits(groups.get(i));
			rtv.put(units.get(0), units.get(1));
		}
		
		return rtv;
	}
	
	/**
	 * Convert records to byte array 
	 **/
	public static byte[] recordListToBytes(List<byte[]> rawlist){
		int capacity = 0;
		for(byte[] belm:rawlist){
			capacity += belm.length + Separator.RECORD.bvalue.length;
		}
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		for(byte[] belm:rawlist){
			buffer.put(belm);
			buffer.put(Separator.RECORD.bvalue);
		}
		return buffer.array();
	}
	
	/**
	 * Convert byte array to record list 
	 **/
	public static List<byte[]> bytesToRecordList(byte[] rawbytes){
		List<byte[]> rtv = splitRecords(rawbytes);
		return rtv;
	}
	
	/**
	 * split units of group 
	 **/
	private static List<byte[]> splitUnits(byte[] rawByte){
		List<byte[]> tokens = new ArrayList<byte[]>();
		final byte[] byteArray = Separator.UNIT.bvalue;
		
	    for (int iterator = 0; iterator < rawByte.length - byteArray.length + 1; )
	    {
	        boolean patternFound = true;
	        for (int i = 0; i < byteArray.length; i++)
	        {
	            if (rawByte[iterator + i] != byteArray[i])
	            {
	                patternFound = false;
	                break;
	            }
	        }
	        if (patternFound)
	        {
	            byte[] byteArrayExtracted = new byte[iterator];
	            System.arraycopy(rawByte, 0, byteArrayExtracted, 0, iterator);
	            tokens.add(byteArrayExtracted);
	            int remain = rawByte.length - iterator - byteArray.length;
	            byteArrayExtracted = new byte[remain];
	            System.arraycopy(rawByte, iterator + byteArray.length, byteArrayExtracted, 0, remain);
	            tokens.add(byteArrayExtracted);
	            break;
	        }
	        else
	            iterator++;

	    }
		return tokens;
	}
	
	/**
	 * split record data to group list 
	 **/
	private static List<byte[]> splitGroups(byte[] rawByte)
	{
	    List<byte[]> tokens = new ArrayList<byte[]>();

	    final byte[] byteArray = Separator.GROUP.bvalue;
	    int lastIndex = 0;

	    for (int iterator = 0; iterator < rawByte.length - byteArray.length + 1; )
	    {
	        boolean patternFound = true;
	        for (int i = 0; i < byteArray.length; i++)
	        {
	            if (rawByte[iterator + i] != byteArray[i])
	            {
	                patternFound = false;
	                break;
	            }
	        }
	        if (patternFound)
	        {
	            byte[] byteArrayExtracted = new byte[iterator - lastIndex];
	            System.arraycopy(rawByte, lastIndex, byteArrayExtracted, 0, iterator - lastIndex);
	            iterator += byteArray.length;
	            lastIndex = iterator;
	            tokens.add(byteArrayExtracted);
	        }
	        else
	            iterator++;

	    }

	    return tokens;
	}
	
	/**
	 * split data to record list 
	 **/
	private static List<byte[]> splitRecords(byte[] rawByte)
	{
	    List<byte[]> tokens = new ArrayList<byte[]>();

	    final byte[] byteArray = Separator.RECORD.bvalue;
	    int lastIndex = 0;

	    for (int iterator = 0; iterator < rawByte.length - byteArray.length + 1; )
	    {
	        boolean patternFound = true;
	        for (int i = 0; i < byteArray.length; i++)
	        {
	            if (rawByte[iterator + i] != byteArray[i])
	            {
	                patternFound = false;
	                break;
	            }
	        }
	        if (patternFound)
	        {
	            byte[] byteArrayExtracted = new byte[iterator - lastIndex];
	            System.arraycopy(rawByte, lastIndex, byteArrayExtracted, 0, iterator - lastIndex);
	            iterator += byteArray.length;
	            lastIndex = iterator;
	            tokens.add(byteArrayExtracted);
	        }
	        else
	            iterator++;

	    }

	    return tokens;
	}
	
	// test only
	public static void main(String[] args){
		
		Map<byte[],byte[]> m = new HashMap<byte[],byte[]>();
		m.put("g1".getBytes(), "v1".getBytes());
		m.put("g2".getBytes(), "v22".getBytes());
		m.put("g3".getBytes(), "v333".getBytes());
		m.put("g4".getBytes(), "".getBytes());
		
		byte[] mb = groupMapToBytes(m);
		List<byte[]> rlist = new ArrayList<byte[]>();
		rlist.add("demouser".getBytes());
		rlist.add(mb);
		
		byte[] all = recordListToBytes(rlist);
		
		List<byte[]> rlist2 = bytesToRecordList(all);
		byte[] r1 = rlist2.get(0);
		System.out.println("record0:"+new String(r1));
		
		Map<byte[],byte[]> m1 = bytesToGroupMap(rlist2.get(1));
		for(Map.Entry<byte[],byte[]> elm:m1.entrySet()){
			String key = new String(elm.getKey());
			String val = new String(elm.getValue());
			System.out.println("key:"+key + "/ val:"+val );
			System.out.println("key:"+key.length() + "/ val:"+val.length() );
		}
	}
}
