package com.dcube.exception;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.slf4j.helpers.MessageFormatter;



public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private static Properties base_exceps = null;

	protected String message;
	
	static{
		InputStream is = loadStream(BaseException.class);
		base_exceps = new Properties();
		try {
			base_exceps.load(is);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public static InputStream loadStream(Class<?> selfclazz){

		String fullname = selfclazz.getName();
		fullname = fullname.replace('.', '/');
		fullname = fullname + ".properties";
		InputStream is = selfclazz.getClassLoader().getResourceAsStream(fullname);
		
		return is;
	}
	
	public BaseException(String errorcode,String ...param){
		super(errorcode);
		this.message = findMessage(errorcode, param);
	}
	
    public BaseException(String errorcode, Throwable cause,String ...param) {
        super(errorcode, cause);
        this.message = findMessage(errorcode, param);
    }
    
    public BaseException(Throwable cause) {
        super(cause);
    }
    
	public String getErrorcode(){
		
		return super.getMessage();
	}
	
	@Override
	public String getMessage(){
		
		return this.message;
	}
	
	protected String findMessage(String errorcode,String ... param){
		
		String messagePattern = base_exceps.getProperty(errorcode, errorcode);		
		return MessageFormatter.arrayFormat(messagePattern, param).getMessage();
	}
		
	public void printStackTrace(PrintStream s)
	{	
	    super.printStackTrace(s);
	    
	}
	 
	public void printStackTrace(PrintWriter s)
	{
		super.printStackTrace(s);

	 }
	
	public void printStackTrace()
	{

		printStackTrace(System.err);
	}
	
}
