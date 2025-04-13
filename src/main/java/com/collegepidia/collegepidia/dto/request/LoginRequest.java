// File: dto/request/LoginRequest.java
package com.collegepidia.collegepidia.dto.request;

import lombok.*;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
