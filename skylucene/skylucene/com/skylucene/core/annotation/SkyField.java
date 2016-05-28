package com.skylucene.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;  

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface SkyField {

    /**
     * 索引
     * @return
     */
    Index	index()		default Index.YES;
    /**
     * 分词
     * @return
     */
    Analyze	analyze()	default Analyze.NO;
    /**
     * 存储
     * @return
     */
    Store	store()		default Store.YES;
    
    public enum Index{
	NO,YES;
    }
    public enum Analyze{
	NO,YES;
    }
    public enum Store{
	NO,YES;
    }
}
