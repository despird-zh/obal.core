package com.dcube.exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.helpers.MessageFormatter;

public class MetaException extends BaseException{

	private static final long serialVersionUID = 1L;
	
	private static Properties meta_exceps = null;
	
	static{

		InputStream is = loadStream(MetaException.class);
		meta_exceps = new Properties();
		try {
			meta_exceps.load(is);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public MetaException(String errorcode, String... param) {
		super(errorcode, param);
		this.message = findMessage(errorcode, param);
	}
	
    public MetaException(String errorcode, Throwable cause,String ...param) {
        super(errorcode, cause);
        this.message = findMessage(errorcode, param);
    }
    
    public MetaException(Throwable cause) {
        super(cause);
    }
    
	@Override
	protected String findMessage(String errorcode,String ... param){
		
		String messagePattern = meta_exceps.getProperty(errorcode, errorcode);
		if(errorcode.equals(messagePattern)){
			return super.findMessage(errorcode, param);
		}
		return MessageFormatter.arrayFormat(messagePattern, param).getMessage();
	}
}
