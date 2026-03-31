package com.me.kaffe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * AuthResponse DTO
 * Response object for authentication endpoints (login, register)
 * Contains JWT token or error information
 */
@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {

    private final boolean success;
    private final String token;
    private final String errorCode;

    /**
     * Create successful authentication response with JWT token
     */
    public static AuthResponse success(String token) {
        return AuthResponse.builder()
                .success(true)
                .token(token)
                .errorCode(null)
                .build();
    }

    /**
     * Create failed authentication response with error code
     */
    public static AuthResponse failure(String errorCode) {
        return AuthResponse.builder()
                .success(false)
                .token(null)
                .errorCode(errorCode)
                .build();
    }
}

