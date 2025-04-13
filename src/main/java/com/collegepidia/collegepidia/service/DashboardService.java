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
        // Retrieve the College using the admin account email
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("College not found for email: " + email));
        
        // Check if profile is complete. 
        // For example, if basicDetails is null, assume profile is incomplete.
        if (college.getBasicDetails() == null) {
            return ApiResponse.error("Profile incomplete. Please complete your profile first.");
        }
        
        // If profile is complete, continue based on verification status
        String status = college.getVerification().getStatus().name();
        if ("PENDING".equalsIgnoreCase(status)) {
            return ApiResponse.success("Welcome " 
                    + (college.getBasicDetails() != null ? college.getBasicDetails().getName() : "User")
                    + "! Your account is pending verification. You are in guest mode with limited access.");
        } else if ("VERIFIED".equalsIgnoreCase(status)) {
            return ApiResponse.success("Welcome " 
                    + college.getBasicDetails().getName() 
                    + "! Your college is verified. Enjoy full access to the application features.", college);
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            return ApiResponse.error("Your college account verification has been rejected. Please contact support.");
        }
        return ApiResponse.error("Invalid verification status.");
    }
}
