// File: service/OtpService.java
package com.collegepidia.collegepidia.service;

import com.collegepidia.collegepidia.exception.InvalidRequestException;
import com.collegepidia.collegepidia.model.OtpToken;
import com.collegepidia.collegepidia.repository.OtpTokenRepository;
import com.collegepidia.collegepidia.utils.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpRepo;
    private final EmailUtil emailUtil;

    private static final int EXPIRY_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SEC = 30;
    // Maximum allowed OTP attempts before blocking resend
    private static final int MAX_ATTEMPTS = 5;
    // Block period (in milliseconds) after MAX_ATTEMPTS are reached
    private static final long BLOCK_DURATION_MS = 3600000; // 1 hour

    public String generateOtp(String email) {
        // Check if there’s already an OTP record that is not expired and still unverified
        Optional<OtpToken> existingOpt = otpRepo.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email);
        Date now = new Date();
        if (existingOpt.isPresent()) {
            OtpToken existing = existingOpt.get();
            if (!isExpired(existing.getExpiresAt())) {
                // Optional: You may decide to not generate a new OTP if one is still valid.
                throw new InvalidRequestException("An OTP has already been sent. Please check your email.");
            }
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        Date expiry = new Date(now.getTime() + EXPIRY_MINUTES * 60 * 1000);

        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .createdAt(now)
                .expiresAt(expiry)
                .used(false)
                .attemptCount(0)
                .resentAt(now)
                .build();

        otpRepo.save(token);
        emailUtil.sendOtpEmail(email, otp);
        return "OTP sent to " + email;
    }

    public String validateOtp(String email, String otp) {
        // Look up the latest unverified OTP
        OtpToken token = otpRepo.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new InvalidRequestException("OTP not found"));
        if (isExpired(token.getExpiresAt())) {
            throw new InvalidRequestException("OTP expired");
        }
        if (!token.getOtp().equals(otp)) {
            token.setAttemptCount(token.getAttemptCount() + 1);
            otpRepo.save(token);
            throw new InvalidRequestException("Invalid OTP");
        }
        // Mark OTP as verified (but do not delete it yet; deletion happens on password creation)
        token.setUsed(true);
        otpRepo.save(token);
        return "OTP verified successfully";
    }

    public String resendOtp(String email) {
        Optional<OtpToken> tokenOpt = otpRepo.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email);
        Date now = new Date();
        if (tokenOpt.isPresent()) {
            OtpToken token = tokenOpt.get();

            // Check if maximum attempts have been reached
            if (token.getAttemptCount() >= MAX_ATTEMPTS) {
                long timeSinceCreation = now.getTime() - token.getCreatedAt().getTime();
                if (timeSinceCreation < BLOCK_DURATION_MS) {
                    long remainingMs = BLOCK_DURATION_MS - timeSinceCreation;
                    long remainingMinutes = (remainingMs + 59999) / 60000; // round up to next minute
                    throw new InvalidRequestException("Maximum OTP attempts reached. Please try again after " + remainingMinutes + " minute(s).");
                } else {
                    // Block duration has passed—reset the attempt count before resending.
                    token.setAttemptCount(0);
                }
            }

            // Check for the resend cooldown interval (to prevent spamming within seconds)
            if ((now.getTime() - token.getResentAt().getTime()) / 1000 < RESEND_COOLDOWN_SEC) {
                throw new InvalidRequestException("Please wait a few seconds before resending OTP.");
            }

            // Generate and set new OTP details
            String newOtp = String.format("%06d", new Random().nextInt(999999));
            token.setOtp(newOtp);
            token.setExpiresAt(new Date(now.getTime() + EXPIRY_MINUTES * 60 * 1000));
            token.setResentAt(now);
            token.setUsed(false);
            otpRepo.save(token);
            emailUtil.sendOtpEmail(email, newOtp);
            return "OTP resent to " + email;
        }
        // If no OTP exists, generate a new one.
        return generateOtp(email);
    }

    private boolean isExpired(Date expiresAt) {
        return new Date().after(expiresAt);
    }
}
