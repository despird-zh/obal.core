package com.doccube.dsl;

import java.util.HashSet;
import java.util.Set;

public class HScanFilters<R> extends HScan.HScanDelegate<R> implements IHScan.IHScanFilters<R>{

	String operator = null;
	
	private Set<IHScanFilter<R>> filterSet = new HashSet<IHScanFilter<R>>();
	
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

}
