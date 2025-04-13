// File: controller/ProfileController.java
package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.dto.request.AdminAccountUpdateRequest;
import com.collegepidia.collegepidia.dto.request.BasicDetailsRequest;
import com.collegepidia.collegepidia.dto.request.ContactInfoRequest;
import com.collegepidia.collegepidia.dto.request.DocumentRequest;
import com.collegepidia.collegepidia.dto.response.ApiResponse;
import com.collegepidia.collegepidia.exception.ResourceNotFoundException;
import com.collegepidia.collegepidia.model.College;
import com.collegepidia.collegepidia.repository.CollegeRepository;
import com.collegepidia.collegepidia.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final CollegeRepository collegeRepo;
    // Get the current authenticated college's profile
    public ApiResponse getProfile(String email) {
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for email: " + email));
        // Check if profile basic details exist
        if (college.getBasicDetails() == null) {
            return ApiResponse.error("Profile incomplete. Please complete your profile first.");
        }
        return ApiResponse.success("Profile retrieved", college);
    }
    @GetMapping  // Add this missing annotation
    public ResponseEntity<ApiResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(getProfile(email));
    }
    // Update basic details (e.g., college name, short name, establishment year, address, etc.)
    @PutMapping("/basic")
    public ResponseEntity<ApiResponse> updateBasicDetails(@RequestBody BasicDetailsRequest request,
                                                          Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateBasicDetails(email, request));
    }
    
    // Update contact information (primary email, secondary email, phone etc.)
    @PutMapping("/contact")
    public ResponseEntity<ApiResponse> updateContactInfo(@RequestBody ContactInfoRequest request,
                                                         Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateContactInfo(email, request));
    }
    
    // Update admin account info (if needed – such as name, designation, phone)
    @PutMapping("/admin")
    public ResponseEntity<ApiResponse> updateAdminAccount(@RequestBody AdminAccountUpdateRequest request,
                                                          Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateAdminAccount(email, request));
    }
    
    // Update/add a document (for license, accreditation, etc.)
    @PutMapping("/documents")
    public ResponseEntity<ApiResponse> updateDocuments(@RequestBody DocumentRequest request,
                                                       Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateDocuments(email, request));
    }
}
