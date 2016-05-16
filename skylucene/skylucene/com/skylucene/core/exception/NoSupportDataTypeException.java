package com.skylucene.core.exception;

@SuppressWarnings("serial")
public class NoSupportDataTypeException extends NoSupportException{

    public NoSupportDataTypeException(String message) {
	super(message);
    }

    @Override
    public String getMessage() { 
	return String.format("%s Data Type: no support", message);
    }
    
    

}
