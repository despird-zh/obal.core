package com.doccube.dsl;

import com.doccube.dsl.IHScan.*;
public class HScanFilter<R> extends HScan.HScanDelegate<R> implements IHScanFilter<R>,IHScan<R>{

	String operator = null;
	String attr = null;
	String value = null;
	IHScanFilters scanFilters = null;
	
	public HScanFilter(IHScan<R> scan,IHScanFilters<R> scanFilters, String attr) {
		super(scan);
		this.scanFilters = scanFilters;
		this.attr = attr;
	}

	@Override
	public IHScanFilter<R> eq(String value) {
		this.operator = "eq";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> lt(String value) {
		this.operator = "lt";
		return this;
	}

	@Override
	public IHScanFilter<R> gt(String value) {
		this.operator = "gt";
		return this;
	}

	@Override
	public IHScanFilter<R> ne(String value) {
		this.operator = "ne";
		return this;
	}

	@Override
	public IHScanFilter<R> lte(String value) {
		this.operator = "lte";
		return this;
	}

	@Override
	public IHScanFilter<R> gte(String value) {
		this.operator = "gte";
		return this;
	}

	@Override
	public IHScanFilter<R> contain(String value) {
		this.operator = "contain";
		return this;
	}

	@Override
	public IHScanFilter<R> match(String value) {
		this.operator = "match";
		return this;
	}

	@Override
	public IHScanFilter<R> between(String value) {
		this.operator = "between";
		return this;
	}

	@Override
	public IHScanFilter<R> nbetween(String value) {
		this.operator = "nbetween";
		return null;
	}

	@Override
	public IHScanFilter<R> filter(String attr) {
		
		return this.scanFilters.filter(attr);
	}	
}
