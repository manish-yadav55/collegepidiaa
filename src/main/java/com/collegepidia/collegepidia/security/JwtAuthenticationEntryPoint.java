// File: security/JwtAuthenticationEntryPoint.java
package com.collegepidia.collegepidia.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Set 401 Unauthorized response when authentication fails
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: " + authException.getMessage());
    }
}
