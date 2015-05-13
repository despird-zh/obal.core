package com.dcube.launcher;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dcube.launcher.Lifecycle.LifeState;

public abstract class LifecycleHooker {
	
	public LifecycleHooker(String name, int priority){
		
		this.name = name;
		this.priority = priority;
	}
	/** The ILifecycle launcher */
	private Lifecycle launcher;
	/** default name */
	private String name = "listener";
	/** the priority of hooker */
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
	 * Dispatch event to LifecycleHooker 
	 * @param event the event of life
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
	
	/**
	 * The operation on initial event 
	 **/
	public abstract void initial();
	
	/**
	 * The operation on startup event 
	 **/
	public abstract void startup();
	
	/**
	 * The operation on shutdown event 
	 **/
	public abstract void shutdown();
	
	/**
	 * Set the ILifecycle launcher
	 * @param launcher the launcher of application
	 **/
	public void setLauncher(Lifecycle launcher){
		
		this.launcher = launcher;
	}
	
	/**
	 * Send feedback to launcher 
	 * @param errorFlag imply the message is error or normal
	 * @param message the message content.
	 **/
	public void sendFeedback(boolean errorFlag, String message){
		
		launcher.feedback(this.name, errorFlag, new Date(), message);
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
