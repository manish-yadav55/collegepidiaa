// File: service/DashboardService.java
package com.collegepidia.collegepidia.service;

import com.collegepidia.collegepidia.dto.response.ApiResponse;
import com.collegepidia.collegepidia.exception.ResourceNotFoundException;
import com.collegepidia.collegepidia.model.College;
import com.collegepidia.collegepidia.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CollegeRepository collegeRepo;

    public ApiResponse getDashboardInfo(String email) {
        College c = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("College not found for email: " + email));

        if (!c.isProfileCreated()) {
            return ApiResponse.error("Profile incomplete. Please complete your profile first.");
        }

        String status = c.getVerification().getStatus().name();
        if ("PENDING".equalsIgnoreCase(status)) {
            return ApiResponse.success(
                    "Welcome " + c.getBasicDetails().getName() +
                            "! Your account is pending verification. You are in guest mode.");
        } else if ("VERIFIED".equalsIgnoreCase(status)) {
            return ApiResponse.success(
                    "Welcome " + c.getBasicDetails().getName() +
                            "! Your college is verified. Enjoy full access.", c);
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            return ApiResponse.error("Your college account verification has been rejected.");
        }

        return ApiResponse.error("Invalid verification status.");
    }


}
