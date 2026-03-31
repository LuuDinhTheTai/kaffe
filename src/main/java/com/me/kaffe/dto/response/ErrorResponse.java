package com.me.kaffe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ErrorResponse DTO
 * Used for returning error information to clients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String error;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private int status;

    /**
     * Create a new error response
     */
    public static ErrorResponse of(String error, String code, int status) {
        return ErrorResponse.builder()
                .error(error)
                .code(code)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a new error response with message
     */
    public static ErrorResponse of(String error, String code, String message, int status) {
        return ErrorResponse.builder()
                .error(error)
                .code(code)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

