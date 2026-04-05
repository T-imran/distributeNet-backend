package com.distribnet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private List<ApiValidationError> errors;
    private Map<String, Object> meta;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    public static ApiResponse<Void> successMessage(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static ApiResponse<Void> failure(String message, List<ApiValidationError> errors) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
}
