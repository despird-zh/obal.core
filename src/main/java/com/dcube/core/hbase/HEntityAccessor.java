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
package com.dcube.core.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.dcube.audit.AuditInfo;
import com.dcube.audit.Predicate;
import com.dcube.core.EntryFilter;
import com.dcube.core.EntryKey;
import com.dcube.core.IEntityEntry;
import com.dcube.core.IEntryConverter;
import com.dcube.core.IGenericEntry.AttributeItem;
import com.dcube.core.accessor.AccessorContext;
import com.dcube.core.accessor.EntityAccessor;
import com.dcube.core.accessor.EntryCollection;
import com.dcube.exception.AccessorException;
import com.dcube.exception.MetaException;
import com.dcube.exception.WrapperException;
import com.dcube.index.IndexInfo;
import com.dcube.index.IndexManager;
import com.dcube.index.IndexInfo.IndexMode;
import com.dcube.meta.BaseEntity;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityManager;

/**
 * Base class of EntitAccessor, it holds HConnection object to access HBase 
 * <pre>
 * public class DemoAccessor extends HEntityAccessor<EntryInfo>{
 *  
 *  // this constructor is required
 * 	public DemoAccessor() {
 *      // initial the name of accessor
 * 		super(EntityConstants.ACCESSOR_ENTITY_ATTR);		
 * 	}
 *  // this constructor is required
 * 	public DemoAccessor(AccessorContext context) {
 *      // initial with name and context for further operation
 * 		super(EntityConstants.ACCESSOR_ENTITY_ATTR,context);		
 * 	}
 * 	
 * 	public HEntryWrapper<EntryInfo> getEntryWrapper() {
 *       .....
 * 	}
 * }
 * </pre>
 * 
 * @author despird
 * @version 0.1 2014-5-2
 * 
 * @see EntityAccessor
 **/
public abstract class HEntityAccessor<GB extends IEntityEntry> extends EntityAccessor<GB> implements HConnAware {

	Logger LOGGER = LoggerFactory.getLogger(HEntityAccessor.class);
	private HConnection connection;
	
	/**
	 * Constructor with EntityAccessor name and context.
	 * @param accessorName the name of EntityAccessor
	 **/	
	public HEntityAccessor(String accessorName) {
		super(accessorName,null);
	}
	
	/**
	 * Constructor with EntityAccessor name and context.
	 * @param accessorName the name of EntityAccessor
	 * @param context the context during operation  
	 **/
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
	public EntryCollection<GB> doScanEntry(EntryFilter<?> scanfilter) throws AccessorException{
		
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_SCAN);
		
		EntryCollection<GB> entryColl = new EntryCollection<GB>();

		HTableInterface table = null;
		Scan scan=new Scan();
		ResultScanner scanner = null;
		BaseEntity schema = context.getEntitySchema();
		try {
			
			if(scanfilter != null && scanfilter != null){
				
				isFilterSupported(scanfilter,true);
				
				Filter hfilter = (Filter) scanfilter.getFilter();
				scan.setFilter(hfilter);
			}
			HConnection conn = getConnection();
			
			table = conn.getTable(schema.getSchemaBytes(getContext().getPrincipal(),null));
			
			List<EntityAttr> attrs = schema.getEntityMeta().getAllAttrs();
			scanner = table.getScanner(scan);
			
			for (Result r : scanner) {  
				 GB entry = newEntryObject();
			     wrap(attrs,r, entry);
			     
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
					scanner.close();
					table.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_SCAN)
				.setTarget(schema.getEntityName());
			audit.addPredicate(AUDIT_OPER_SCAN, Predicate.KEY_FILTER, scanfilter == null?"":scanfilter.toString());
			context.auditEnd();
		}
		
		return entryColl;
	}

	@Override
	public EntryCollection<GB> doScanEntry(EntryFilter<?> scanfilter, String... attributes)throws AccessorException{
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_SCAN);
		
		EntryCollection<GB> entryColl = new EntryCollection<GB>();

		HTableInterface table = null;
		Scan scan=new Scan();
		ResultScanner scanner = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		try {
			
			if(scanfilter != null && scanfilter != null){
				
				isFilterSupported(scanfilter,true);
				
				Filter hfilter = (Filter) scanfilter.getFilter();
				scan.setFilter(hfilter);
			}
			HConnection conn = getConnection();
			
			table = conn.getTable(context.getEntitySchema().getSchemaBytes(getContext().getPrincipal(),null));
			List<EntityAttr> attrs = entitySchema.getEntityMeta().getAttrs(attributes);
        	for(EntityAttr attr: attrs){
        		scan.addColumn(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
        	}
			scanner = table.getScanner(scan);
			
			for (Result r : scanner) {  
				GB entry = newEntryObject();
			     wrap(attrs,r,entry);
			     
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
					scanner.close();
					table.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_SCAN)
				.setTarget(entitySchema.getEntityName());
			audit.addPredicate(AUDIT_OPER_SCAN, Predicate.KEY_FILTER, scanfilter);
			audit.addPredicate(AUDIT_OPER_SCAN, Predicate.KEY_PARAM, attributes);
			context.auditEnd();
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
		
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_GET_ATTR);
		
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
        	switch(attr.mode){
        	case PRIMITIVE:
				get.addColumn(column, qualifier);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = HWrapperUtils.getPrimitiveValue(attr, cell);		
        		break;
        	case MAP:
				get.addFamily(column);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = HWrapperUtils.getJMapValue(attr, cell);
				break;
        	case LIST:
				get.addFamily(column);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = HWrapperUtils.getJListValue(attr, cell);
				break;
        	case SET:
        		get.addFamily(column);
	        	entry = table.get(get);
	        	cell = entry.getValue(column, qualifier);
				rtv = HWrapperUtils.getJSetValue(attr, cell);
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
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_GET_ATTR)
				.setTarget(entitySchema.getEntryKey(entryKey).toString());
			audit.addPredicate(AUDIT_OPER_GET_ATTR, Predicate.KEY_PARAM, attr.getAttrName());
			context.auditEnd();
        }
		return (K)rtv;
	}
	
	@Override
	public GB doGetEntry(String entryKey) throws AccessorException {
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_GET_ENTRY);
		HTableInterface table = null;
		GB rtv = newEntryObject();
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {
        	
        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));

        	Get get = new Get(entryKey.getBytes());           
        	Result result = table.get(get);
        	
        	wrap(entitySchema.getEntityMeta().getAllAttrs(),result, rtv);

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
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_GET_ENTRY)
				.setTarget(entitySchema.getEntryKey(entryKey).toString());
			
			context.auditEnd();
        }
		return rtv;
	}
	
	@Override
	public GB doGetEntry(String entryKey, String... attributes)throws AccessorException{
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_GET_ENTRY);
		
		HTableInterface table = null;
		GB rtv = newEntryObject();
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {

        	table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
        	List<EntityAttr> attrs = entitySchema.getEntityMeta().getAttrs(attributes);
        	
        	Get get = new Get(entryKey.getBytes());
        	for(EntityAttr attr: attrs){
        		get.addColumn(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
        	}
        	Result result = table.get(get);
        	
        	wrap(attrs, result, rtv);

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
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_GET_ENTRY)
				.setTarget(entitySchema.getEntryKey(entryKey).toString());
			audit.addPredicate(AUDIT_OPER_GET_ATTR, Predicate.KEY_PARAM, attributes);
			context.auditEnd();
		}
		return rtv;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EntryKey doPutEntryAttr(String entryKey, String attrName,  Object value) throws AccessorException{
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_PUT_ATTR);
		
		HTableInterface table = null;
		EntryKey rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
		EntityAttr attr = entitySchema.getEntityMeta().getAttr(attrName);
		
		if(!validateAttrValue(attrName, value)){
			
			throw new AccessorException("The {} is required, but value is null.",attrName);
		}
		
        try {  
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),entryKey));
            // try to get the origin value before put
            Get get = new Get(entryKey.getBytes());
            get.addColumn(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
            Result entry = table.get(get);
            byte[] cell = entry.getValue(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
            Object originVal = HWrapperUtils.getPrimitiveValue(attr, cell);	
            
            Put put =  new Put(entryKey.getBytes());
            
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("Put:{} => attribute:{} - value:{}",new Object[]{entryKey,attr.getAttrName(),value});
            }
            switch(attr.mode){
            
	            case PRIMITIVE:
	            	HWrapperUtils.putPrimitiveValue(put, attr, value);
	            	break;
	            case MAP:
	            	if(!(value instanceof Map<?,?>))
	        			throw new AccessorException("the attr:{} value is not Map object",attrName);        		
	            	HWrapperUtils.putJMapValue(put, attr, (Map<String,Object>)value);	
	        		break;
	            case LIST:
	            	if(!(value instanceof List<?>))
	        			throw new AccessorException("the attr:{} value is not List object",attrName);        		
	            	HWrapperUtils.putJListValue(put, attr, (List<Object>)value);	
	        		break;
	            case SET:
	            	if(!(value instanceof List<?>))
	        			throw new AccessorException("the attr:{} value is not List object",attrName);        		
	            	HWrapperUtils.putJSetValue(put, attr, (Set<Object>)value);	
	        		break;
	            default:
	            	break;      	
            }
        	
        	table.put(put);
        	table.flushCommits();        	
        	// Now try to update the index data
        	sendUpdateIndexInfo(entryKey, attr, originVal, value);
        	
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
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_PUT_ATTR)
				.setTarget(entitySchema.getEntryKey(entryKey).toString());
			audit.addPredicate(AUDIT_OPER_PUT_ATTR, attrName, value);
			context.auditEnd();
        }
		return rtv;
	}
	
	@Override
	public EntryKey doPutEntry(GB entryInfo, boolean changedOnly) throws AccessorException {
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_PUT_ENTRY);
		
		HTableInterface table = null;
		EntryKey rtv = null;
		BaseEntity entitySchema = (BaseEntity)getEntitySchema();
        try {  
        	EntryKey key = entryInfo.getEntryKey();
            table = getConnection().getTable(entitySchema.getSchema(getContext().getPrincipal(),key.getKey()));
            List<EntityAttr> attrs = null;
            if(changedOnly){
            	// only put those changed attributes
            	attrs = this.filterChangedAttrList(entryInfo);
            }else{
            	attrs = entitySchema.getEntityMeta().getAllAttrs();
            	if(!validateEntry(entryInfo)){// some attributes missing
            		String missedAttrs = context.getValue(AccessorContext.KEY_VALID_MSG);
            		throw new AccessorException("Required attrs missed:{}",missedAttrs);
            	}
            }
            // Try to get original value
            Get get = new Get(key.getKey().getBytes());
            for(EntityAttr attr:attrs){
            	if(attr.isIndexable())
            		get.addColumn(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
            }
            Result oriResult = table.get(get);
            
            Put put = parse(attrs,entryInfo);

            table.put(put);
        	table.flushCommits();
        	// Prepare the index data        	
        	for(EntityAttr attr:attrs){
                if(attr.isIndexable() && oriResult.isEmpty()){
                	// create new mode
                	sendUpdateIndexInfo(key.getKey(), attr, null, entryInfo.getAttrValue(attr.getAttrName()));
                }
                else if(attr.isIndexable() && !oriResult.isEmpty()){
                	// update mode
                	byte[] cell = oriResult.getValue(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
                    Object originVal = HWrapperUtils.getPrimitiveValue(attr, cell);	
                	sendUpdateIndexInfo(key.getKey(), attr, originVal, entryInfo.getAttrValue(attr.getAttrName()));
                }
            }
        	
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
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_PUT_ENTRY).setTarget(entryInfo.getEntryKey().toString());
			Map<String,Object> predicates = entryInfo.getAuditPredicates();
			audit.addPredicates(AUDIT_OPER_PUT_ENTRY, predicates);
			context.auditEnd();
        }
		return rtv;
	}
	
	@Override
	public void doRemoveEntry(String... rowkeys) throws AccessorException {
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_DEL_ENTRY);
		
		BaseEntity entrySchema = (BaseEntity)getEntitySchema();
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			//-- comment @ 2014-3-2 by desprid
			// It's possible the schema depends on key to calculate. 
			// that means same entity include multiple schemas(tables)
			// In such case, necessary to group keys by schema
			// then perform delete on schema respectively.
			for(String key:rowkeys){
				String schemaname = entrySchema.getSchema(getContext().getPrincipal(),key);
				List<String> keys = map.get(schemaname);
				keys = keys == null? new ArrayList<String>(): keys;
				keys.add(key);
				map.put(schemaname, keys);				
			}
			
			for(Map.Entry<String, List<String>> e:map.entrySet()){
				List<EntityAttr> attrs = entrySchema.getEntityMeta().getAllAttrs();
				EntityAttr[] attrarray = attrs.toArray(new EntityAttr[0]);
				removeEntry(e.getKey(), e.getValue(), attrarray);				
			}
			
		} catch (MetaException e) {
			throw new AccessorException("Error delete entry row, key:{}",e,rowkeys);
		
		} finally{
			
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_DEL_ENTRY)
				.setTarget(entrySchema.getEntityName());
			
			audit.addPredicate(AUDIT_OPER_DEL_ENTRY, Predicate.KEY_PARAM, rowkeys);
			context.auditEnd();
		}  
	}
	
	/**
	 * Delete specified table entries with keys 
	 * 
	 * @param schemaname the name of schema
	 * @param keys the key list
	 * @param attr the entity attribute object
	 **/
	private void removeEntry(String schemaname, List<String> keys, EntityAttr ... attrs) throws AccessorException{
		HTableInterface table = null;
		String akey = null;
		
		try {

			Get get = null;
			table = getConnection().getTable(schemaname);
			HashMap<EntityAttr, Object> tempMap = new HashMap<EntityAttr, Object>();
			for(String key:keys){
				akey = key;
				if(StringUtils.isBlank(key)) continue;
				tempMap.clear();
				for(EntityAttr attr :attrs){
					// not indexable continue
					if(!attr.isIndexable()) continue;
					// try to get current value
					get = new Get(key.getBytes());
					get.addColumn(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
					Result result = table.get(get);
					byte[] cell = result.getValue(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
                    Object oldValue = HWrapperUtils.getPrimitiveValue(attr, cell);					
                    // keep in temporary map
                    tempMap.put(attr, oldValue);
				}
				Delete del = new Delete(key.getBytes());
				for(EntityAttr attr :attrs){

					// perform delete column
                    del.deleteColumns(attr.getColumn().getBytes(), attr.getQualifier().getBytes());
					
				}
				table.delete(del);
				table.flushCommits();
								
                // send remove index info
				for(Map.Entry<EntityAttr, Object> mapitem: tempMap.entrySet())
					sendRemoveIndexInfo(key, mapitem.getKey(), mapitem.getValue());
			}	       
	        
		} catch (IOException | WrapperException e) {
			
			throw new AccessorException("Error delete entry row, key:{}-{}",e,schemaname,akey);

		}finally{
        	
        	try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }   
	}
	
	@Override
	public void doRemoveEntryAttr(String attribute, String... rowkeys)throws AccessorException{
		AccessorContext context = super.getContext();		
		context.auditBegin(AUDIT_OPER_DEL_ATTR);
		
		BaseEntity entrySchema = (BaseEntity)getEntitySchema();
		EntityAttr attr = null;
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			attr = entrySchema.getEntityMeta().getAttr(attribute);
			for(String key:rowkeys){
				String schemaname = entrySchema.getSchema(getContext().getPrincipal(),key);
				List<String> keys = map.get(schemaname);
				keys = keys == null? new ArrayList<String>(): keys;
				keys.add(key);
				map.put(schemaname, keys);				
			}
			
			for(Map.Entry<String, List<String>> e:map.entrySet()){
				removeEntry(e.getKey(), e.getValue(), attr);
			}

		} catch (MetaException e) {
			throw new AccessorException("Error delete entry row, key:{}",e,rowkeys);
		} finally{
			
			// collect the audit data
			AuditInfo audit = context.getAuditInfo();
			audit.getVerb(AUDIT_OPER_DEL_ATTR)
				.setTarget(entrySchema.getEntityName());
			
			audit.addPredicate(AUDIT_OPER_DEL_ATTR, attribute, rowkeys);
			context.auditEnd();
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
	
	/**
	 * Here define a blank method, not supported by default.
	 **/
	@Override 
	public <To> IEntryConverter<GB, To> getEntryConverter(Class<To> cto){
		
		throw new UnsupportedOperationException("Not define any converter yet.");
	}
	
	/**
	 * Wrap the rawentry into bean object
	 * 
	 * @param attrs the attributes of rawEntry
	 * @param rawEntry the entry information
	 * @return GB the bean object. 
	 **/
	public void wrap(List<EntityAttr> attrs, final Result rawEntry,final GB entryInfo) throws WrapperException{
		
		//Result entry = rawEntry;
		String entityName = attrs.size()>0? (attrs.get(0).getEntityName()):EntityConstants.ENTITY_BLIND;
		if(entityName == null || entityName.length()==0){
			
			entityName = EntityConstants.ENTITY_BLIND;
		}
		
		entryInfo.setEntryKey(new EntryKey(entityName,new String(rawEntry.getRow())));
		
		for(EntityAttr attr: attrs){
			byte[] column = attr.getColumn().getBytes();
			byte[] qualifier = attr.getQualifier().getBytes();
			byte[] cell = rawEntry.getValue(column, qualifier);
			
			switch(attr.mode){
			
				case PRIMITIVE :
				
					Object value =(cell== null)? null: HWrapperUtils.getPrimitiveValue(attr, cell);
					entryInfo.setAttrValue(attr, value);	
					break;
					
				case MAP :
					
					Map<String, Object> map = (cell== null)? null: HWrapperUtils.getJMapValue(attr, cell);				
					entryInfo.setAttrValue(attr, map);
					break;
					
				case LIST :
					
					List<Object> list = (cell== null)? null: HWrapperUtils.getJListValue(attr, cell);					
					entryInfo.setAttrValue(attr, list);
					break;
					
				case SET :
					
					Set<Object> set = (cell== null)? null: HWrapperUtils.getJSetValue(attr, cell);					
					entryInfo.setAttrValue(attr, set);
					break;
					
				default:
					break;
				
			}			
		}

	}
	
	/**
	 * Parse bean object into raw Object
	 * 
	 * @param attrs the attributes of target entity
	 * @param entryInfo the entry information bean
	 * @return Object the raw object. 
	 **/	
	@SuppressWarnings("unchecked")
	public Put parse(List<EntityAttr> attrs, final GB entryInfo)throws WrapperException{
		byte[] keybytes = entryInfo.getEntryKey().getKeyBytes();
		if(keybytes == null)
			throw new WrapperException("The entrykey's cannot be null");
		
		Put put = new Put(entryInfo.getEntryKey().getKey().getBytes());

        for(EntityAttr attr:attrs){

        	Object value = entryInfo.getAttrValue(attr.getAttrName());
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("Put -> attribute:{} - value:{}",attr.getAttrName(),value);
        	}
        	if(null == value) continue;
        	
        	switch(attr.mode){
        	
        		case PRIMITIVE:
        			HWrapperUtils.putPrimitiveValue(put, attr, value);					
        			break;
        		case MAP:
        			HWrapperUtils.putJMapValue(put, attr, (Map<String,Object>)value);	
        			break;
        		case LIST:
        			HWrapperUtils.putJListValue(put, attr, (List<Object>)value);	
        			
        			break;
        		case SET:
        			HWrapperUtils.putJSetValue(put, attr, (Set<Object>)value);				
        			break;
        		default:
        			break;
        	
        	}
        }

        return put;
	}
	
	/**
	 * filter out the changed attributes out of EntityEntry
	 * @param entryInfo
	 * @return List<EntityAttr> list of changed attributes.
	 **/
	protected List<EntityAttr> filterChangedAttrList(GB entryInfo) throws MetaException{

		List<AttributeItem> itemlist = entryInfo.getAttrItemList();
		List<EntityAttr> rtv = new ArrayList<EntityAttr>();
		for(AttributeItem item: itemlist){
			if(!item.isChanged()) // unchanged ignore
				continue;
			
			EntityAttr attr;

			attr = EntityManager.getInstance().getEntityAttr(item.entity(), item.attribute());
			rtv.add(attr);
		
		}
		return rtv;
	}
	
	/**
	 * Send IndexInfo object to disruptor Index info queue. 
	 **/
	private void sendUpdateIndexInfo(String key, EntityAttr attr, Object oldValue, Object newValue){
		
		IndexInfo indexinfo = new IndexInfo(key, attr, oldValue, newValue);		
		IndexManager.getInstance().offerIndexQueue(indexinfo);		
	}
	
	/**
	 * Send IndexInfo object to disruptor Index info queue. 
	 **/
	private void sendRemoveIndexInfo(String key, EntityAttr attr, Object oldValue){
		
		IndexInfo indexinfo = new IndexInfo(IndexMode.Remove, key, attr, oldValue);		
		IndexManager.getInstance().offerIndexQueue(indexinfo);		
	}
}
