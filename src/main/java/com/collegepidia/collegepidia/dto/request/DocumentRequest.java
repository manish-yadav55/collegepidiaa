// File: dto/request/DocumentRequest.java
package com.collegepidia.collegepidia.dto.request;

import lombok.Data;

@Data
public class DocumentRequest {
    private String type;  // license/accreditation/registration
    private String fileUrl;
    private String status; // pending/verified/rejected
    private String verificationNotes;
}
