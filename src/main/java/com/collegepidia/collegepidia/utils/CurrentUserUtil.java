package com.collegepidia.collegepidia.utils;

import com.collegepidia.collegepidia.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Utility to extract current user (email) from the JWT Authorization header of the request.
 * This bean is request-scoped, so it holds info for each HTTP request.
 */
@Component
@RequestScope
public class CurrentUserUtil {

    private final String currentUserEmail;

    @Autowired
    public CurrentUserUtil(HttpServletRequest request, JwtService jwtService) {
        // Extract JWT from the Authorization header and parse the email claim
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            this.currentUserEmail = jwtService.extractEmail(token);
        } else {
            this.currentUserEmail = null;
        }
    }

    /** Returns the email of the current authenticated user. */
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }
}
