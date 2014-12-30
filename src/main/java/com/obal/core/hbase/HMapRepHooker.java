package com.obal.core.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.mapreduce.ResultSerialization;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.serializer.Deserializer;

import com.obal.core.EntryKey;
import com.obal.disruptor.EventPayload;
import com.obal.disruptor.EventType;
import com.obal.disruptor.GenericHooker;
import com.obal.disruptor.GenericPayload;
import com.obal.exception.RingEventException;
import com.obal.exception.WrapperException;
import com.obal.meta.EntityAttr;

public class HMapRepHooker<K extends EntryKey> extends GenericHooker<DataOutputBuffer>{

	
	List<K> resultList = new ArrayList<K>();
	
	HEntryWrapper<K> wrapper = null;
	
	List<EntityAttr> attrs = null;
	
	public HMapRepHooker(EventType eventType, HEntryWrapper<K> wrapper) {
		
		super(eventType);
		this.wrapper = wrapper;
	}

	public void setEntryAttrs(List<EntityAttr> attrs){
		
		this.attrs = attrs;
	}
	
	@Override
	public void processPayload(EventPayload payload) throws RingEventException {
		@SuppressWarnings("unchecked")
		GenericPayload<DataOutputBuffer> gpayload = (GenericPayload<DataOutputBuffer>)payload;
		DataOutputBuffer dobuf = gpayload.data();
		DataInputBuffer dibuf = new DataInputBuffer();
		dibuf.reset(dobuf.getData(),dobuf.getLength());
		ResultSerialization rstSerialUtil = new ResultSerialization();
		Deserializer<Result> deserializer = rstSerialUtil.getDeserializer(Result.class);
		try {
			deserializer.open(dibuf);
			Result result = deserializer.deserialize(new Result());
			K resultBean = wrapper.wrap(attrs, result);
			resultList.add(resultBean);
		} catch (IOException | WrapperException e) {
			throw new RingEventException("Error parsing result({}) stream",e,getEventType().toString());
		}finally{
			dobuf.reset();
			try {
				dibuf.reset();
			} catch (IOException e) {
				throw new RingEventException("Error parsing result({}) stream",e,getEventType().toString());
			}
		}
		
	}

	public List<K> getResultList(){
		
		return this.resultList;
	}
}
