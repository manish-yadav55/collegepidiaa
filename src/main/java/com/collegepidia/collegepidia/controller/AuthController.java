// Update: File: controller/AuthController.java
package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.dto.request.*;
import com.collegepidia.collegepidia.dto.response.ApiResponse;
import com.collegepidia.collegepidia.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @GetMapping
    public String welcome() {
        return "Welcome to CollegePidia API!";
    }

    @PostMapping("/register-email")
    public ResponseEntity<ApiResponse> registerEmail(@RequestBody EmailRegisterRequest request) {
        return ResponseEntity.ok(authService.registerEmail(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/set-password")
    public ResponseEntity<ApiResponse> setPassword(@RequestBody PasswordSetupRequest request) {
        return ResponseEntity.ok(authService.setPassword(request));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(authService.resendOtp(request));
    }
    
    // Forgot password flow endpoints:
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ApiResponse> forgotPasswordSendOtp(@RequestBody EmailRegisterRequest request) {
        return ResponseEntity.ok(authService.forgotPasswordSendOtp(request));
    }


    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse> forgotPasswordVerifyOtp(@RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }
    
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse> forgotPasswordReset(@RequestBody PasswordSetupRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
