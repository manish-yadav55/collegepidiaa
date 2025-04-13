// File: dto/response/ApiResponse.java
package com.collegepidia.collegepidia.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }
}
