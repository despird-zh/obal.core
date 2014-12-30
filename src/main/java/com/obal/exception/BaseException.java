package com.obal.exception;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Properties;

import com.obal.common.MessageFormatter;


public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private static Properties base_exceps = null;

	protected String message;
	
	static{
		
		Class<?> selfclazz = BaseException.class;
		InputStream is = selfclazz.getClassLoader().getResourceAsStream("com/obal/exception/BaseException.properties");
		base_exceps = new Properties();
		try {
			base_exceps.load(is);
		} catch (IOException e) {

			e.printStackTrace();
		}
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
		return MessageFormatter.arrayFormat(messagePattern, param);
	}
		
	public void printStackTrace(PrintStream s)
	{	
		s.print("Exception:");
		//s.println(super.getMessage() + ":" + this.message);
		s.println( this.message);
	    super.printStackTrace(s);
	    
	}
	 
	public void printStackTrace(PrintWriter s)
	{
		s.print("Exception:");
		//s.println(super.getMessage() + ":" + this.message);
		s.println( this.message);
		super.printStackTrace(s);

	 }
	
	public void printStackTrace()
	{

		printStackTrace(System.err);
	}
	
}
