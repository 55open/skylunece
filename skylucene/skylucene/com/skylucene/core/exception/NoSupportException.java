package com.skylucene.core.exception;

@SuppressWarnings("serial")
public class NoSupportException extends Exception{

    protected String message;
    
    public NoSupportException(String message){
	this.message=message;
    }

    @Override
    public String getMessage() { 
	return String.format("%s: no support", message);
    }
}
