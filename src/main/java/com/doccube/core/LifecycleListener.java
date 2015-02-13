package com.doccube.core;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.doccube.core.ILifecycle.State;

public abstract class LifecycleListener {
	
	public LifecycleListener(String name, int priority){
		
		this.name = name;
		this.priority = priority;
	}
	
	private String name = "listener";
	
	private int priority = 0;
	
	/**
	 * Get the priority of listener 
	 **/
	public int priority(){
		
		return priority;
	}
	
	/**
	 * Get the name of listener 
	 **/
	public String name(){
		
		return this.name;
	}
	
	/**
	 * Disgest event  
	 **/
	public abstract void onEvent(State event);
	

	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof LifecycleListener)) {
			return false;
		}
		// step 3
		LifecycleListener that = (LifecycleListener) other;

		return new EqualsBuilder()
			.append(this.priority, that.priority)
			.append(this.name, that.name).isEquals();

	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder(17, 37)
			.append(this.priority)
			.append(this.name).toHashCode();
			
	}
}
