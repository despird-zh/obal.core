package com.obal.exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.obal.common.MessageFormatter;

public class SecurityException extends BaseException{

	private static final long serialVersionUID = 1L;
	
	private static Properties secu_exceps = null;
	
	static{
		
		Class<?> selfclazz = MetaException.class;
		String fullname = selfclazz.getName();
		fullname = fullname.replace('.', '/');
		fullname = fullname + ".properties";
		InputStream is = selfclazz.getClassLoader().getResourceAsStream(fullname);
		secu_exceps = new Properties();
		try {
			secu_exceps.load(is);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public SecurityException(String errorcode, String... param) {
		super(errorcode, param);
		this.message = findMessage(errorcode, param);
	}
	
    public SecurityException(String errorcode, Throwable cause,String ...param) {
        super(errorcode, cause);
        this.message = findMessage(errorcode, param);
    }
    
    public SecurityException(Throwable cause) {
        super(cause);
    }
    
	@Override
	protected String findMessage(String errorcode,String ... param){
		
		String messagePattern = secu_exceps.getProperty(errorcode, errorcode);
		if(errorcode.equals(messagePattern)){
			return super.findMessage(errorcode, param);
		}
		return MessageFormatter.arrayFormat(messagePattern, param);
	}
}
