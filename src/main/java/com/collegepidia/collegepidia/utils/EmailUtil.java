// File: util/EmailUtil.java
package com.collegepidia.collegepidia.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtil {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("CollegePidia - OTP Verification");
            message.setText("Your OTP is: " + otp + ". It is valid for 5 minutes.");
            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}", toEmail, ex);
            throw new RuntimeException("Email sending failed");
        }
    }
}
