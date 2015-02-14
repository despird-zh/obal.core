package com.doccube.dsl;

public interface IHGet<R> {

	public IHGet<R> row(String row);
	
	public IHGet<R> attribute(String... attrs);
	
	public IHGetFilter<R> filter();
	
	public static interface IHGetFilter<FR>{
		
		public IHGetFilter<FR> eg(String value);
	}
}
