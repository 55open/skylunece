package com.skylucene.core;

import java.util.Map;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Sort;

import com.skylucene.core.model.FieldInfo;

public abstract class LuceneSqlHandler { 
    
    public abstract LuceneSqlHandler initTable(Class<?> tableClazz,Map<String, FieldInfo> fieldInfos);
    
    public abstract void handle(BooleanQuery rootQuery,Sort sort , String sql,Object[] param) throws Exception;

}
