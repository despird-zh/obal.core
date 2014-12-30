package com.obal.aop;

public class ByteValueUtils {
		
	/** 
	 * Convert byte value to bit String 
	 * 
	 * @param b the byte value
	 * @return String the string of binary bit array.
	 */  
	public static String byteToBit(byte b) {
		
		StringBuffer sbuf = new StringBuffer();
		
		sbuf.append((byte)((b >> 7) & 0x1))
		.append((byte)((b >> 6) & 0x1))
		.append((byte)((b >> 5) & 0x1))
		.append((byte)((b >> 4) & 0x1))
		.append((byte)((b >> 3) & 0x1))
		.append((byte)((b >> 2) & 0x1))
		.append((byte)((b >> 1) & 0x1))
		.append((byte)((b >> 0) & 0x1));
		
	    return sbuf.toString(); 
	}  
	  
	/** 
	 * Convert bit string into Byte
	 * 
	 * @param byteStr the bitString
	 * @return byte the byte object
	 */  
	public static byte bitToByte(String byteStr) {  
	    int re, len;
	    
	    if (null == byteStr) {  
	        return 0;  
	    }
	    
	    len = byteStr.length();  
	    if (len != 4 && len != 8) {  
	        return 0;  
	    }  
	    if (len == 8) {// 8 bit处理  
	        if (byteStr.charAt(0) == '0') {// 正数  
	            re = Integer.parseInt(byteStr, 2);  
	        } else {// 负数  
	            re = Integer.parseInt(byteStr, 2) - 256;  
	        }  
	    } else {//4 bit处理  
	        re = Integer.parseInt(byteStr, 2);  
	    }  
	    
	    return (byte) re;  
	}  
}
