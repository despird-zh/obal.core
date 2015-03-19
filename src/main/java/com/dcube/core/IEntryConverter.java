package com.dcube.core;

import com.dcube.exception.BaseException;

public interface IEntryConverter <FROM, TO>{

	public TO convert(FROM fromObject) throws BaseException;
}
