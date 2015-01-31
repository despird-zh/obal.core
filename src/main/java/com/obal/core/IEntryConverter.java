package com.obal.core;

import com.obal.exception.BaseException;

public interface IEntryConverter <FROM, TO>{

	public TO convert(FROM fromObject) throws BaseException;
}
