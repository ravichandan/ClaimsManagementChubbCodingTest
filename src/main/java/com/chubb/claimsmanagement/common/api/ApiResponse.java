package com.chubb.claimsmanagement.common.api;

import java.time.Instant;

/** Consistent response envelope returned by all REST controllers. */
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        Instant timestamp
) {
    /** Creates a successful response with a business message. */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }

    /** Creates a successful response using the default OK message. */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "OK");
    }

    /** Creates an error response without a data payload. */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, Instant.now());
    }
}
