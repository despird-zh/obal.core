package com.dcube.launcher;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.launcher.ILifecycle.LifeState;

public abstract class LifecycleHooker {
	
	public LifecycleHooker(String name, int priority){
		
		this.name = name;
		this.priority = priority;
	}
	
	private ILifecycle launcher;
	
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
	public void onEvent(LifeState event){
		switch(event){
		case INITIAL:		
			initial();
			break;
		case STARTUP:
			startup();
			break;
		case SHUTDOWN:
			shutdown();
			break;
		default:
			;
		}
	}
	
	public abstract void initial();
	
	public abstract void startup();
	
	public abstract void shutdown();
	
	/**
	 * Set the ILifecycle launcher
	 **/
	public void setLauncher(ILifecycle launcher){
		
		this.launcher = launcher;
	}
	
	/**
	 * Send feedback to launcher 
	 **/
	public void sendFeedback(boolean errorFlag, String message){
		
		launcher.receiveFeedback(this.name, errorFlag, new Date(), message);
	}
	
	@Override
	public boolean equals(Object other) {
		// step 1
		if (other == this) {
			return true;
		}
		// step 2
		if (!(other instanceof LifecycleHooker)) {
			return false;
		}
		// step 3
		LifecycleHooker that = (LifecycleHooker) other;

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
