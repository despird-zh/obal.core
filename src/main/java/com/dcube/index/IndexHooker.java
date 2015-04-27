package com.dcube.index;

import com.dcube.cache.CacheEntryPipe;
import com.dcube.disruptor.EventHooker;
import com.dcube.disruptor.EventPayload;
import com.dcube.disruptor.EventType;
import com.dcube.exception.RingEventException;

public class IndexHooker extends EventHooker<CacheEntryPipe>{

	public IndexHooker() {
		super(EventType.INDEX);
	}

	@Override
	public void processPayload(EventPayload payload) throws RingEventException {
		IndexPipe indexqueue = (IndexPipe)payload;
		IndexInfo ii = indexqueue.poll();
		System.out.println("index processing --:"+ii.getEntryKey());
	}

}
