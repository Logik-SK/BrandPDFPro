package com.brandpdfpro.exception;

/**
 * Thrown when user input validation fails before PDF processing starts.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}