package com.skylucene.core.exception;

@SuppressWarnings("serial")
public class NotFoundException extends Exception{
    
    protected String message;
    
    public NotFoundException(String message){
	this.message=message;
    }

    @Override
    public String getMessage() { 
	return String.format("%s: not found", message);
    }

    
}
