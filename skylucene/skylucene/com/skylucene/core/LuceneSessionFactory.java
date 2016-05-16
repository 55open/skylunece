package com.skylucene.core;

import java.util.HashMap;
import java.util.Map;

public class LuceneSessionFactory {
    
    private final static LuceneSessionFactory factory = new LuceneSessionFactory();
    
    public static LuceneSessionFactory getInstance(){
	return factory;
    }
    
    private  final static  Map<String,LuceneSession<?>> writers=new HashMap<String, LuceneSession<?>>();
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public LuceneSession<?> getLuceneWriter(Class<?> clazz){ 
	String clazzName = clazz.getName();
	synchronized (factory) { 
		LuceneSession<?> writer =  writers.get(clazzName);
		try {
		    if(null==writer){
		        writer = new LuceneSession(clazz);
		        writers.put(clazzName, writer);
		    } 
		    return writer;
		} catch (Exception e) { 
		    e.printStackTrace();
		}
		return null;
	} 
    }

    private LuceneSessionFactory() {
	super(); 
    }
    

}
