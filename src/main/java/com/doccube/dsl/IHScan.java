package com.doccube.dsl;
/**
 * IHScan defines the basic common functions of Scan object.
 * 
 * @author despird 
 * @version 0.1 2014-2-2 
 **/
public interface IHScan<R> {

	public IHScan<R> attribute(String... attrs);
	
	public IHScan<R> attribute(IHAttrFilter attrs);
	
	public IHScanFilters<R> filters(String Operator);
	
	public IHScan<R> getHScan();
	
	/**
	 * IHScanFilters defines the normal function of filters object
	 * @author despird
	 * @version 0.1 2014-2-1
	 **/
	public static interface IHScanFilters<R> extends IHScan<R>{

		public IHScanFilter<R> filter(String attr);
		
	}
	
	/**
	 * IHScanFilter defines the normal function of filter object
	 * @author despird
	 * @version 0.1 2014-2-1
	 **/
	public static interface IHScanFilter<R> extends IHScanFilters<R>{

		public IHScanFilter<R> eq(String value);
		
		public IHScanFilter<R> lt(String value);
		
		public IHScanFilter<R> gt(String value);
		
		public IHScanFilter<R> ne(String value);
		
		public IHScanFilter<R> lte(String value);
		
		public IHScanFilter<R> gte(String value);
		
		public IHScanFilter<R> contain(String value);
		
		public IHScanFilter<R> match(String value);
		
		public IHScanFilter<R> between(String value);
		
		public IHScanFilter<R> nbetween(String value);
	}
}
