package com.parkit.parkingsystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseProperties {

	private static Properties properties;

	private final static String CONFIGFILE = "db.properties";

	public synchronized static String getProperty(String key) {
		if (properties == null) {
			InputStream inputStream = DatabaseProperties.class.getClassLoader().getResourceAsStream(CONFIGFILE);
			try {
				properties = new Properties();
				properties.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (properties.getProperty(key) != null) {
			return properties.getProperty(key);
		} else
			return "";
	}
}
