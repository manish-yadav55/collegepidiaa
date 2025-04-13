// File: service/AuthService.java
package com.collegepidia.collegepidia.service;

import com.collegepidia.collegepidia.dto.request.*;
import com.collegepidia.collegepidia.dto.response.ApiResponse;
import com.collegepidia.collegepidia.dto.response.LoginResponse;
import com.collegepidia.collegepidia.dto.response.RefreshTokenResponse;
import com.collegepidia.collegepidia.model.VerificationStatus;
import com.collegepidia.collegepidia.exception.ResourceNotFoundException;
import com.collegepidia.collegepidia.model.College;
import com.collegepidia.collegepidia.model.OtpToken;
import com.collegepidia.collegepidia.repository.CollegeRepository;
import com.collegepidia.collegepidia.repository.OtpTokenRepository;
import com.collegepidia.collegepidia.security.JwtService;
import com.collegepidia.collegepidia.security.LoginRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CollegeRepository collegeRepo;
    private final OtpTokenRepository otpRepo;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final LoginRateLimiter rateLimiter;

    public ApiResponse registerEmail(EmailRegisterRequest request) {
        if (collegeRepo.findByAdminAccountEmail(request.getEmail()).isPresent()) {
            return ApiResponse.error("Email already registered.");
        }
        String otpMsg = otpService.generateOtp(request.getEmail());
        return ApiResponse.success(otpMsg);
    }

    public ApiResponse verifyOtp(OtpVerificationRequest request) {
        String result = otpService.validateOtp(request.getEmail(), request.getOtp());
        return ApiResponse.success(result);
    }

    public ApiResponse setPassword(PasswordSetupRequest request) {
        // Fetch the latest OTP record regardless of the used flag
        Optional<OtpToken> optToken = otpRepo.findTopByEmailOrderByCreatedAtDesc(request.getEmail());
        System.out.println("OTP Record: " + optToken);
        if (optToken.isEmpty()) {
            return ApiResponse.error("OTP verification not completed for this email");
        }
        OtpToken token = optToken.get();
        // Check if OTP has been verified
        if (!token.isUsed()) {
            return ApiResponse.error("OTP not verified");
        }
        // Prevent duplicate account creation
        if (collegeRepo.findByAdminAccountEmail(request.getEmail()).isPresent()) {
            return ApiResponse.error("Account already exists");
        }

        // Create College record
        College college = College.builder()
                .basicDetails(null)  // To be updated later during profile completion
                .contactInfo(null)
                .adminAccount(
                        College.AdminAccount.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .build()
                )
                .verification(
                        College.VerificationDetails.builder()
                                .status(VerificationStatus.PENDING)
                                .build()
                )
                .usageCount(0)
                .createdAt(new Date())
                .build();
        collegeRepo.save(college);
        // Delete the OTP record now that it has served its purpose
        otpRepo.delete(token);
        return ApiResponse.success("Password set; account created with pending verification.");
    }




    public ApiResponse login(LoginRequest request) {
        try {
            rateLimiter.check(request.getEmail());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            return ApiResponse.error("Invalid credentials");
        }
        Optional<College> collegeOpt = collegeRepo.findByAdminAccountEmail(request.getEmail());
        if (collegeOpt.isEmpty()) {
            return ApiResponse.error("Account not found. Please register and verify your email before logging in.");
        }
        College college = collegeOpt.get();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(college.getAdminAccount().getEmail())
                .password(college.getAdminAccount().getPassword())
                .roles("COLLEGE")
                .build();
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        LoginResponse response = new LoginResponse(accessToken, refreshToken, college);
        return ApiResponse.success("Login successful", response);
    }

    // New refresh token endpoint that issues both new access and new refresh tokens
    public ApiResponse refreshToken(RefreshTokenRequest request) {
        String oldRefreshToken = request.getRefreshToken();
        if (!jwtService.isRefreshTokenValid(oldRefreshToken)) {
            return ApiResponse.error("Invalid or expired refresh token");
        }
        String email = jwtService.extractEmail(oldRefreshToken);
        Optional<College> collegeOpt = collegeRepo.findByAdminAccountEmail(email);
        if (collegeOpt.isEmpty()) {
            return ApiResponse.error("College not found for refresh token");
        }
        College college = collegeOpt.get();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(college.getAdminAccount().getEmail())
                .password(college.getAdminAccount().getPassword())
                .roles("COLLEGE")
                .build();
        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        RefreshTokenResponse response = new RefreshTokenResponse(newAccessToken, newRefreshToken);
        return ApiResponse.success("Token refreshed", response);
    }

    public ApiResponse resendOtp(ResendOtpRequest request) {
        String msg = otpService.resendOtp(request.getEmail());
        return ApiResponse.success(msg);
    }

    public ApiResponse forgotPasswordSendOtp(EmailRegisterRequest request) {
        // Check if account exists
        if (collegeRepo.findByAdminAccountEmail(request.getEmail()).isEmpty()) {
            return ApiResponse.error("Email not registered.");
        }
        // Account exists so generate OTP (using same logic as generateOtp)
        String otpMsg = otpService.generateOtp(request.getEmail());
        return ApiResponse.success(otpMsg);
    }

    public ApiResponse resetPassword(PasswordSetupRequest request) {
        Optional<OtpToken> optToken = otpRepo.findTopByEmailOrderByCreatedAtDesc(request.getEmail());
        if (optToken.isEmpty()) {
            return ApiResponse.error("OTP verification required for reset");
        }
        OtpToken token = optToken.get();
        if (!token.isUsed()) {
            return ApiResponse.error("OTP not verified");
        }
        College college = collegeRepo.findByAdminAccountEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        college.getAdminAccount().setPassword(passwordEncoder.encode(request.getPassword()));
        collegeRepo.save(college);
        otpRepo.delete(token);
        return ApiResponse.success("Password reset successful.");
    }
}
