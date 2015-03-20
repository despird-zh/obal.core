package com.dcube.core;

import com.dcube.exception.BaseException;

/**
 * IEntryConverter define the methods to support conversion between FROM and TO classes type.
 * Mostly it is used in EntityAccessor to acquire the specified convert tool.
 * 
 * @author despird-zh 
 * @version 0.2 2014-2-1
 **/
public interface IEntryConverter <FROM, TO>{

	/**
	 * Convert the object from <FROM> class to <TO> class 
	 **/
	public TO toTarget(FROM fromObject) throws BaseException;
	
	/**
	 * Convert the object from <TO> class to <FROM> class 
	 **/
	public FROM toSource(TO toObject) throws BaseException;
}
