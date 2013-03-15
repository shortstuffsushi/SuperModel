package com.grahammueller.supermodel.gen.exception;

/**
 * Exception indicating failure to connect to
 * requested database while attempting to generate
 * entity tables.
 * 
 * @author gmueller
 */
public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;
}
