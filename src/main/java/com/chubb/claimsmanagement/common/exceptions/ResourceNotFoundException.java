package com.chubb.claimsmanagement.common.exceptions;

/** Signals that a requested domain resource does not exist. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
