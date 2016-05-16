package com.skylucene.core.exception;

@SuppressWarnings("serial")
public class FieldNotFoundException extends NotFoundException{

    
    public FieldNotFoundException(String message) {
	super(message); 
    }
    
    @Override
    public String getMessage() { 
	return String.format("%s:field not found", message);
    }

}
