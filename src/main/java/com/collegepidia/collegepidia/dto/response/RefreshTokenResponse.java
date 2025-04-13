// File: dto/response/RefreshTokenResponse.java
package com.collegepidia.collegepidia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
