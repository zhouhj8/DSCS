package cn.edu.sysu.config;

import java.io.IOException;
import java.util.Properties;

public class ConfigOperation {
	public static String getConfigProperties(String propertiesName) {
		String value = "";
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("cn/edu/sysu/config/system.properties"));
			value = properties.getProperty(propertiesName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static void SetConfigProperties(String key,String propertiesValue) {
		
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("cn/edu/sysu/system.properties"));
			properties.setProperty(key, propertiesValue);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public  static void main(String[] args){

	}

}
