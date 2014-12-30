package com.obal.disruptor;

public class GenericPayload<T> implements EventPayload{
	
	private T data = null;
	
	public GenericPayload(T data){
		
		this.data = data;
	}
	
	public T data(){
		
		return this.data;
	}
}
