/*
 * Licensed to the G.Obal under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  G.Obal licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 */
package com.obal.core.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obal.core.EntryFilter;
import com.obal.core.EntryKey;
import com.obal.core.ITraceable;
import com.obal.core.accessor.EntityAccessor;
import com.obal.exception.AccessorException;
import com.obal.exception.MetaException;
import com.obal.exception.WrapperException;
import com.obal.meta.BaseEntity;
import com.obal.meta.EntityAttr;

/**
 * Base class of entry accessor, it holds HConnection object 
 * 
 * @author despird
 * @version 0.1 2014-5-2
 * 
 **/
public abstract class HEntityAccessor<GB extends EntryKey> extends EntityAccessor<GB> implements HConnAware {
	
	Logger LOGGER = LoggerFactory.getLogger(HEntityAccessor.class);
	
	public HEntityAccessor(BaseEntity schema) {
		super(schema);
	}

	private HConnection conn;
	
	@Override
	public boolean isFilterSupported(EntryFilter<?> scanfilter,boolean throwExcep) throws AccessorException{
		
		if(!Filter.class.isInstance(scanfilter.getFilter())){
			
			if(throwExcep){
				throw new AccessorException("Filter:{} is expected.",Filter.class.getName());
			}
			return false;
		}
		
		return true;
	}


	@Override
	public EntryKey newKey() throws AccessorException{
		
		EntryKey key = null;
		try {
			if(null == super.getEntitySchema())
				throw new AccessorException("The entity schema not set yet");
			
			key = super.getEntitySchema().newKey();
		} catch (MetaException e) {
			
			throw new AccessorException("Error when generating entry key",e);
		}
		
		return key;
	}
	/**
	 * get entry wrapper
	 * @return wrapper object 
	 **/
	public abstract HEntryWrapper<GB> getEntryWrapper();
	
	@Override
	public List<GB> doScanEntry(EntryFilter<?> scanfilter) throws AccessorException{
		
		List<GB> result = new LinkedList<GB>();
		HEntryWrapper<GB> wrapper = this.getEntryWrapper();
		HTableInterface table = null;
		Scan scan=new Scan();
		try {
			
			if(scanfilter != null && scanfilter != null){
				
				isFilterSupported(scanfilter,true);
				
				Filter hfilter = (Filter) scanfilter.getFilter();
				scan.setFilter(hfilter);
			}
			
			table = conn.getTable(super.getEntitySchema().getSchemaBytes(null));
			
			ResultScanner rs = table.getScanner(scan);
			
			for (Result r : rs) {  
			     GB entry = wrapper.wrap(super.getEntitySchema().getEntityName(),r);
			     // Extract the traceable information
			     if(entry instanceof ITraceable){
			    	 wrapper.wrapTraceable((ITraceable)entry, r);
			     }
			     result.add(entry);
			}
		} catch (IOException e) {
			
			throw new AccessorException("Scan exception .",e);
		} catch (WrapperException e) {
			throw new AccessorException("Scan exception .",e);
		}finally{
			
			if(table != null)
				try {
					table.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
		}
		
		return result;
	}

	@Override
	public void setConnection(HConnection connection) {
		this.conn = connection;
	}

	@Override
	public HConnection getConnection() {
		
		return conn;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <K> K doGetEntryAttr(String entryKey ,String attrName ) throws AccessorException{
		
		HTableInterface table = null;
		Object rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		EntityAttr attr = entitySchema.getEntityMeta().getAttr(attrName);
        try {
        	byte[] column = attr.getColumn().getBytes();
        	byte[] qualifier = attr.getQualifier().getBytes();
        	table = getConnection().getTable(entitySchema.getSchema(entryKey));
        	Get get = new Get(entryKey.getBytes());
        	QualifierFilter qfilter = new QualifierFilter(CompareOp.GREATER,new BinaryPrefixComparator(qualifier));
        	get.setFilter(qfilter);
        	Result entry = null;
        	byte[] cell = null;
        	HEntryWrapper<GB> wrapper = (HEntryWrapper<GB>)getEntryWrapper();
        	switch(attr.mode){
        	case PRIMITIVE:
				get.addColumn(column, qualifier);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = wrapper.getPrimitiveValue(attr, cell);		
        		break;
        	case MAP:
				get.addFamily(column);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = wrapper.getMapValue(attr, cell);
				break;
        	case LIST:
				get.addFamily(column);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = wrapper.getListValue(attr, cell);
				break;
        	case SET:
        		get.addFamily(column);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = wrapper.getSetValue(attr, cell);
				break;
			default:
				break;
        	}
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{} attr:{}",e,entryKey,attr.getAttrName());
        } catch (WrapperException e) {
        	 throw new AccessorException("Error get entry row,key:{} attr:{}",e,entryKey,attr.getAttrName());
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }
		return (K)rtv;
	}
	
	@Override
	public GB doGetEntry(String entryKey) throws AccessorException {
		HTableInterface table = null;
		GB rtv = null;
		BaseEntity entrySchema = (BaseEntity)getEntitySchema();
        try {
        	
           table = getConnection().getTable(entrySchema.getSchema(entryKey));
           Get get = new Get(entryKey.getBytes());
           
           Result r = table.get(get);
           HEntryWrapper<GB> wrapper = (HEntryWrapper<GB>)getEntryWrapper();

           rtv = wrapper.wrap(super.getEntitySchema().getEntityName(),r);
           // Extract the traceable information
		   if(rtv instanceof ITraceable){
			   wrapper.wrapTraceable((ITraceable)rtv, r);
		   }
        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (WrapperException e) {
        	 throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }
		return rtv;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EntryKey doPutEntryAttr(String entryKey, String attrName,  Object value) throws AccessorException{
		
		HTableInterface table = null;
		EntryKey rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		EntityAttr attr = entitySchema.getEntityMeta().getAttr(attrName);
        try {  
            table = getConnection().getTable(entitySchema.getSchema(entryKey));
            // support check.
            HEntryWrapper<GB> wrapper = (HEntryWrapper<GB>)this.getEntryWrapper();

            Put put =  new Put(entryKey.getBytes());
            
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("--==>>attr:{} - value:{}",attr.getAttrName(),value);
            }
            switch(attr.mode){
            
	            case PRIMITIVE:
	            	wrapper.putPrimitiveValue(put, attr, value);
	            	break;
	            case MAP:
	            	if(!(value instanceof Map<?,?>))
	        			throw new AccessorException("the attr:{} value is not Map object",attrName);        		
	        		wrapper.putMapValue(put, attr, (Map<String,Object>)value);	
	        		break;
	            case LIST:
	            	if(!(value instanceof List<?>))
	        			throw new AccessorException("the attr:{} value is not List object",attrName);        		
	        		wrapper.putListValue(put, attr, (List<Object>)value);	
	        		break;
	            case SET:
	            	if(!(value instanceof List<?>))
	        			throw new AccessorException("the attr:{} value is not List object",attrName);        		
	        		wrapper.putSetValue(put, attr, (Set<Object>)value);	
	        		break;
	            default:
	            	break;      	
            }
        	
        	table.put(put);
        	table.flushCommits();
        	rtv = new EntryKey(entitySchema.getEntityName(),entryKey);
        	
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryKey,attrName,value.toString());
        } catch (WrapperException e) {
        	throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryKey,attrName,value.toString());
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }
		return rtv;
	}
	
	@Override
	public EntryKey doPutEntry(GB entryInfo) throws AccessorException {
		HTableInterface table = null;
		EntryKey rtv = null;
		BaseEntity entrySchema = (BaseEntity)getEntitySchema();
        try {  
            table = getConnection().getTable(entrySchema.getSchema(entryInfo));
            HEntryWrapper<GB> wrapper = this.getEntryWrapper();
 
            Put put = (Put)wrapper.parse(entrySchema.getEntityMeta().getAllAttrs(),entryInfo);
        	// for traceable set trace information
            if(entryInfo instanceof ITraceable){
        		
        		wrapper.parseTraceable(put, (ITraceable)entryInfo);
        	}
            table.put(put);
        	table.flushCommits();
        	rtv = entryInfo;
        	
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryInfo.getKey());
        } catch (WrapperException e) {
        	throw new AccessorException("Error put entry row,key:{}",e,entryInfo.getKey());
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }
		return rtv;
	}
	
	@Override
	public void doDelEntry(String... rowkey) throws AccessorException {
		HTableInterface table = null;
		BaseEntity entrySchema = (BaseEntity)getEntitySchema();
		try {
			
			
			List<Delete> list = new ArrayList<Delete>();
			for(String key:rowkey){
				table = getConnection().getTable(entrySchema.getSchema(key));
				if(StringUtils.isBlank(key)) continue;
				
				Delete d1 = new Delete(key.getBytes());  
				list.add(d1); 
			}
	        table.delete(list);
	        table.flushCommits();
		} catch (IOException e) {
			throw new AccessorException("Error delete entry row, key:{}",e,rowkey);
		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }        
	}
		
	@Override
	public void release() {
		try {
			// embed means share connection, close it directly affect other accessors using this conn.
			if (conn != null && !isEmbed()){
				this.conn.close();
				this.conn = null;
			}

			super.release();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
