package com.doccube.core;

import com.doccube.exception.BaseException;

public interface IEntryConverter <FROM, TO>{

	public TO convert(FROM fromObject) throws BaseException;
}
