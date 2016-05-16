package com.skylucene.core.config;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
    
    public static Map<String ,String> getResProp(String resPath){
    	Map<String, String> mapProp = new HashMap<String, String>();
    	try {
	    Properties prop = new Properties();
	    InputStream in = PropertiesUtil.class.getResourceAsStream("/"+resPath);
	    prop.load(in);
	    Enumeration en = prop.propertyNames();
	    while (en.hasMoreElements()) {
	    	    String key = (String) en.nextElement();
	            String Property = prop.getProperty (key);
	            mapProp.put(key, Property);
	    }
	    
	    
	} catch (Exception e) { 
	    e.printStackTrace();
	}
    	return mapProp;
}

}
