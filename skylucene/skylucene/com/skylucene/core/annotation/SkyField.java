package com.skylucene.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;  

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface SkyField {

    
    Index	index()		default Index.YES;
    Analyze	analyze()	default Analyze.NO;
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
