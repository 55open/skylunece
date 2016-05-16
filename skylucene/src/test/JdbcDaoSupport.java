package test;

import java.lang.reflect.ParameterizedType;

public abstract class JdbcDaoSupport<T> {
 
   private Class<T> clazz;
     
    @SuppressWarnings("unchecked")
    protected JdbcDaoSupport() {
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        System.out.println(clazz.getName());
    }
}