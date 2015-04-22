package com.dcube.core.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcube.core.EntryKey;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.IndexAccessor;
import com.dcube.exception.AccessorException;
import com.dcube.exception.MetaException;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;

/**
 * HIndexAccessor provides method to put/get EntryKey for indexable attributes.
 * 
 * @author despird
 * @version 0.1 2014-3-2 Initial
 * 
 **/
public class HIndexAccessor extends IndexAccessor implements HConnAware {

	Logger LOGGER = LoggerFactory.getLogger(HIndexAccessor.class);
	private HConnection connection;
	
	public HIndexAccessor() {
		super(null);
	}
	
	public HIndexAccessor(AccessorContext context) {
		super(context);
	}

	@Override
	public EntryKey doGetEntryKey(String attribute, Object value)
			throws AccessorException {
		
		AccessorContext context = super.getContext();		

		HTableInterface table = null;
		Result result = null;
		BaseEntity schema = context.getEntitySchema();
		try {
						
			HConnection conn = getConnection();
			String indexschema = schema.getIndexSchema(getContext().getPrincipal(),null);
			table = conn.getTable(indexschema.getBytes());
			// get attribute meta data
			EntityAttr attr = schema.getEntityMeta().getAttr(attribute);
			byte[] indexKey = HWrapperUtils.toIndexKey(attr, value);
			Get get = new Get(indexKey);			
			result = table.get(get);
			CellScanner scanner = result.cellScanner();
			while (scanner.advance()) {
				Cell cell = scanner.current();
				String key = Bytes.toString(cell.getQualifierArray());
				return new EntryKey(attr.getEntityName(),key);
			}
			return null;
		} catch (IOException e) {
			
			throw new AccessorException("Scan exception .",e);
		} catch (MetaException e) {
			throw new AccessorException("Scan exception .",e);
		}finally{
			
			if(table != null)
				try {
					table.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
		}

	}

	@Override
	public List<EntryKey> doGetEntryKeyList(String attribute, Object value)
			throws AccessorException {

		AccessorContext context = super.getContext();		
		List<EntryKey> rtv = new ArrayList<EntryKey>();
		HTableInterface table = null;
		Result result = null;
		BaseEntity schema = context.getEntitySchema();
		try {
						
			HConnection conn = getConnection();			
			String indexschema = schema.getIndexSchema(getContext().getPrincipal(),null);
			table = conn.getTable(indexschema.getBytes());
			// get attribute meta data
			EntityAttr attr = schema.getEntityMeta().getAttr(attribute);
			byte[] indexKey = HWrapperUtils.toIndexKey(attr, value);
			Get get = new Get(indexKey);			
			result = table.get(get);
			
			CellScanner scanner = result.cellScanner();
			while (scanner.advance()) {
				Cell cell = scanner.current();
				// qualifier is the expected row key
				String key = Bytes.toString(cell.getQualifierArray());
				rtv.add(new EntryKey(attr.getEntityName(),key));				
			}
			return rtv;
		} catch (IOException e) {
			
			throw new AccessorException("Scan exception .",e);
		} catch (MetaException e) {
			throw new AccessorException("Scan exception .",e);
		}finally{
			
			if(table != null)
				try {
					table.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
		}
	}

	@Override
	public void doDelEntryKey(String attribute, Object value, String... keys)
			throws AccessorException {
		AccessorContext context = super.getContext();	
		HTableInterface table = null;
		String akey = null;
		BaseEntity schema = context.getEntitySchema();
		String indexschema = null;
		try {
			// Get Attribute infomation
			EntityAttr attr = schema.getEntityMeta().getAttr(attribute);
			List<Delete> list = new ArrayList<Delete>();
			// Get index schema name
			indexschema = schema.getIndexSchema(getContext().getPrincipal(),null);
			table = getConnection().getTable(indexschema.getBytes());
			// Get index key
			byte[] indexKey = HWrapperUtils.toIndexKey(attr, value);
			for(String key:keys){
				akey = key;
				if(StringUtils.isBlank(key)) continue;				
				Delete del = new Delete(indexKey);
				if(attr != null){
					// delete qualifier all version.
					del.deleteColumns(EntityConstants.ATTR_DFT_COLUMN.getBytes(), key.getBytes());
				
				}
				list.add(del); 
			}			
	        table.delete(list);
	        // Check if index row is empty
	        Get get = new Get(indexKey);			
			Result result = table.get(get);
			if(result.isEmpty()){
				Delete del = new Delete(indexKey);
				table.delete(del);
			}
	        table.flushCommits();
	        
		} catch (Exception e) {
			
			throw new AccessorException("Error delete entry index, key:{}-{}-{}",e,indexschema,attribute,akey);

		}finally{
			if(table != null)
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }       
	}

	@Override
	public void doPutEntryKey(String attribute, Object value, String... keys)
			throws AccessorException {
		AccessorContext context = super.getContext();	
		HTableInterface table = null;
		String akey = null;
		BaseEntity schema = context.getEntitySchema();
		String indexschema = null;
		try {
			// Get Attribute infomation
			EntityAttr attr = schema.getEntityMeta().getAttr(attribute);
			List<Put> list = new ArrayList<Put>();
			// Get index schema name
			indexschema = schema.getIndexSchema(getContext().getPrincipal(),null);
			table = getConnection().getTable(indexschema.getBytes());
			// Get index key
			byte[] indexKey = HWrapperUtils.toIndexKey(attr, value);
			for(String key:keys){
				akey = key;
				if(StringUtils.isBlank(key)) continue;				
				Put put = new Put(indexKey);
				if(attr != null){
					// put value index row.
					put.add(EntityConstants.ATTR_DFT_COLUMN.getBytes(), 
							key.getBytes(),
							EntityConstants.BLANK_VALUE.getBytes());
				
				}
				list.add(put); 
			}			
	        table.put(list);
	        table.flushCommits();
	        
		} catch (Exception e) {
			
			throw new AccessorException("Error put entry index, key:{}-{}-{}",e,indexschema,attribute,akey);

		}finally{
			if(table != null)
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }  
	}

	@Override
	public void doChangeEntryKey(String attribute, Object oldValue,
			Object newValue, String key) throws AccessorException {
		AccessorContext context = super.getContext();	
		HTableInterface table = null;
		String akey = null;
		BaseEntity schema = context.getEntitySchema();
		String indexschema = null;
		try {
			// Get Attribute information
			EntityAttr attr = schema.getEntityMeta().getAttr(attribute);
			// Get index schema name
			indexschema = schema.getIndexSchema(getContext().getPrincipal(),null);
			table = getConnection().getTable(indexschema.getBytes());
			// Get index key
			byte[] indexKey = HWrapperUtils.toIndexKey(attr, oldValue);
			
			Delete del = new Delete(indexKey);
			if(attr != null){
				// delete qualifier all version.
				del.deleteColumns(EntityConstants.ATTR_DFT_COLUMN.getBytes(), key.getBytes());
				
			}
			
	        table.delete(del);
	        // Check if index row is empty
	        Get get = new Get(indexKey);			
			Result result = table.get(get);
			if(result.isEmpty()){
				Delete delrow = new Delete(indexKey);
				table.delete(delrow);
			}
			// put key to changed new value index
			indexKey = HWrapperUtils.toIndexKey(attr, newValue);
			Put put = new Put(indexKey);
			put.add(EntityConstants.ATTR_DFT_COLUMN.getBytes(), 
					key.getBytes(),
					EntityConstants.BLANK_VALUE.getBytes());
			
	        table.flushCommits();
	        
		} catch (Exception e) {
			
			throw new AccessorException("Error delete entry index, key:{}-{}-{}",e,indexschema,attribute,akey);

		}finally{
			if(table != null)
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }       
	}
	
	@Override
	public void setConnection(HConnection connection) {
		
		this.connection = connection;
		
	}

	@Override
	public HConnection getConnection() {
		
		return connection;
	}
	
	@Override	
	public void close(){
		try {
			HConnection conn = getConnection();
			// embed means share connection, close it directly affect other accessors using this conn.
			if (conn != null && !isEmbed()){
				conn.close();				
			}
			
			super.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
