package com.skylucene.core.config;

import java.util.Map;

public class Config {
    
    public  static Class<?> ANALYZER_IMPL_CLAZZ ; 
    public  static String INDEX_ROOT_PATH ; 
    public  static Class<?> LUCENE_SQL_HANDLER_IMPL_CLAZZ ;

    
    static{
	try {
	    Map<String,String> map = PropertiesUtil.getResProp("skylucene.properties");
	    INDEX_ROOT_PATH = map.get("index.root.path");
	    ANALYZER_IMPL_CLAZZ =Class.forName(map.get("analyzer.impl.clazz")); 
	    LUCENE_SQL_HANDLER_IMPL_CLAZZ =Class.forName(map.get("lucene.sql.handler.impl.clazz"));
	} catch (ClassNotFoundException e) { 
	    e.printStackTrace();
	}
    }


}
