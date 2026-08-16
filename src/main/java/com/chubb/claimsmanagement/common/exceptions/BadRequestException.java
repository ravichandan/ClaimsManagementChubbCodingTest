package com.chubb.claimsmanagement.common.exceptions;

/** Signals a request that violates a domain validation or lifecycle rule. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
