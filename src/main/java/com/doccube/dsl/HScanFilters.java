package com.doccube.dsl;

import java.util.HashSet;
import java.util.Set;

public class HScanFilters<R> extends HScan.HScanDelegate<R> implements IHScan.IHScanFilters<R>{

	String operator = null;
	
	private Set<IHScanFilter<R>> filterSet = new HashSet<IHScanFilter<R>>();
	
	public HScanFilters(HScan<R> scan) {
		super(scan);
	}
	
	public HScanFilters(HScan<R> scan, String operator) {
		super(scan);
		this.operator = operator;
	}

	@Override
	public IHScanFilter<R> filter(String attr) {
		
		HScanFilter<R> filter = new HScanFilter<R>(super.getHScan(),this,attr);
		filterSet.add(filter);
		
		return filter;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		
		return this.operator;
	}

	public Set<IHScanFilter<R>> filterSet(){
		
		return this.filterSet;
	}
}
