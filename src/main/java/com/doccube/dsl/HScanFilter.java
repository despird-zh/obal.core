package com.doccube.dsl;

import com.doccube.dsl.IHScan.*;
public class HScanFilter<R> extends HScan.HScanDelegate<R> implements IHScanFilter<R>,IHScan<R>{

	String operator = null;
	String attr = null;
	String value = null;
	IHScanFilters<R> scanFilters = null;
	
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
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> gt(String value) {
		this.operator = "gt";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> ne(String value) {
		this.operator = "ne";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> lte(String value) {
		this.operator = "lte";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> gte(String value) {
		this.operator = "gte";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> contain(String value) {
		this.operator = "contain";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> match(String value) {
		this.operator = "match";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> between(String value) {
		this.operator = "between";
		this.value = value;
		return this;
	}

	@Override
	public IHScanFilter<R> nbetween(String value) {
		this.operator = "nbetween";
		this.value = value;
		return null;
	}

	@Override
	public IHScanFilter<R> filter(String attr) {
		
		return (IHScanFilter<R>)this.scanFilters.filter(attr);
	}

	public void setOperator(String operator) {

		this.operator = operator;
	}

	public String getOperator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IHScanFilters<R> filters() {
		return (IHScanFilter<R>)this.scanFilters;
	}
}
