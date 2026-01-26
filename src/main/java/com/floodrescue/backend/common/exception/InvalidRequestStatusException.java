package com.floodrescue.backend.common.exception;

public class InvalidRequestStatusException extends RuntimeException {
    public InvalidRequestStatusException(String message) {
        super(message);
    }
}
