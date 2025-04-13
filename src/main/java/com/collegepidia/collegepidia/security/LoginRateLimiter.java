// File: security/LoginRateLimiter.java
package com.collegepidia.collegepidia.security;

import com.collegepidia.collegepidia.exception.AccessLimitExceededException;
import com.collegepidia.collegepidia.model.College;
import com.collegepidia.collegepidia.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginRateLimiter {

    private final CollegeRepository collegeRepo;
    private final int GUEST_MAX_USAGE = 20;

    public void check(String email) {
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("College not found"));
        if ("PENDING".equalsIgnoreCase(college.getVerification().getStatus().name())) {
            int count = college.getUsageCount() == null ? 0 : college.getUsageCount();
            if (count >= GUEST_MAX_USAGE) {
                throw new AccessLimitExceededException("API limit exceeded for guest user.");
            }
            college.setUsageCount(count + 1);
            collegeRepo.save(college);
        }
    }
}
