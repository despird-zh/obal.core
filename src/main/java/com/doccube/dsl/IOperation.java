package com.doccube.dsl;

public interface IOperation <R>{

	public R execute();
	
	public String toJson();
	
	public void fromJson(String json);
}
