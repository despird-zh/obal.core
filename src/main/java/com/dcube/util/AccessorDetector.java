package com.dcube.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AccessorDetector facilitates detect the classes under certain package.
 * 
 * @author desprid-zh
 * @version 0.1 2014-2-1
 * 
 **/
public class AccessorDetector {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AccessorDetector.class);

	// load class by class name
	private static Class<?> loadClass(String className) {
		
		try {
			return Class.forName(className);
			
		} catch (ClassNotFoundException e) {
			
			LOGGER.error("Class not found:{}", className);			
		}
		return null;
	}

	// find classes from specified directory
	private static void processDirectory(File directory, String pkgname,
			ArrayList<Class<?>> classes) {
		LOGGER.debug("Reading Directory '" + directory + "'");
		// Get the list of the files contained in the package
		String[] files = directory.list();
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i];
			String className = null;
			// we are only interested in .class files
			if (fileName.endsWith(".class")) {
				// removes the .class extension
				className = pkgname + '.'
						+ fileName.substring(0, fileName.length() - 6);
			}
			//LOGGER.debug("FileName '" + fileName + "'  =>  class '" + className + "'");
			if (className != null && !className.contains("$") ) {
				Class<?> clazz = loadClass(className);
				if(clazz != null)
					classes.add(clazz);
			}
			File subdir = new File(directory, fileName);
			if (subdir.isDirectory()) {
				processDirectory(subdir, pkgname + '.' + fileName, classes);
			}
		}
	}

	// process jar file to find classes
	private static void processJarfile(URL resource, String pkgname,
			ArrayList<Class<?>> classes) {
		String relPath = pkgname.replace('.', '/');
		String resPath = resource.getPath();
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar")
				.replaceFirst("file:", "");
		LOGGER.debug("Reading JAR file: '" + jarPath + "'");
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarPath);

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				String className = null;
				if (entryName.endsWith(".class") && entryName.startsWith(relPath)
						&& entryName.length() > (relPath.length() + "/".length())) {
					className = entryName.replace('/', '.').replace('\\', '.')
							.replace(".class", "");
				}
				//LOGGER.debug("JarEntry '" + entryName + "'  =>  class '" + className + "'");
				if (className != null && !className.contains("$") ) {
					Class<?> clazz = loadClass(className);
					if(clazz != null)
						classes.add(clazz);
				}
			}
		
		} catch (IOException | RuntimeException e) {
			throw new RuntimeException(
					"Unexpected IOException reading JAR File '" + jarPath + "'",
					e);
		} finally{
			
			try {
				jarFile.close();
			} catch (IOException e) {
				// Ignore
				LOGGER.error("Error fetch classes from jar file[{}].", e, jarPath);
			}
		}
	}

	/**
	 * Get classes list for class package
	 * 
	 * @param pkg the class package.
	 **/
	public static ArrayList<Class<?>> getClassesForPackage(Package pkg) {
		String pkgname = pkg.getName();
		return getClassesForPackage(pkgname);
	}
	
	/**
	 * Get classes list for class package
	 * 
	 * @param pkgname the name of class package.
	 **/
	public static ArrayList<Class<?>> getClassesForPackage(String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		String relPath = pkgname.replace('.', '/');

		// Get a File object for the package
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		if (resource == null) {
			throw new RuntimeException("Unexpected problem: No resource for "
					+ relPath);
		}
		LOGGER.debug("Package: '" + pkgname + "' becomes Resource: '"
				+ resource.toString() + "'");

		resource.getPath();
		if (resource.toString().startsWith("jar:")) {
			processJarfile(resource, pkgname, classes);
		} else {
			processDirectory(new File(resource.getPath()), pkgname, classes);
		}

		return classes;
	}

}
