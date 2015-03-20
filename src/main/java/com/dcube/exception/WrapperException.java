package com.dcube.exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.helpers.MessageFormatter;

public class WrapperException  extends BaseException{

	private static final long serialVersionUID = 1L;
	
	private static Properties wrapper_exceps = null;
	
	static{
		InputStream is = loadStream(WrapperException.class);
		wrapper_exceps = new Properties();
		try {
			wrapper_exceps.load(is);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public WrapperException(String errorcode, String... param) {
		super(errorcode, param);
		this.message = findMessage(errorcode, param);
	}
	
    public WrapperException(String errorcode, Throwable cause,String ...param) {
        super(errorcode, cause);
        this.message = findMessage(errorcode, param);
    }
    
    public WrapperException(Throwable cause) {
        super(cause);
    }
    
    @Override
	protected String findMessage(String errorcode,String ... param){
		
		String messagePattern = wrapper_exceps.getProperty(errorcode, errorcode);
		if(errorcode.equals(messagePattern)){
			return super.findMessage(errorcode, param);
		}
		return MessageFormatter.arrayFormat(messagePattern, param).getMessage();
	}

}
