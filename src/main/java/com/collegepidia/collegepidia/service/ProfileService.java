// File: service/ProfileService.java
package com.collegepidia.collegepidia.service;

import com.collegepidia.collegepidia.dto.request.AdminAccountUpdateRequest;
import com.collegepidia.collegepidia.dto.request.BasicDetailsRequest;
import com.collegepidia.collegepidia.dto.request.ContactInfoRequest;
import com.collegepidia.collegepidia.dto.request.DocumentRequest;
import com.collegepidia.collegepidia.dto.response.ApiResponse;
import com.collegepidia.collegepidia.exception.ResourceNotFoundException;
import com.collegepidia.collegepidia.model.College;
import com.collegepidia.collegepidia.model.College.BasicDetails;
import com.collegepidia.collegepidia.model.College.ContactInfo;
import com.collegepidia.collegepidia.model.College.AdminAccount;
import com.collegepidia.collegepidia.model.College.CollegeDocument;
import com.collegepidia.collegepidia.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CollegeRepository collegeRepo;

    // Get the current college profile based on email
    public ApiResponse getProfile(String email) {
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for email: " + email));
        return ApiResponse.success("Profile retrieved", college);
    }

    // Update basic details
    public ApiResponse updateBasicDetails(String email, BasicDetailsRequest request) {
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for email: " + email));
        BasicDetails basic = BasicDetails.builder()
                .name(request.getName())
                .shortName(request.getShortName())
                .establishmentYear(request.getEstablishmentYear())
                .institutionId(request.getInstitutionId())
                .type(request.getType())
                .address(request.getAddress())
                .website(request.getWebsite())
                .build();
        college.setBasicDetails(basic);
        college.setUpdatedAt(new Date());
        collegeRepo.save(college);
        return ApiResponse.success("Basic details updated", college.getBasicDetails());
    }

    // Update contact info
    public ApiResponse updateContactInfo(String email, ContactInfoRequest request) {
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for email: " + email));
        ContactInfo contact = ContactInfo.builder()
                .primaryEmail(request.getPrimaryEmail())
                .secondaryEmail(request.getSecondaryEmail())
                .phone(request.getPhone())
                .alternatePhone(request.getAlternatePhone())
                .officialDomain(request.getOfficialDomain())
                .build();
        college.setContactInfo(contact);
        college.setUpdatedAt(new Date());
        collegeRepo.save(college);
        return ApiResponse.success("Contact info updated", college.getContactInfo());
    }

    // Update admin account details
    public ApiResponse updateAdminAccount(String email, AdminAccountUpdateRequest request) {
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for email: " + email));
        AdminAccount admin = college.getAdminAccount();
        // Update fields if provided
        if (request.getName() != null) {
            admin.setName(request.getName());
        }
        if (request.getDesignation() != null) {
            admin.setDesignation(request.getDesignation());
        }
        if (request.getPhone() != null) {
            admin.setPhone(request.getPhone());
        }
        college.setAdminAccount(admin);
        college.setUpdatedAt(new Date());
        collegeRepo.save(college);
        return ApiResponse.success("Admin account updated", admin);
    }

    // Update or add document info
    public ApiResponse updateDocuments(String email, DocumentRequest request) {
        College college = collegeRepo.findByAdminAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for email: " + email));
        // Initialize list if null
        if (college.getDocuments() == null) {
            college.setDocuments(new ArrayList<>());
        }
        // Create a new document record (or update an existing one if you want)
        CollegeDocument doc = CollegeDocument.builder()
                .type(request.getType())
                .fileUrl(request.getFileUrl())
                .uploadDate(new Date())
                .status(request.getStatus())
                .verificationNotes(request.getVerificationNotes())
                .build();
        college.getDocuments().add(doc);
        college.setUpdatedAt(new Date());
        collegeRepo.save(college);
        return ApiResponse.success("Document added/updated", doc);
    }
}
