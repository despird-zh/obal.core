package com.dcube.core.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
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
			table = conn.getTable(schema.getSchemaBytes(getContext().getPrincipal(),null));
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
			table = conn.getTable(schema.getSchemaBytes(getContext().getPrincipal(),null));
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
	public void doDelEntryKey(String attribute, Object value, String... key)
			throws AccessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doPutEntryKey(String attribute, Object value, String... keys)
			throws AccessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConnection(HConnection connection) {
		
		this.connection = connection;
		
	}

	@Override
	public HConnection getConnection() {
		
		return connection;
	}
	
}
