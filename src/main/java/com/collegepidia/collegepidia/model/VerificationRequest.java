// File: model/VerificationRequest.java
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
@Document(collection = "verification_requests")
public class VerificationRequest {

    @Id
    private String id;

    private String collegeId;
    private Date requestDate;
    private String status; // pending, approved, rejected
    private String reviewNotes;
    private String reviewerId;
    private List<RequestDocument> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDocument {
        private String documentId;
        private String status; // pending, approved, rejected
        private String notes;
    }
}
