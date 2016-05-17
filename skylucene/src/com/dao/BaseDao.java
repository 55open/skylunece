package com.dao;

import java.util.List;

public interface BaseDao<T> {
    
    public void save(T t);
    
    
    
    public void delete(T t);
    
    public void update(T t);
    
    public T get(Object id);

    public List<T> find(String sql, Object[] param, Integer pageNo, Integer row);
    
    

}
