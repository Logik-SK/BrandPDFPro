package com.brandpdfpro.exception;

/**
 * Represents processing-related failures occurring
 * during PDF generation, branding, batch processing,
 * file I/O operations, or controller orchestration.
 */
public class ProcessingException extends RuntimeException {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(Throwable cause) {
        super(cause);
    }
}