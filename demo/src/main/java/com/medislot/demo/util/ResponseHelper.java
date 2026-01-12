package com.medislot.demo.util;

import com.medislot.demo.dto.ApiResponse;

public class ResponseHelper {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(null, message);
    }
}
