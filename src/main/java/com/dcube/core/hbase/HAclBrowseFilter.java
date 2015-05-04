package com.dcube.core.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.Bytes;

import com.dcube.common.SimpleConverter;
import com.dcube.core.security.AclConstants;
import com.dcube.core.security.AclConstants.AcePrivilege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *  column family = acl<br>
 *  -= basic account and group information <br>
 *  owner qualifier: owner / value :[owner account]<br>
 *  
 *  -= basic privilege information <br>
 *  owner privilege qualifier : u: /value :[privilege]<br>
 *  other privilege qualifier : o: /value :[privilege]<br>
 *  
 *  -= extend privilege information <br>
 *  user privilege qualifier : u:[xx1]  / value :[privilege]<br>
 *  group privilege qualifier : g:[xx2]  / value :[privilege]<br>
 *  
 *  
 **/
public class HAclBrowseFilter extends FilterBase {
	
	/** column family */
	protected byte[] aclColumnFamily = AclConstants.CF_ACL.getBytes();
	
	protected byte[] ownerPrivQL = AclConstants.AceType.Owner.abbr.getBytes();
	//protected byte[] ownerPrefix = AclConstants.QL_OWNER_PREFIX.getBytes();
	
	protected byte[] userPrefix  = AclConstants.QL_USER_PREFIX.getBytes();
	protected byte[] groupPrefix = AclConstants.QL_GROUP_PREFIX.getBytes();
	
	protected byte[] otherPrivQL = AclConstants.AceType.Other.abbr.getBytes();
	protected byte[] otherPrefix = AclConstants.QL_OTHER_PREFIX.getBytes();
	
	/** owner qualifier */
	private byte[] ownerQualifier = AclConstants.QL_OWNRER.getBytes();
	/** user */
	private byte[] account = new byte[0];
	/** group */
	private List<String> groups = new ArrayList<String>();
	/** is owner account */
	protected Boolean isowner = null;
	/** is named account */
	protected Boolean isnamed = false;
	/** owner could browse */
	protected Boolean ownerbrowse = null;
	/** owner could browse */
	protected Boolean namedbrowse = null;
	/** other could browse */
	protected Boolean otherbrowse = null;
	/** group could browse */
	protected Boolean groupbrowse = false;
	
	public HAclBrowseFilter(String account, String[] groups) {

		this.account = account == null? this.account:account.getBytes();
		if(groups == null) return;
		for (int i = 0; i < groups.length; i++) {
			this.groups.add(groups[i]);
		}
	}

	public HAclBrowseFilter(byte[] account, List<byte[]> groups) {
		this.account = account == null? this.account: account;
		if(groups == null) return;
		for(byte[] grp:groups){
			this.groups.add(new String(grp));
		}
	}

	@Override
	public boolean filterRow() {
		
		// if account is owner , check owner and groups
		if(isowner == true){
			System.out.println("---=== isowner=true ===---");
			// return false select current row, true ignore it.
			return !(ownerbrowse || groupbrowse);
		}else if(isnamed == true){
			System.out.println("---=== isnamed=true ===---");
			// return false select current row, true ignore it.
			return !(namedbrowse || groupbrowse);
		}else{
			System.out.println("---=== other=true ===---");
			// return false select current row, true ignore it.
			return !(otherbrowse || groupbrowse);			
		}
	}

	@Override
	public boolean hasFilterRow() {
		return true;
	}

	@Override
	public void reset() {
		
		isowner = null;
		isnamed = false;
		ownerbrowse = null;		
		otherbrowse = null;
		namedbrowse = null;
		groupbrowse = false;
	}

	@Override
	public ReturnCode filterKeyValue(Cell kv) {
		// found acl data do acl check
		if (Bytes.equals(aclColumnFamily, 0, aclColumnFamily.length,
						kv.getFamilyArray(), kv.getFamilyOffset(),
						kv.getFamilyLength())) {
			// only compare column family [acl]
			return filterReadable(kv);
		} else {
			return ReturnCode.INCLUDE;
		}
	}

	private ReturnCode filterReadable(Cell cell) {

		byte[] value = CellUtil.cloneValue(cell);
		
		// try to set isowner flag, only one cell
		if(isowner == null && CellUtil.matchingColumn(cell, this.aclColumnFamily, this.ownerQualifier) ){
			isowner = Bytes.equals(value, account);
			return ReturnCode.INCLUDE;
		}
		// check owner's privilege , only one cell
		if(ownerbrowse == null && CellUtil.matchingColumn(cell, this.aclColumnFamily, this.ownerPrivQL) ){
			// set if could be browse
			AcePrivilege v = AclConstants.convertPrivilege(new String(value));
			ownerbrowse = v.priority >= AcePrivilege.BROWSE.priority;
			
			return ReturnCode.INCLUDE;
		}		
		// check other's privilege , only one cell
		if(otherbrowse == null && CellUtil.matchingColumn(cell, this.aclColumnFamily, this.otherPrivQL) ){
			// set if could be browse
			AcePrivilege v = AclConstants.convertPrivilege(new String(value));
			otherbrowse = v.priority >= AcePrivilege.BROWSE.priority;
			return ReturnCode.INCLUDE;
		}
		
		byte[] qualifier = CellUtil.cloneQualifier(cell);		
		// check group's privilege, intercept all g:xxx qualifier/value pairs
		if(!groupbrowse && Bytes.startsWith(qualifier, groupPrefix) && groups.size()>0){
			byte[] groupname = Bytes.copy(qualifier, groupPrefix.length, qualifier.length - groupPrefix.length);
			
			// not query user's group, then return include.
			if(!groups.contains(new String(groupname))) return ReturnCode.INCLUDE;
			
			// set if could be browse
			AcePrivilege v = AclConstants.convertPrivilege(new String(value));		
			groupbrowse = v.priority >= AcePrivilege.BROWSE.priority;
			return ReturnCode.INCLUDE;
		}
		
		// check account's privilege, intercept all u:xxx qualifier/value pairs until sure isnamed= ture
		if(!isnamed && Bytes.startsWith(qualifier, userPrefix)){
			byte[] namedaccount = Bytes.copy(qualifier, userPrefix.length, qualifier.length - userPrefix.length);
			// not query user's group, then return include.
			if(!Bytes.equals(account, namedaccount)) return ReturnCode.INCLUDE;
			
			// set if could be browse
			AcePrivilege v = AclConstants.convertPrivilege(new String(value));			
			namedbrowse = v.priority >= AcePrivilege.BROWSE.priority;
			return ReturnCode.INCLUDE;
		}
		
		// owner mode only, not browse support, next row
		if(isowner == Boolean.TRUE && ( groups == null || groups.size()==0) && ownerbrowse == Boolean.FALSE){
			return ReturnCode.NEXT_ROW;	
		}
		
		// other mode only, not browse support, next row
		if(isowner == Boolean.FALSE && ( groups == null || groups.size()==0) && otherbrowse == Boolean.FALSE){
			return ReturnCode.NEXT_ROW;	
		}
		return ReturnCode.INCLUDE;
	}

	/**
	 * @return The filter serialized using pb
	 */
	public byte[] toByteArray() {

		List<byte[]> records = new ArrayList<byte[]>();
		records.add(account);
		Map<byte[], byte[]> gmap = new HashMap<byte[], byte[]>();
		for (int i = 0; i < groups.size(); i++) {
			gmap.put(Bytes.toBytes(i), groups.get(i).getBytes());
		}
		records.add(SimpleConverter.groupMapToBytes(gmap));
		return SimpleConverter.recordListToBytes(records);
	}

	/**
	 * @param pbBytes
	 *            A pb serialized {@link ColumnPrefixFilter} instance
	 * @return An instance of {@link ColumnPrefixFilter} made from
	 *         <code>bytes</code>
	 * @throws org.apache.hadoop.hbase.exceptions.DeserializationException
	 * @see #toByteArray
	 */
	public static HAclBrowseFilter parseFrom(final byte[] pbBytes)
			throws DeserializationException {

		List<byte[]> records = SimpleConverter.bytesToRecordList(pbBytes);
		Map<byte[], byte[]> gmap = SimpleConverter.bytesToGroupMap(records.get(1));
		List<byte[]> groups = new ArrayList<byte[]>(gmap.values());

		HAclBrowseFilter aclFilter = new HAclBrowseFilter(records.get(0), groups);
		return aclFilter;
	}

	@Override
	public String toString() {

		return this.getClass().getSimpleName() + ":" + Bytes.toString(account)
				+ ":" + groups + ":";
	}

}
