package com.obal.disruptor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The event type enum 
 **/
public class EventType {
	
	private int type = -1;
	
	public EventType(int type){
		
		this.type = type;
	}
	
	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof EventType)) {
			return false;
		}
		// step 3
		EventType that = (EventType) other;
		// step 4
		return new EqualsBuilder()
			.append(this.type, that.type).isEquals();
	}

	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.type).toHashCode();
	}
	
	@Override
	public String toString(){
		
		return "type:"+String.valueOf(type);
	}
	public static EventType AUDIT   = new EventType(10); // audit event
	public static EventType CACHE   = new EventType(11); // cache event
	public static EventType UNKNOWN = new EventType(12); // unknow event
}
