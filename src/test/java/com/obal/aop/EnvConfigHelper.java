package com.obal.aop;

import javax.xml.parsers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Deprecated
public class EnvConfigHelper extends DefaultHandler {

	Logger LOGGER = LoggerFactory.getLogger(EnvConfigHelper.class);
	private InputStream is;
	private Map<String,String> cfgMap = new HashMap<String,String>();
	private String pathPattern;
	public EnvConfigHelper(InputStream is){
		
		 this.is = is;
	}
	
	Stack<String> stack = new Stack<String>();
	// current element
	String currentElement = null;
	String currentValue   = null;
	String currentKey     = null;
	
	public void startDocument() throws SAXException {
		LOGGER.debug("SAX Event: START DOCUMENT");
	}

	public void endDocument() throws SAXException {
		LOGGER.debug("SAX Event: END DOCUMENT");
	}

	public void startElement(String namespaceURI, String localName,	String qName, Attributes attr) throws SAXException {
		LOGGER.debug("SAX Event: START ELEMENT[ " + namespaceURI +":"+ localName + ":" + qName +" ]");
		currentElement = qName;
		stack.push(qName);
		// 如果有属性，我们也一并打印出来．．．
		for (int i = 0; i < attr.getLength(); i++) {
			LOGGER.debug("   ATTRIBUTE: " + attr.getLocalName(i)
					+ " VALUE: " + attr.getValue(i));
		}		
		currentKey = attr.getValue("name");
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		
		if(pathPattern.equals(currentPath())){
			cfgMap.put(currentKey, currentValue);
			LOGGER.debug("key:{}--value:{}",currentKey, currentValue);
		}
		LOGGER.debug("SAX Event: END ELEMENT[ " + localName + " ]");
		stack.pop();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
	
		String value = new String(ch, start,length).trim();
		if(this.currentElement == stack.peek())
			currentValue = value;
		
		LOGGER.debug("SAX Event: CHARACTERS[{}]",value);

	}
	
	private String currentPath(){
		
		StringBuffer sb = new StringBuffer();
		 if (!stack.empty()){
	        Enumeration<String> items = stack.elements(); 
	        while (items.hasMoreElements()) 
	        	sb.append(items.nextElement()).append('/');
	     }
		sb.deleteCharAt(sb.length()-1);
		LOGGER.debug("path:{}",sb.toString());
		return sb.toString();
	}
	
	public void parse(String pathPattern){
		
		this.pathPattern = pathPattern;
		 SAXParserFactory factory = SAXParserFactory.newInstance();
		 SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(is, this);
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

}