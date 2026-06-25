package com.brandpdfpro.exception;

/**
 * Exception thrown when a structural integrity violation or validation failure
 * occurs during licensing operations.
 */
public class LicenseException extends Exception {

    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }
}