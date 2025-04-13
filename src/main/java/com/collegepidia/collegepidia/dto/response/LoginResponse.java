// File: dto/response/LoginResponse.java
package com.collegepidia.collegepidia.dto.response;

import com.collegepidia.collegepidia.model.College;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private College college;
}
