package com.investflow.analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private String timestamp = Instant.now().toString();
    private int status;
    private String message;
    private T data;
    private String error;
    private String path;
    private String correlationId;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponse<T> error(int status, String error, String message, String path, String correlationId) {
        return ApiResponse.<T>builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .correlationId(correlationId)
                .build();
    }
}
