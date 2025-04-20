// File: model/College.java
package com.collegepidia.collegepidia.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "colleges")
public class College {

    @Id
    private String id;

    private BasicDetails basicDetails;
    private ContactInfo contactInfo;
    private AdminAccount adminAccount;
    private VerificationDetails verification;
    private List<CollegeDocument> documents;
    private List<SocialMedia> socialMedia;
    private Date createdAt;
    private Date updatedAt;
    private Integer usageCount; // For guest mode API limit tracking
    @Builder.Default
    private boolean profileCreated = false;
    // Nested Classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicDetails {
        private String name;
        private String shortName;
        private Integer establishmentYear;
        private String institutionId;
        private String type; // government / private
        private Address address;
        private String collegeLogoUrl;
        private String collegeBannerUrl;
        private String website;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String country;
        private String pincode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private String primaryEmail;
        private String secondaryEmail;
        private String phone;
        private String alternatePhone;
        private String officialDomain;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminAccount {
        private String email;
        private String password; // hashed
        private String name;
        private String designation;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationDetails {
        private VerificationStatus status; // PENDING, VERIFIED, REJECTED
        private Date verificationDate;
        private String verifiedBy;
        private String rejectionReason;
        private Badge badge;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Badge {
        private String type;
        private Date issuedDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollegeDocument {
        private String type; // license, accreditation, registration, etc.
        private String fileUrl;
        private Date uploadDate;
        private String status; // pending, verified, rejected
        private String verificationNotes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialMedia {
        private String platform;  // e.g., "LinkedIn", "Instagram"
        private String url;       // e.g., "https://linkedin.com/college-page"
    }
}
