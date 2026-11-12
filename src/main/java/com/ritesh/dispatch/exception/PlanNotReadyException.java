package com.ritesh.dispatch.exception;

/** Thrown when a plan is requested before orders and vehicles were submitted (maps to HTTP 409). */
public class PlanNotReadyException extends RuntimeException {
    public PlanNotReadyException(String message) {
        super(message);
    }
}
