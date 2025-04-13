// File: dto/request/OtpVerificationRequest.java
package com.collegepidia.collegepidia.dto.request;

import lombok.*;

@Data
public class OtpVerificationRequest {
    private String email;
    private String otp;
}
