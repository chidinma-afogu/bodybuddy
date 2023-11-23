package com.Klusterthon.Medbot.exception;


public class CustomUniqueConstraintViolationException  extends RuntimeException{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CustomUniqueConstraintViolationException() {
	super("Data is not unique");
    }

    public CustomUniqueConstraintViolationException(String message) {
	super(message);
    }

}