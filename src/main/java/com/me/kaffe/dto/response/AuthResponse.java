package com.me.kaffe.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthResponse {

    private final boolean success;
    private final String token;
    private final String errorCode;

    public static AuthResponse success(String token) {
        return new AuthResponse(true, token, null);
    }

    public static AuthResponse failure(String errorCode) {
        return new AuthResponse(false, null, errorCode);
    }
}

