package com.skylucene.core.model;

import java.lang.reflect.Method;

 

public class FieldInfo {
    
    private boolean id;
    private boolean number;
    
    private boolean index;
    private boolean analyze;
    private boolean store;
    
    private Method fieldReadMethod;
    private Method fieldWriteMethod;
    
    private FieldType fieldType;
    
    public boolean isId() {
        return id;
    }
    public void setId(boolean id) {
        this.id = id;
    }
    public boolean isNumber() {
        return number;
    }
    public void setNumber(boolean number) {
        this.number = number;
    }
    public boolean isIndex() {
        return index;
    }
    public void setIndex(boolean index) {
        this.index = index;
    }
    public boolean isAnalyze() {
        return analyze;
    }
    public void setAnalyze(boolean analyze) {
        this.analyze = analyze;
    }
    public boolean isStore() {
        return store;
    }
    public void setStore(boolean store) {
        this.store = store;
    }
    public FieldType getFieldType() {
        return fieldType;
    }
    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
    public Method getFieldReadMethod() {
        return fieldReadMethod;
    }
    public void setFieldReadMethod(Method fieldReadMethod) {
        this.fieldReadMethod = fieldReadMethod;
    }
    public Method getFieldWriteMethod() {
        return fieldWriteMethod;
    }
    public void setFieldWriteMethod(Method fieldWriteMethod) {
        this.fieldWriteMethod = fieldWriteMethod;
    }

    
    
    
    

}
