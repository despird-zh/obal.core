package com.doccube.dsl;

import java.util.ArrayList;
import java.util.List;

import com.doccube.dsl.IHGet;

public class HGet<R> implements IHGet<R>{

	List<String> attrList = new ArrayList<String>();
	
	private String schema;
	
	private String row = null;
	
	public HGet(String schema){
		this.schema = schema;
	}
	
	@Override
	public IHGet<R> row(String row) {
		this.row = row;
		return this;
	}

	@Override
	public IHGet<R> attribute(String... attrs) {

		for(String attr:attrs)
			this.attrList.add(attr);
		
		return this;
	}

	@Override
	public IHGetFilter<R> filter() {
		// TODO Auto-generated method stub
		return null;
	}

}
