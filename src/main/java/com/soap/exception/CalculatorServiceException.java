package com.soap.exception;

/**
 * Wraps any failure coming from the Calculator SOAP service (connectivity
 * issues, SOAP faults, or invalid input caught before calling the service)
 * behind a single, meaningful exception type instead of leaking Spring-WS's
 * internal exceptions to callers.
 */
public class CalculatorServiceException extends RuntimeException {

    public CalculatorServiceException(String message) {
        super(message);
    }

    public CalculatorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
