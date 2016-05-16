package com.dao.impl;


import java.util.List;

import com.dao.BaseDao;
import com.skylucene.core.LuceneSession;
import com.skylucene.core.LuceneSessionFactory;


public class BaseDaoImpl<T> implements BaseDao<T> {
    
    private LuceneSessionFactory  sessionFactory =  LuceneSessionFactory.getInstance();
    private LuceneSession<T> session;
    
    
/*    private BaseDaoImpl() {
	//System.out.println(this.getClass().getGenericInterfaces());;
	//System.out.println(this.getClass().getGenericSuperclass());
	//ParameterizedType parameterizedType = (ParameterizedType)this.getClass().getGenericSuperclass();  
	Type[] types= this.getClass().getGenericInterfaces();//parameterizedType.getActualTypeArguments();
	for (int i = 0; i < types.length; i++) {
	    ParameterizedType parameterizedType =  (ParameterizedType) types[i];
	    System.out.println(parameterizedType.getActualTypeArguments()[i]);
	}
	
	ParameterizedType parameterizedType =  (ParameterizedType) types[0];
	System.out.println();
	Class<T> entityClass= (Class<T>)(parameterizedType.getActualTypeArguments()[0]); 
	session = (LuceneSession<T>) sessionFactory.getLuceneWriter(entityClass);
    } */
    
    @SuppressWarnings("unchecked")
    public BaseDaoImpl(Class<?> clazz){
	session = (LuceneSession<T>) sessionFactory.getLuceneWriter(clazz);
    }
    
    
    
    public void save(T t){ 
	session.save(t);
    }

    @Override
    public void delete(T t) { 
	session.delete(t);
    }

    @Override
    public void update(T t) { 
	session.update(t);
    }

    @Override
    public T get(Object id) {
	return session.get(id); 
    }
    
    @Override
    public List<T> find(String sql,Object[] param,Integer pageNo,Integer row){
	return session.find(sql, param, pageNo, row);
    }
    
    

}
