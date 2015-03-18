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
package com.doccube.core.hbase;

import java.io.IOException;
import java.util.ArrayList;
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

import com.doccube.core.EntryFilter;
import com.doccube.core.EntryKey;
import com.doccube.core.accessor.AccessorContext;
import com.doccube.core.accessor.EntityAccessor;
import com.doccube.core.accessor.EntryCollection;
import com.doccube.core.accessor.EntryInfo;
import com.doccube.exception.AccessorException;
import com.doccube.exception.MetaException;
import com.doccube.exception.WrapperException;
import com.doccube.meta.BaseEntity;
import com.doccube.meta.EntityAttr;

/**
 * Base class of EntitAccessor, it holds HConnection object to access HBase 
 * <p>
 * 	
 * </p>
 * @author despird
 * @version 0.1 2014-5-2
 * 
 * @see EntityAccessor
 **/
public abstract class HEntityAccessor<GB extends EntryInfo> extends EntityAccessor<GB> implements HConnAware {

	Logger LOGGER = LoggerFactory.getLogger(HEntityAccessor.class);
	private HConnection connection;
	
	public HEntityAccessor(String accessorName,AccessorContext context) {
		super(accessorName,context);
	}
	
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
			
			key = super.getEntitySchema().newKey(getContext().getPrincipal());
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
	public EntryCollection<GB> doScanEntry(EntryFilter<?> scanfilter) throws AccessorException{
		
		EntryCollection<GB> entryColl = new EntryCollection<GB>();
		HEntryWrapper<GB> wrapper = this.getEntryWrapper();
		HTableInterface table = null;
		Scan scan=new Scan();
		try {
			
			if(scanfilter != null && scanfilter != null){
				
				isFilterSupported(scanfilter,true);
				
				Filter hfilter = (Filter) scanfilter.getFilter();
				scan.setFilter(hfilter);
			}
			HConnection conn = getConnection();
			AccessorContext context = super.getContext();
			table = conn.getTable(context.getEntitySchema().getSchemaBytes(getContext().getPrincipal(),null));
			BaseEntity schema = context.getEntitySchema();
			List<EntityAttr> attrs = schema.getEntityMeta().getAllAttrs();
			ResultScanner rs = table.getScanner(scan);
			
			for (Result r : rs) {  
			     GB entry = wrapper.wrap(attrs,r);
			     
			     entryColl.addEntry(entry);
			}
		} catch (IOException e) {
			
			throw new AccessorException("Scan exception .",e);
		} catch (WrapperException e) {
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
		
		return entryColl;
	}

	@Override
	public void setConnection(HConnection connection) {
		
		this.connection = connection;
		
	}

	@Override
	public HConnection getConnection() {
		
		return connection;
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
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
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
		} catch (MetaException e) {
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
        	
        	table = getConnection().getTable(entrySchema.getSchema(getContext().getPrincipal(),entryKey));

           Get get = new Get(entryKey.getBytes());
           
           Result r = table.get(get);
           HEntryWrapper<GB> wrapper = (HEntryWrapper<GB>)getEntryWrapper();
           AccessorContext context = super.getContext();
           BaseEntity schema = context.getEntitySchema();
           rtv = wrapper.wrap(schema.getEntityMeta().getAllAttrs(),r);

        } catch (IOException e) {  
        	
            throw new AccessorException("Error get entry row,key:{}",e,entryKey);
        } catch (WrapperException e) {
        	 throw new AccessorException("Error get entry row,key:{}",e,entryKey);
		} catch (MetaException e) {
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
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
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
		} catch (MetaException e) {
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
        	EntryKey key = entryInfo.getEntryKey();
            table = getConnection().getTable(entrySchema.getSchema(getContext().getPrincipal(),key.getKey()));
            HEntryWrapper<GB> wrapper = this.getEntryWrapper();
 
            Put put = (Put)wrapper.parse(entrySchema.getEntityMeta().getAllAttrs(),entryInfo);

            table.put(put);
        	table.flushCommits();
        	rtv = entryInfo.getEntryKey();
        	
        } catch (IOException e) {  
        	 throw new AccessorException("Error put entry row,key:{}",e,entryInfo.getEntryKey().toString());
        } catch (WrapperException e) {
        	throw new AccessorException("Error put entry row,key:{}",e,entryInfo.getEntryKey().toString());
		} catch (MetaException e) {
			throw new AccessorException("Error put entry row,key:{},attr:{},value{}",e,entryInfo.getEntryKey().toString());
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
				table = getConnection().getTable(entrySchema.getSchema(getContext().getPrincipal(),key));
				if(StringUtils.isBlank(key)) continue;
				
				Delete d1 = new Delete(key.getBytes());  
				list.add(d1); 
			}
	        table.delete(list);
	        table.flushCommits();
		} catch (IOException e) {
			throw new AccessorException("Error delete entry row, key:{}",e,rowkey);
		} catch (MetaException e) {
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
