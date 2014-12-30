package com.obal.core.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jackson.map.ObjectMapper;

import com.obal.core.security.EntryAcl;
import com.obal.core.security.Principal;

import java.nio.ByteBuffer;

public class HAclFilter extends FilterBase {

	public static String DEFAULT_ACL_QUALIFIER = "acl";
	public static String DEFAULT_ACL_COLUMN = "c0";
	
	private static byte[] FLD_SEPARATOR = "-=&=-".getBytes();
	
	protected byte[] aclQualifier = DEFAULT_ACL_QUALIFIER.getBytes();
	
	protected byte[] aclColumn = DEFAULT_ACL_COLUMN.getBytes();

	protected Principal principal = null;
	
	private static ObjectMapper mapper = null;
	
	public HAclFilter(final byte[] aclColumn, final byte[] aclQualifier) {
		this.aclQualifier = aclQualifier;
		this.aclColumn = aclColumn;
	}
	
	public HAclFilter(final byte[] aclColumn, final byte[] aclQualifier, Principal principal) {
		this.aclQualifier = aclQualifier;
		this.aclColumn = aclColumn;
		this.principal = principal;
	}

	public void setPrincipal(Principal principal) {

		this.principal = principal;
	}

	public void setPrincipal(String jsonPrincipal) {

		if(null == mapper)
			mapper = new ObjectMapper();
		
		try {
			principal = mapper.readValue(jsonPrincipal, Principal.class);
			this.setPrincipal(principal);
		} catch (Exception e) {

			e.printStackTrace();
		} 
	}

	@Override
	public ReturnCode filterKeyValue(Cell kv) {
		// found acl data do acl check
		if (this.aclColumn != null
				&& this.aclQualifier != null
				&& Bytes.equals(aclQualifier, 0, aclQualifier.length,
						kv.getQualifierArray(), kv.getQualifierOffset(),
						kv.getQualifierLength())
				&& Bytes.equals(this.aclColumn, 0, aclColumn.length,
						kv.getFamilyArray(), kv.getFamilyOffset(),
						kv.getFamilyLength())) {
			return filterACL(kv.getValueArray(), kv.getValueOffset(),
					kv.getValueLength());
		} else {

			return ReturnCode.INCLUDE;
		}
	}

	private ReturnCode filterACL(byte[] buffer, int valueOffset, int valueLength) {
		String acl = Bytes.toString(buffer, valueOffset, valueLength);

		EntryAcl ea = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			ea = mapper.readValue(acl, EntryAcl.class);
			
		} catch (Exception e) {

			e.printStackTrace();
		} 

		if(ea != null && !ea.checkReadable(principal)){
			
			return ReturnCode.NEXT_ROW;
			
		}else{
		
			return ReturnCode.INCLUDE;
			
		}

	}

	private static int COLFAMILY = 101;
	private static int QUALIFIER = 102;
	private static int PRINCIPAL = 103;
	
	private byte[] getByteArray(int mode){
		
		if(mode == COLFAMILY){
			return this.aclColumn;
		}else if(mode == QUALIFIER){
			return this.aclQualifier;
		}else if(mode == PRINCIPAL){
			
			mapper = (null == mapper) ? new ObjectMapper():null;
			try {
				return mapper.writeValueAsBytes(principal);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * @return The filter serialized using pb
	 */
	public byte[] toByteArray() {
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(2048);		
		byte[] bytes = null;
		
		bytes = getByteArray(COLFAMILY);		
		buffer.put(bytes).put(FLD_SEPARATOR);
		
		bytes = getByteArray(QUALIFIER);
		buffer.put(bytes).put(FLD_SEPARATOR);
		
		bytes = getByteArray(PRINCIPAL);
		buffer.put(bytes);
		
		buffer.flip();		
		bytes = new byte[buffer.remaining()]; 
		buffer.get(bytes);
		
		return bytes;
	}

	/**
	 * @param pbBytes
	 *            A pb serialized {@link ColumnPrefixFilter} instance
	 * @return An instance of {@link ColumnPrefixFilter} made from
	 *         <code>bytes</code>
	 * @throws org.apache.hadoop.hbase.exceptions.DeserializationException
	 * @see #toByteArray
	 */
	public static HAclFilter parseFrom(final byte[] pbBytes)
			throws DeserializationException {
		
		int length = Bytes.indexOf(pbBytes, FLD_SEPARATOR);
		int offset = 0;
		byte[] colfamily = Bytes.copy(pbBytes, offset, length);
		
		length = length + FLD_SEPARATOR.length;		
		Bytes.zero(pbBytes, offset, length );
		
		offset = offset + length;
		length = Bytes.indexOf(pbBytes, FLD_SEPARATOR) - offset;
		byte[] qualifier = Bytes.copy(pbBytes, offset, length );
		
		length = length + FLD_SEPARATOR.length;		
		Bytes.zero(pbBytes, offset, length );
		
		offset = offset + length;		
		length = pbBytes.length - offset;
		byte[] principal = Bytes.copy(pbBytes, offset,length);
		
		HAclFilter aclFilter = new HAclFilter(colfamily, qualifier);		
		aclFilter.setPrincipal(Bytes.toString(principal));
		
		return aclFilter;
	}

	@Override
	public String toString() {
		
		return this.getClass().getSimpleName() + ":" 
				+ Bytes.toString(this.aclColumn) + ":" 
				+ Bytes.toString(this.aclQualifier) + ":" 
				+ "";
	}
}
