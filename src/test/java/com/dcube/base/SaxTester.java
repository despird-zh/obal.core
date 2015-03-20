package com.dcube.base;

import javax.xml.parsers.*;

import com.dcube.aop.EnvConfigHelper;
import com.dcube.core.CoreConfigs;

import java.io.*;
//import com.sun.org.apache.xerces.internal.parsers.SAXParser;
public class SaxTester extends BaseTester {

	public static void main(String[] argv) {

		System.out.println("Example1 SAX Events:");
		initLog4j();
		try {
			//SAXParser parser = (SAXParser) Class.forName(
			//		"com.sun.org.apache.xerces.internal.parsers.SAXParser")
			//		.newInstance();
			//EnvConfigHelper handler = new EnvConfigHelper();
			//parser.setContentHandler(handler);
			//parser.parse("D:\\e.private\\obal\\test\\simple.xml");

			// 通过类装载器获取文件
			// InputStream inStream = SAXPerson.class.getClassLoader()
			// .getResourceAsStream("person.xml");
			// SAXParserFactory factory = SAXParserFactory.newInstance();
			// SAXParser saxParser = factory.newSAXParser();
			// PersonDefaultHandler handler = new PersonDefaultHandler();
			// saxParser.parse(inStream, handler);
			// inStream.close();
			InputStream is = new FileInputStream(new File("D:\\e.private\\obal\\test\\simple.xml"));
			 EnvConfigHelper handler = new EnvConfigHelper(is);
			 handler.parse("envsetting/setting");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String s = CoreConfigs.getString("aa");
		System.out.println(s);
	}

}