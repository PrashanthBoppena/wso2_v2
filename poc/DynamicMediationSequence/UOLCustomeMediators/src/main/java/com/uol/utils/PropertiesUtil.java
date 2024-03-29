package com.uol.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

	public static Properties propertiesFileRead(String filename) {
		// Load the properties file containing dynamic queries
		Properties properties = new Properties();
		try {
			FileInputStream fileInputStream = new FileInputStream(filename);
			properties.load(fileInputStream);
			fileInputStream.close();
		} catch (IOException e) {
			System.out.println("File Not Found"+e);
			e.printStackTrace();
		}
		return properties;
	}
	
	
}
