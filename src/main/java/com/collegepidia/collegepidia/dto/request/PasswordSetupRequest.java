// File: dto/request/PasswordSetupRequest.java
package com.collegepidia.collegepidia.dto.request;

import lombok.*;

@Data
public class PasswordSetupRequest {
    private String email;
    private String password;
}
