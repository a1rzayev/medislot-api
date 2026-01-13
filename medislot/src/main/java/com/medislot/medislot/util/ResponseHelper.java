package com.medislot.medislot.util;

import com.medislot.medislot.dto.ApiResponse;

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
