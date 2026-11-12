package com.ritesh.dispatch.exception;

/** Thrown when a request is well-formed JSON but breaks a business rule (maps to HTTP 400). */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
