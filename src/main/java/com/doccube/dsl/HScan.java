package com.doccube.dsl;

import java.util.ArrayList;
import java.util.List;

public class HScan<R> implements IHScan<R>,IOperation<R>{

	List<String> attrList = new ArrayList<String>();
	IHAttrFilter attrFilter = null;
	IHScanFilters<R> filters = null;
	
	private String schema;
	
	public HScan(String schema){
		
		this.schema = schema;
	}
	
	public IHScan<R> attribute(String... attrs){
		
		for(String attr:attrs)
			this.attrList.add(attr);
		
		return this;
	}
	
	public IHScan<R> attribute(IHAttrFilter attrFilter){
		
		this.attrFilter = attrFilter;
		
		return this;
	}
	
	@Override
	public IHScanFilters<R> filters(String operator) {

		this.filters = new HScanFilters<R>(this,operator);
		
		return this.filters;
	}

	public IHScanFilter<R> filter(String attr) {
		
		this.filters = new HScanFilters<R>(this,null);
		return this.filter(attr);
	}
	
	@Override
	public IHScan<R> getHScan() {
		return this;
	}
	
	@Override
	public R execute() {
		return null;
	}

	/**
	 * HScanDelegate defines the normal function of scan delegate object
	 * it will be the base class of IHScanFilter and IHScanFilters object.
	 * @author despird
	 * @version 0.1 2014-2-1
	 **/
	protected static class HScanDelegate<D> implements IHScan<D>{

		private IHScan<D> scan = null;
		public HScanDelegate(IHScan<D> scan){
			
			this.scan = scan;
		}
		
		@Override
		public IHScan<D> attribute(String... attrs) {
			return this.scan.attribute(attrs);
		}

		@Override
		public IHScan<D> attribute(IHAttrFilter attrs) {
			return this.scan.attribute(attrs);
		}

		@Override
		public IHScanFilters<D> filters(String Operator) {
			return this.filters(Operator);
		}
		
		@Override
		public IHScan<D> getHScan() {
			return this.scan;
		}
	}

}
